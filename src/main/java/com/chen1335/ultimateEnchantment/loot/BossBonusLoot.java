package com.chen1335.ultimateEnchantment.loot;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

/**
 * Boss 额外掉落。
 * <p>
 * 按原战利品表<b>额外摇取若干份</b>，把相当于 {@value #RATIO} 份的结果追加到第一次的结果里。
 * 之所以是「重新摇取再抽样」而不是「把第一次的结果复制一份」，是因为战利品表里的
 * 概率条目（{@code minecraft:alternatives}、带 {@code random_chance} 的 pool）每次摇取
 * 结果都不同 —— 重新摇取才符合「额外掉落」的直觉，也才让 {@code 25%} 这个数字有意义。
 * <p>
 * 第二次摇取复用同一个 {@link net.minecraft.util.RandomSource}（见
 * {@link LootContext.Builder#Builder(LootContext)}），所以世界种子推进是连续可复现的，
 * 没有引入额外的随机源。
 * <p>
 * <b>Boss 判定</b>走两条互不干扰的路径：原版与模组 Boss 用 NeoForge 约定的
 * {@link Tags.EntityTypes#BOSSES}（{@code c:bosses}）；神化 Boss 用它自己打的持久 NBT 标记。
 * 后者是字符串判断，因此本类<b>不引入对 Apotheosis 的编译期依赖</b>。
 * <p>
 * 调用点见 {@code mixins/ultimate_enchantment/LootTableMixin}。
 */
@ParametersAreNonnullByDefault
public final class BossBonusLoot {

    /**
     * 额外掉落的倍率，可以大于 1。
     * <p>
     * 结算方式是「整数部分整份全取 + 小数部分按比例抽样」：
     * <ul>
     *   <li>{@code 0.25} —— 多掉 25%</li>
     *   <li>{@code 1.5} —— 多掉一整份，外加半份的随机抽样</li>
     *   <li>{@code 2.3} —— 多掉两份，外加 30%</li>
     * </ul>
     * 小数部分的抽样先取整数个，余下的小数按概率补一件（与 {@code BlockDropsEvent}
     * 处理经验值的方式一致），这样物品基数很小时也不会被 {@code floor} 抹成 0。
     * <p>
     * 注意这个值直接决定额外摇取的次数（每次摇取都完整展开一遍战利品表），别设得太大。
     */
    private static final float RATIO = 0.25F;

    /** 神化给 Boss 打的持久 NBT 标记（{@code apoth.boss}），值恒为 true。 */
    private static final String APOTH_BOSS_KEY = "apoth.boss";

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

    private BossBonusLoot() {
    }

    /**
     * 若 {@code context} 对应的掉落来自 Boss，则追加额外掉落并返回。
     * <p>
     * {@code original} 与返回值是同一个列表对象（原地追加），返回值只为了方便调用方直接返回。
     *
     * @param table    正在产出战利品的表，用于与 {@code context} 携带的表 ID 交叉校验
     * @param original 第一次摇取的结果
     * @param context  该次摇取的上下文，携带 {@code THIS_ENTITY} 参数
     */
    public static ObjectArrayList<ItemStack> append(
            LootTable table,
            ObjectArrayList<ItemStack> original,
            LootContext context
    ) {
        // 二次摇取自己触发的回调：放行。见 SECOND_PASS 的说明。
        if (context.hasVisitedElement(SECOND_PASS)) {
            return original;
        }

        if (original.isEmpty()) {
            return original;
        }
        // context 的表 ID 由 CommonHooks#modifyLoot 写入，而对同一个 context
        // 它只写一次。这里 ID 对不上，说明这个 context 是被外层复用的
        // （比如嵌套的 loot table），此时不插手，避免张冠李戴。
        ResourceLocation lootTableId = context.getQueriedLootTableId();
        if (LootTableIdCondition.UNKNOWN_LOOT_TABLE.equals(lootTableId)
                || !lootTableId.equals(table.getLootTableId())) {
            return original;
        }

        // 最热的一行：方块、宝箱、钓鱼等绝大多数 loot table 都没有 THIS_ENTITY，
        // 在这里就返回了，底下 Boss 判定的开销不会被摊到它们身上。
        if (!(context.getParamOrNull(LootContextParams.THIS_ENTITY) instanceof LivingEntity entity)) {
            return original;
        }
        if (!isBoss(entity)) {
            return original;
        }

        ServerLevel level = context.getLevel();

        // ── 整数部分：整份摇取并全部收取 ──────────────────────────────────────
        // RATIO = 1.5 时摇 1 次，2.3 时摇 2 次。
        // 每次都用独立的 context，但共享同一个 RandomSource，随机序列连续推进，
        // 因此几份战利品不会摇出完全相同的内容。
        for (int pass = Mth.floor(RATIO); pass > 0; pass--) {
            table.getRandomItems(secondaryContext(context), original::add);
        }

        // ── 小数部分：再摇一份，拆成单件后按剩余比例取前 N 个 ────────────────
        float remainder = Mth.frac(0.9F);
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

            float expected = singles.size() * remainder;
            int keep = Mth.floor(expected);
            float fraction = Mth.frac(expected);
            if (fraction > 0.0F && level.getRandom().nextFloat() < fraction) {
                keep++;
            }
            if (keep > 0) {
                // 打乱后再取前 keep 个，避免总是偏向战利品表里靠前的条目
                Util.shuffle(singles, level.getRandom());
                original.addAll(singles.subList(0, keep));
            }
        }

        return original;
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

    /**
     * Boss 判定：原版与模组 Boss 走 {@code c:bosses} tag，神化 Boss 走它自己的持久 NBT 标记。
     */
    private static boolean isBoss(LivingEntity entity) {
        return entity.getType().is(Tags.EntityTypes.BOSSES)
                || entity.getPersistentData().getBoolean(APOTH_BOSS_KEY);
    }
}
