package com.chen1335.ultimate_enchantment.enchantment.enchantments;

import com.chen1335.ultimate_enchantment.common.Formula;
import com.chen1335.ultimate_enchantment.enchantment.EnchantmentBasic;
import com.chen1335.ultimate_enchantment.enchantment.UEEnchantments;
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

public class Harvest extends EnchantmentBasic {
    public static final Formula DROP_BONUS = new Formula("0.15*lvl");

    public Harvest() {
        super("harvest");
        supported_items = new Type.TagType<>(ItemTags.HOES);
        primary_items = supported_items;
        max_cost = new Enchantment.Cost(90, 10);
        min_cost = new Enchantment.Cost(25, 12);
        slots = List.of(EquipmentSlotGroup.HAND);
        max_level = 5;
        weight = 2;
    }


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
            return 0.0F;
        }
        if (!(context.getOptionalParameter(LootContextParams.TOOL) instanceof ItemStack tool)) {
            return 0.0F;
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
