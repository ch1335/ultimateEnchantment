package com.chen1335.ultimateEnchantment.enchantment.enchantments;

import com.chen1335.ultimateEnchantment.common.Formula;
import com.chen1335.ultimateEnchantment.enchantment.EnchantmentBasic;
import com.chen1335.ultimateEnchantment.enchantment.UEEnchantments;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.List;
import java.util.Map;

/**
 * 收获：用锄破坏<b>成熟作物</b>时额外掉落。
 * <p>
 * 收益曲线是线性的 {@code 15% * lvl}（满级 5 级 75%）。这里刻意没用
 * {@link Scabbing} 那种复利递减 —— 那是为了让「单次命中」的收益不至于失控，
 * 而作物掉落的基数本来就大、单次收获的份数也有限，线性叠加读起来更直观。
 * <p>
 * 判定与追加掉落的实现都在 {@code loot.BonusLoot#appendHarvest}，本类只提供
 * 「单次收获的额外倍率」这一个数字，以及数据包可覆盖的公式。
 * 之所以能走战利品表：方块掉落本身就是一次 {@code LootTable#getRandomItems}，
 * 与 Boss 掉落共用 {@code LootTableMixin} 那个注入点。
 */
public class Harvest extends EnchantmentBasic {
    public static final Formula DROP_BONUS = new Formula("0.15*lvl");

    public Harvest() {
        super("harvest");
        supported_items = new Type.TagType<>(ItemTags.HOES);
        max_cost = new Enchantment.Cost(90, 10);
        min_cost = new Enchantment.Cost(25, 12);
        slots = List.of(EquipmentSlotGroup.HAND);
        max_level = 5;
        weight = 2;
    }

    /**
     * 读出 {@code tool} 上本附魔的等级并算成额外掉落倍率，没有附魔时返回 {@code 0}。
     * <p>
     * 只需要工具本身：方块掉落的 {@link net.minecraft.world.level.storage.loot.LootContext}
     * 一定带 {@code TOOL} 参数，但不保证带玩家实体，所以倍率的计算不依赖任何玩家状态。
     * <p>
     * 返回值不做上限钳制 —— 该由调用方按自己的场景决定（见
     * {@code BonusLoot#appendHarvest}），这里只挡住非有限值与负数：公式由数据包提供，
     * 写得出 {@code Infinity} 也写得出负号。
     */
    public static float ratioFor(ItemStack tool, ServerLevel level) {
        int lvl = UEEnchantments.HARVEST.getEnchantmentLevel(tool, level);
        if (lvl <= 0) {
            return 0.0F;
        }

        float ratio = DROP_BONUS.calculate(EnchantmentBasic.buildBindings(lvl));
        if (!Float.isFinite(ratio) || ratio <= 0.0F) {
            return 0.0F;
        }
        return ratio;
    }

    public static float getRatio(
            LootContext context,
            BlockState state
    ) {
        if (!(state.getBlock() instanceof CropBlock crop) || !crop.isMaxAge(state)) {
            return 0;
        }
        if (!(context.getParamOrNull(LootContextParams.TOOL) instanceof ItemStack tool)) {
            return 0;
        }

        return ratioFor(tool, context.getLevel());
    }

    @Override
    protected void registerFormula(Map<String, Formula> formulas) {
        formulas.put("drop_bonus", DROP_BONUS);
    }

    @Override
    public List<MutableComponent> getDesc(int level) {
        return List.of(Component.translatable(getDescId(), DROP_BONUS.toComponent(buildBindings(level), 100, 0)).withStyle(ChatFormatting.GOLD));
    }
}
