package com.chen1335.ultimate_enchantment.loot;

import com.chen1335.ultimate_enchantment.enchantment.enchantments.Harvest;
import com.chen1335.ultimate_enchantment.enchantment.enchantments.UltimateSlayer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.Util;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * 额外掉落。
 * <p>
 * 按原战利品表<b>额外摇取若干份</b>，份数由倍率决定 —— Boss 掉落看击杀者
 * （{@link UltimateSlayer#getRatio}），作物掉落看工具上的「收获」附魔
 * （{@link Harvest#getRatio}）—— 结果追加到第一次的结果里。之所以是「重新摇取再抽样」而不是「把第一次的结果复制一份」，
 * 是因为战利品表里的概率条目（{@code minecraft:alternatives}、带 {@code random_chance}
 * 的 pool）每次摇取结果都不同 —— 重新摇取才符合「额外掉落」的直觉，
 * 也才让 {@code 25%} 这个数字有意义。
 * <p>
 * 第二次摇取复用同一个 {@link net.minecraft.util.RandomSource}（见
 * {@link LootContext.Builder#Builder(LootContext)}），所以世界种子推进是连续可复现的，
 * 没有引入额外的随机源。
 * <p>
 * <b>Boss 判定</b>走两条互不干扰的路径：原版与模组 Boss 用 NeoForge 约定的
 * {@link Tags.EntityTypes#BOSSES}（{@code c:bosses}）；神化 Boss 用它自己打的持久 NBT 标记。
 * 后者是字符串判断，因此本类<b>不引入对 Apotheosis 的编译期依赖</b>。
 * <p>
 * <p>
 * 本类只负责<b>战利品表</b>这条路径，调用点见
 * {@code mixins/ultimate_enchantment/LootTableMixin}。实体与<b>方块（成熟作物）</b>两种
 * 来源都经由它 —— 方块掉落本身就是一次 {@code LootTable#getRandomItems}，在战利品表
 * 这一层和实体掉落是同一件事。<b>神化 Boss 直接穿在身上的装备</b>
 * 不经过战利品表（见 {@code ApothBossEquipmentLoot} 的说明），那条路径单独实现，
 * 这样本类得以保持对 Apotheosis 零编译期依赖。
 */
public final class BonusLoot {
    /**
     * 额外掉落倍率的硬上限。
     */
    private static final float MAX_RATIO = 20.0F;

    /**
     * 二次摇取的重入哨兵，挂在 {@link LootContext#pushVisitedElement} 的展开栈上。
     * <p>
     * 二次摇取走的是非 Raw 版本（必须如此，否则全局战利品修改器不生效），而它内部会经过
     * 本 mixin 的注入点，于是又回调到 {@link #append} —— 靠这个哨兵放行，避免无限递归。
     * <p>
     * value 选 {@link LootTable#EMPTY} 是刻意的：它是 {@code LootDataType.TABLE} 的
     * defaultValue，只在「条件为假」时充当占位符，<b>永远不会作为真实战利品表被展开</b>，
     * 因此这个条目不会与任何一次真实的 push 相撞。同理也不能拿 {@code table} 自己当 value ——
     * 那会让 {@code getRandomItemsRaw} 里的 {@code pushVisitedElement} 失败，
     * 直接走「检测到无限循环」分支返回空列表。
     */
    private static final LootContext.VisitedEntry<LootTable> SECOND_PASS =
            LootContext.createVisitedEntry(LootTable.EMPTY);

    /**
     * 按 {@code context} 的来源追加额外掉落：方块看是不是成熟作物，实体看击杀者。
     * <p>
     * {@code original} 与返回值是同一个列表对象（原地追加），返回值只为了方便调用方直接返回。
     *
     * @param table    正在产出战利品的表，用于与 {@code context} 携带的表 ID 交叉校验
     * @param original 第一次摇取的结果
     * @param context  该次摇取的上下文；方块来源带 {@code BLOCK_STATE} 与 {@code TOOL}，
     *                 实体来源带 {@code THIS_ENTITY} 与 {@code ATTACKING_ENTITY}
     */
    public static ObjectArrayList<ItemStack> append(
            LootTable table,
            ObjectArrayList<ItemStack> original,
            LootContext context
    ) {
        if (context.hasVisitedElement(SECOND_PASS)) {
            return original;
        }

        if (original.isEmpty()) {
            return original;
        }
        Identifier lootTableId = context.getQueriedLootTableId();
        if (LootTableIdCondition.UNKNOWN_LOOT_TABLE.equals(lootTableId)
                || !lootTableId.equals(table.getLootTableId())) {
            return original;
        }

        float ratio = 0;
        LivingEntity thisEntity = null;
        LivingEntity killer = null;
        BlockState blockState = null;

        if (context.getOptionalParameter(LootContextParams.BLOCK_STATE) instanceof BlockState state) {
            blockState = state;
        }

        if (context.getOptionalParameter(LootContextParams.THIS_ENTITY) instanceof LivingEntity entity) {
            thisEntity = entity;
        }
        if (context.getOptionalParameter(LootContextParams.ATTACKING_ENTITY) instanceof LivingEntity entity) {
            killer = entity;
        }


        if (blockState != null) {
            ratio += Harvest.getRatio(context, blockState);
        }
        if (thisEntity != null && killer != null) {
            ratio += UltimateSlayer.getRatio(thisEntity, killer);
        }


        addExtraRolls(table, original, context, Math.clamp(0, ratio, MAX_RATIO));
        return original;
    }


    /**
     * 按 {@code ratio} 往 {@code original} 里追加若干份额外摇取。
     */
    private static void addExtraRolls(
            LootTable table,
            ObjectArrayList<ItemStack> original,
            LootContext context,
            float ratio
    ) {
        // ── 整数部分：整份摇取并全部收取 ──────────────────────────────────────
        // ratio = 1.5 时摇 1 次，2.3 时摇 2 次。
        // 每次都用独立的 context，但共享同一个 RandomSource，随机序列连续推进，
        // 因此几份战利品不会摇出完全相同的内容。
        for (int pass = Mth.floor(ratio); pass > 0; pass--) {
            table.getRandomItems(secondaryContext(context), original::add);
        }

        // ── 小数部分：再摇一份，拆成单件后按剩余比例取前 N 个 ────────────────
        float remainder = Mth.frac(ratio);
        if (remainder > 0.0F) {
            ObjectArrayList<ItemStack> draw = new ObjectArrayList<>();
            table.getRandomItems(secondaryContext(context), draw::add);

            // 抽样前把堆叠拆成单件。口径应当是「物品个数」而不是「条目数」：
            // 否则 10 个铁锭堆成一格只算一份，反而比 10 个各占一格的表掉得少。
            ObjectArrayList<ItemStack> singles = new ObjectArrayList<>();
            for (ItemStack stack : draw) {
                int count = stack.getCount();
                for (int i = 0; i < count; i++) {
                    singles.add(stack.copyWithCount(1));
                }
            }

            sampleInto(singles, remainder, context.getLevel().getRandom(), original::add);
        }
    }

    /**
     * 按 {@code ratio} 算出该额外产出多少份，基数由调用方给定。
     * @param unitCount 本次实际掉了多少份，作为倍率的基数
     * @param ratio     额外倍率
     */
    public static int rollCount(int unitCount, float ratio, RandomSource random) {
        float expected = unitCount * ratio;
        int count = Mth.floor(expected);
        float fraction = Mth.frac(expected);
        if (fraction > 0.0F && random.nextFloat() < fraction) {
            count++;
        }
        return count;
    }

    /**
     * 从 {@code pool} 里按 {@code ratio} 抽样，把抽中的元素交给 {@code sink}。
     * <p>
     * 抽取个数由 {@link #rollCount} 算出，并以 {@code pool} 的大小为上限
     * （{@code ratio > 1} 时不会重复抽同一件）。
     * <p>
     * 抽样前会打乱 {@code pool}，避免总是偏向靠前的条目。
     */
    private static void sampleInto(
            ObjectArrayList<ItemStack> pool,
            float ratio,
            RandomSource random,
            Consumer<ItemStack> sink
    ) {
        int keep = rollCount(pool.size(), ratio, random);
        if (keep <= 0) {
            return;
        }

        Util.shuffle(pool, random);
        for (ItemStack stack : pool.subList(0, Math.min(keep, pool.size()))) {
            sink.accept(stack);
        }
    }

    /**
     * 造一个用于额外摇取的 context。
     * <p>
     * <b>每次摇取都必须新建一个</b>：{@code visitedElements} 是整次摇取共享的展开栈，
     * 复用会让 {@code getRandomItemsRaw} 里的 {@code pushVisitedElement} 误判成
     * 「检测到战利品表无限循环」并静默返回空列表。挂上去的 {@link #SECOND_PASS} 哨兵
     * 也限定了「一份 context 只服务一次额外摇取」，复用还会让第二次摇取被直接放行。
     * <p>
     * {@code Builder(context)} 会复制 params/random/queriedLootTableId，所以
     * {@code THIS_ENTITY} 等参数完整保留，随机数也在同一个
     * {@link net.minecraft.util.RandomSource} 上连续推进。
     * <p>
     * 用非 Raw 版本摇取是必须的：它内部经过 {@code CommonHooks#modifyLoot}，
     * 全局战利品修改器才会生效（原版在 {@code getRandomItemsRaw} 上标
     * {@code @Deprecated} 就是这个原因）。该重载还会自动套上 {@code createStackSplitter}
     * 的堆叠上限处理，不必手动再套一层。
     */
    private static LootContext secondaryContext(LootContext context) {
        LootContext fresh = new LootContext.Builder(context).create(Optional.empty());
        fresh.pushVisitedElement(SECOND_PASS);
        return fresh;
    }
}
