package com.chen1335.ultimateEnchantment.apotheosis;

import com.chen1335.ultimateEnchantment.apotheosis.attachmentDatas.ApothBossAttachmentTypes;
import com.chen1335.ultimateEnchantment.apotheosis.attachmentDatas.ApothBossInfo;
import com.chen1335.ultimateEnchantment.enchantment.enchantments.UltimateSlayer;
import com.chen1335.ultimateEnchantment.loot.BonusLoot;
import dev.shadowsoffire.apotheosis.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.loot.LootController;
import dev.shadowsoffire.apotheosis.loot.LootRarity;
import dev.shadowsoffire.apotheosis.tiers.GenContext;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;

/**
 * 神化 Boss 的额外装备掉落。
 * <p>
 * 神化 Boss 的装备不是战利品表产出的，而是 {@code Invader#initBoss} 用
 * {@code Mob#setItemSlot} 直接穿上去的，死亡时由 {@code Mob#dropCustomDeathLoot}
 * 掉落 —— 这条路径<b>完全不经过 {@code LootTable}</b>，所以
 * {@link BonusLoot#append} 那个注入点看不到它。宝石之所以一直有效，是因为它走
 * {@code BonusLootTables#drop}，内部仍然展开战利品表。
 * <p>
 * <b>本类直接引用神化 API</b>，因此调用方必须保证神化已加载 —— 由
 * {@code mixins/apotheosis/BossEquipmentDropMixin} 的加载条件兜住（mixin 子包名会
 * 被 {@code MainMixinPlugin} 拿去查 modid）。请不要从其它地方引用本类。
 * <p>
 * 额外掉落的是<b>重新生成的新装备</b>：物品类型、稀有度和词缀<b>全部重新随机</b> ——
 * 神化给的装备本来就带随机词缀，「额外掉一件」应当是一件新的，而不是原装备的副本。
 * 稀有度的随机范围取自<b>这个 Boss 自己能出的那几档</b>（{@code Invader#stats} 的 key
 * 集合），不沿用原装备、也不放开到全局，理由见 {@link #appendEquipment}。
 */
@ParametersAreNonnullByDefault
public final class ApothBossEquipmentLoot {

    /** 神化给 Boss 打的持久 NBT 标记（{@code apoth.boss}），值恒为 true。 */
    private static final String APOTH_BOSS_KEY = "apoth.boss";

    private ApothBossEquipmentLoot() {
    }

    public static void appendEquipment(
            LivingEntity entity,
            ObjectArrayList<ItemStack> droppedEquipment,
            @Nullable Player killer
    ) {
        if (droppedEquipment.isEmpty()) {
            return;
        }
        // 没有击杀者的死亡不参与 —— 与 BonusLoot#append 同一口径，那边在战利品表
        // 上下文里取不到玩家时也是直接返回。
        if (killer == null) {
            return;
        }
        if (!(entity.level() instanceof ServerLevel level)) {
            return;
        }
        if (!entity.getPersistentData().getBoolean(APOTH_BOSS_KEY)) {
            return;
        }

        ObjectArrayList<ItemStack> affixDrops = new ObjectArrayList<>();
        for (ItemStack stack : droppedEquipment) {
            if (AffixHelper.getRarity(stack).isBound()) {
                affixDrops.add(stack);
            }
        }
        if (affixDrops.isEmpty()) {
            return;
        }

        // 只读一次快照：上下文和稀有度范围都从同一份里取，免得中途状态变了取到两份不一致的。
        ApothBossInfo info = readInfo(entity);
        GenContext ctx = info != null ? info.toGenContext(level, entity) : fallbackGenContext(killer, level);
        Set<LootRarity> rarities = info != null ? info.rarities() : Set.of();

        RandomSource random = level.getRandom();
        float ratio = UltimateSlayer.ratioFor(killer);
        // 件数按 ratio 直接算，不能借 sampleInto —— 那个方法是从既有池子里挑，抽取量被
        // 池子大小卡住，而 Boss 身上就那几件装备，倍率一超就整段被吃掉。基数取「这次实际
        // 掉了几件神化装备」，与战利品表那边同口径：整数部分整件全给，小数部分按概率补一件。
        int count = BonusLoot.rollCount(affixDrops.size(), ratio, random);
        for (int i = 0; i < count; i++) {
            ItemStack fresh = createNewEquipment(ctx, rarities);
            if (!fresh.isEmpty()) {
                entity.spawnAtLocation(fresh);
            }
        }
    }

    /**
     * 生成一件全新的装备：<b>物品类型和词缀都是随机摇出来的</b>，稀有度在 {@code pool} 里摇
     * —— 那是这个 Boss 自己能出的那几档（{@code Invader#stats} 的 key 集合），所以额外掉的
     * 那件不会跑出 Boss 应有的档次范围。
     * <p>
     * 物品随机池是神化的 {@code AffixLootRegistry}，与 {@code Invader#initBoss} 生成 Boss 装备
     * 时走的是同一条路，掉出来的东西不会跑出神化自己的词缀物品范围。
     * <p>
     * {@code pool} 为空时（老存档，或该 Boss 的稀有度已被数据包删掉）传 {@code null}，退回神化
     * 原生的随机：优先从命中的 {@code AffixLootEntry#rarities()} 摇，条目没配则用全局池
     * （见 {@code TieredDynamicRegistry#getRandomItem} 对空池的处理），总之不会取不到值。
     */
    private static ItemStack createNewEquipment(GenContext ctx, Set<LootRarity> pool) {
        LootRarity rarity = pool.isEmpty() ? null : LootRarity.random(ctx, pool);
        return LootController.createRandomLootItem(ctx, rarity);
    }

    /**
     * 抄不到快照时的兜底上下文。
     * <p>
     * 正常流程走不到这里 —— 只有 Boss 是在装本模组之前生成的（实体上没挂这份 attachment）
     * 才会。退回击杀者的上下文（击杀者在入口就保证非空），总之不会因为取不到参数就不掉。
     */
    private static GenContext fallbackGenContext(Player killer, ServerLevel level) {
        return GenContext.forPlayer(level.getRandom(), killer);
    }

    /**
     * 把 Boss 生成时那份参数抄下来，等死亡时读回来。
     * <p>
     * 快照本身怎么落盘由 {@link ApothBossInfo} 负责；这里只负责挂上去。
     * 调用点见 {@code mixins/apotheosis/InvaderMixin}。
     *
     * @param rarities 该 Boss 能出的稀有度，即 {@code Invader#stats} 的 key 集合
     */
    public static void rememberGenContext(Mob mob, GenContext ctx, Set<LootRarity> rarities) {
        mob.setData(ApothBossAttachmentTypes.APOTH_BOSS_INFO.get(), new ApothBossInfo(ctx, rarities));
    }

    /**
     * 读 Boss 身上那份生成参数快照，没写过则返回 {@code null}。
     * <p>
     * 用 {@code getExistingDataOrNull} 而不是 {@code getData}：后者会给没写过的实体也挂一个
     * 默认对象上去，「没抄过」就识别不出来了，而这两种情况要走的流程不一样。
     */
    @Nullable
    private static ApothBossInfo readInfo(LivingEntity entity) {
        return entity.getExistingDataOrNull(ApothBossAttachmentTypes.APOTH_BOSS_INFO.get());
    }
}
