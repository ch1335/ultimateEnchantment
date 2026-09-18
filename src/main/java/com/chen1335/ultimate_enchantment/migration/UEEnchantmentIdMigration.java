package com.chen1335.ultimate_enchantment.migration;

import com.chen1335.ultimate_enchantment.UltimateEnchantment;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 附魔注册 ID 迁移。
 * <p>
 * 附魔是 datapack registry，条目以 <b>字符串 ID</b> 的形式写进 NBT（如 {@code minecraft:enchantments} 组件的
 * {@code levels} 映射、{@code minecraft:stored_enchantments}）。一旦改了附魔的注册 ID，旧存档里记录的旧 ID
 * 就再也查不到了。
 * <p>
 * 更糟的是 {@code RegistryFixedCodec#decode} 在查不到 ID 时返回的是
 * {@code DataResult.error("Failed to get element ...")} —— <b>硬失败</b>，而不是静默跳过。
 * 这意味着整个 {@code minecraft:enchantments} 组件解析失败，连带物品本身一起损坏（变成空气）。
 * <p>
 * 本类通过在 codec 反序列化路径上包装 {@link HolderGetter} 来解决这个问题：任何对旧 ID 的查找都会被
 * 重定向到新 ID 的 {@link Holder.Reference}。由于物品在内存中持有的是新 ID 的 Holder，
 * 当该物品被<b>重新序列化</b>（玩家退出保存 playerdata、区块保存容器、末影箱等）时，
 * NBT 里写的就已经是新 ID —— <b>迁移是自动且惰性的，无需扫描存档</b>。
 * <p>
 * 注入点见 {@code mixins/ultimate_enchantment/RegistryOpsMixin}。
 * <p>
 * <b>迁移步骤</b>：
 * <ol>
 *   <li>在 {@link #createRenames()} 里按模板登记 旧 ID → 新 ID</li>
 *   <li>把附魔 Java 类里的 {@code super("旧id")} 改成 {@code super("新id")}，
 *       配置文件 {@code config/ultimate_enchantment/enchantments/旧id.json} 改名为 {@code 新id.json}</li>
 *   <li>启动游戏，让玩家正常进出一次（物品会自动重写成新 ID）</li>
 *   <li>确认所有存档都已迁移后，删除迁移表条目 —— 本类会自动退化为零开销的空操作</li>
 * </ol>
 *
 * @see <a href="https://minecraft.wiki/w/Data_version">Data version</a>
 */
public final class UEEnchantmentIdMigration {

    private UEEnchantmentIdMigration() {
    }

    // ══════════════════════════════════════════════════════════════════════════════
    //  迁移表：旧附魔 ID → 新附魔 ID（只写路径部分，命名空间固定为本模组）
    // ══════════════════════════════════════════════════════════════════════════════

    private static final Map<Identifier, Identifier> RENAMES = createRenames();

    private static Map<Identifier, Identifier> createRenames() {
        Map<Identifier, Identifier> renames = new HashMap<>();
        // ─────────────────────────────────────────────────────────────────────────
        //  迁移模板：每改一个附魔 ID，在这里登记一行；改完确认迁移完成后删掉即可。
        //
        //       rename(renames, "旧路径", "新路径");
        //
        //  例如把 tear 的注册 ID 从 tear 改成了 tear_new：
        //       rename(renames, "tear", "tear_new");
        // ─────────────────────────────────────────────────────────────────────────

        return Map.copyOf(renames);
    }

    /**
     * 登记一条迁移。不支持链式迁移（A→B→C 只会解析一次，直接命中 A→B），避免写出环导致死循环。
     */
    private static void rename(Map<Identifier, Identifier> renames, String oldPath, String newPath) {
        Identifier oldId = UltimateEnchantment.id(oldPath);
        Identifier newId = UltimateEnchantment.id(newPath);
        if (oldId.equals(newId)) {
            throw new IllegalArgumentException("Enchantment ID migration: old and new ID are identical: " + oldId);
        }
        Identifier previous = renames.put(oldId, newId);
        if (previous != null) {
            throw new IllegalArgumentException("Enchantment ID migration: duplicate legacy ID: " + oldId);
        }
    }

    /**
     * 是否有生效中的迁移。为 {@code false} 时所有入口都是零开销的空操作。
     */
    public static boolean isMigrating() {
        return !RENAMES.isEmpty();
    }

    /**
     * 解析附魔 ID：命中迁移表返回新 ID，否则原样返回。
     */
    public static Identifier redirect(Identifier id) {
        return RENAMES.getOrDefault(id, id);
    }

    /**
     * 解析附魔 {@link ResourceKey}：命中迁移表返回新 key，否则原样返回。
     */
    public static <E> ResourceKey<E> redirect(ResourceKey<E> key) {
        Identifier renamed = RENAMES.get(key.identifier());
        if (renamed == null) {
            return key;
        }
        if (REPORTED.add(key.identifier())) {
            UltimateEnchantment.LOGGER.info("[Enchantment Migration] Redirecting legacy ID {} -> {}", key.identifier(), renamed);
        }
        return ResourceKey.create(key.registryKey(), renamed);
    }

    /**
     * 包装附魔注册表的 {@link HolderGetter}。
     * <p>
     * 只在「迁移表非空」且「目标注册表是附魔」时才包装，其余情况原样放行。
     *
     * @param original    {@code RegistryOps#getter} 原本的返回值
     * @param registryKey 被查询的注册表 key
     */
    public static Optional<HolderGetter<?>> wrap(
            Optional<HolderGetter<?>> original,
            ResourceKey<? extends Registry<?>> registryKey
    ) {
        if (RENAMES.isEmpty() || !Registries.ENCHANTMENT.equals(registryKey)) {
            return original;
        }
        if (ANNOUNCED.compareAndSet(false, true)) {
            UltimateEnchantment.LOGGER.info("[Enchantment Migration] Enabled with {} legacy ID mapping(s): {}", RENAMES.size(), RENAMES);
        }
        return original.map(UEEnchantmentIdMigration::wrapLookup);
    }

    /**
     * {@code RegistryOps#getter} 在每次解析带附魔的物品时都会被调用，因此这里缓存包装对象，
     * 避免同一注册表的 lookup 被反复包装。注册表 lookup 实例在整个进程内是稳定的，
     * 且数量固定（每个注册表一个），不会造成泄漏。
     */
    private static final Map<HolderLookup.RegistryLookup<?>, HolderGetter<?>> LOOKUP_CACHE = new ConcurrentHashMap<>();

    private static final Set<Identifier> REPORTED = ConcurrentHashMap.newKeySet();

    private static final AtomicBoolean ANNOUNCED = new AtomicBoolean();

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static HolderGetter<?> wrapLookup(HolderGetter<?> getter) {
        // 附魔注册表的 getter 必然是 RegistryLookup；万一不是，保守放行，不改变原有行为
        if (!(getter instanceof HolderLookup.RegistryLookup<?> lookup)) {
            return getter;
        }
        return LOOKUP_CACHE.computeIfAbsent(lookup, RedirectingRegistryLookup::new);
    }

    /**
         * 只覆盖 {@link #get(ResourceKey)}，其余方法（{@code listElements}、{@code listTags}、
         * {@code key}、{@code getData} 等）全部沿用 {@link Delegate} 的
         * 默认委托实现，因此包装后依旧保有 {@code RegistryLookup} 类型，
         * 不会破坏依赖该类型的 codec（如 {@code RegistryOps#retrieveRegistryLookup}）。
         */
        private record RedirectingRegistryLookup<E>(
            RegistryLookup<E> parent) implements HolderLookup.RegistryLookup.Delegate<E> {

        @Override
            public Optional<Holder.Reference<E>> get(ResourceKey<E> key) {
                return this.parent.get(UEEnchantmentIdMigration.redirect(key));
            }

            @Override
            public boolean canSerializeIn(HolderOwner<E> owner) {
                return this.parent.canSerializeIn(owner);
            }
        }
}
