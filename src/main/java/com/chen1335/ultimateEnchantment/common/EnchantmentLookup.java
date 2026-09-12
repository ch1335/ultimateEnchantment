package com.chen1335.ultimateEnchantment.common;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

/**
 * 附魔注册表 lookup 的缓存。
 * <p>
 * <b>为什么可以缓存</b>：{@code Registries.ENCHANTMENT} 属于 {@code RegistryLayer.WORLDGEN}
 * （见 {@code RegistryDataLoader#WORLDGEN_REGISTRIES}），而 {@code /reload} 走的
 * {@code ReloadableServerRegistries#reload} 只重建 {@code RegistryLayer.RELOADABLE}
 * 那一层，也就是 {@code LootDataType.values()} 里的 loot_table / predicate / item_modifier。
 * 换句话说附魔注册表在整个世界（或整条连接）的生命周期内始终是同一个对象，缓存不会拿到
 * 失效引用 —— 换存档不行，那会重新走 {@code WorldLoader#load}。
 * <p>
 * <b>为什么分两个槽位</b>：不是为了两端内容不同（内容是一致的），而是为了保持
 * {@link CommonHooks#resolveLookup} 的选择顺序 —— 服务端在跑就优先给服务端那份，
 * 单人游戏里客户端代码因此拿到的仍是服务端 lookup。若两个来源写进同一个槽，
 * 客户端登录时会把服务端那份挤掉，行为就和现在不一样了。
 * <p>
 * <b>为什么无条件覆盖</b>：退出存档时 {@code ServerLifecycleHooks} 先把
 * {@code currentServer} 置 null，而客户端的 {@code ClientLevel} 这时还活着，这个窗口里
 * 的解析会得到一个马上要作废的 lookup。若刷新写成「槽位为空才填」，它会被一直用到下个存档。
 */
public final class EnchantmentLookup {
    /**
     * 服务端槽位。单人游戏里两端同 JVM，这两个字段会被两个线程（服务端主线程、客户端渲染线程）
     * 触碰，所以是 volatile。
     */
    private static volatile HolderLookup.RegistryLookup<Enchantment> serverSide;
    /**
     * 客户端槽位。多人游戏里客户端没有本地服务端，只会用到这个。
     */
    private static volatile HolderLookup.RegistryLookup<Enchantment> clientSide;

    private EnchantmentLookup() {
    }

    /**
     * 世界加载完成时调用（{@code ServerStartedEvent}），无条件覆盖服务端槽位。
     */
    public static void refreshServer(HolderLookup.Provider provider) {
        serverSide = provider.lookupOrThrow(Registries.ENCHANTMENT);
    }

    /**
     * 客户端登录时调用（{@code ClientPlayerNetworkEvent.LoggingIn}），无条件覆盖客户端槽位。
     */
    public static void refreshClient(HolderLookup.Provider provider) {
        clientSide = provider.lookupOrThrow(Registries.ENCHANTMENT);
    }

    /**
     * 客户端断开时调用（{@code ClientPlayerNetworkEvent.LoggingOut}），避免重连到别的服务器时
     * 还拿着上一条连接的注册表。
     * <p>
     * 服务端槽位不用清：下次 {@code ServerStartedEvent} 会覆盖，而在这之前
     * {@code getCurrentServer()} 已经是 null，根本走不到那个分支。
     */
    public static void clearClient() {
        clientSide = null;
    }

    /**
     * 取附魔 lookup，端的优先级与 {@link CommonHooks#resolveLookup} 一致。
     * <p>
     * 槽位为空时回填，所以正常运行时每个槽位只解析一次。但兜底那条路径在客户端没加载世界时
     * 仍会返回 {@code null}（主菜单、数据生成），调用方必须处理 —— 见 {@link #get()}。
     */
    @Nullable
    public static HolderLookup.RegistryLookup<Enchantment> getOrNull() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            HolderLookup.RegistryLookup<Enchantment> local = serverSide;
            if (local == null) {
                local = server.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
                serverSide = local;
            }
            return local;
        }

        HolderLookup.RegistryLookup<Enchantment> local = clientSide;
        if (local == null) {
            local = CommonHooks.resolveLookup(Registries.ENCHANTMENT);
            clientSide = local;
        }
        return local;
    }

    /**
     * 同 {@link #getOrNull()}，但注册表确实不可用时直接抛，而不是让调用方在别处吃到 NPE。
     */
    public static HolderLookup.RegistryLookup<Enchantment> get() {
        HolderLookup.RegistryLookup<Enchantment> local = getOrNull();
        if (local == null) {
            throw new IllegalStateException("Enchantment registry is not available");
        }
        return local;
    }
}
