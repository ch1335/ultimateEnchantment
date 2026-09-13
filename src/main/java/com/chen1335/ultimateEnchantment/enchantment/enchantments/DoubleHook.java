package com.chen1335.ultimateEnchantment.enchantment.enchantments;

import com.chen1335.ultimateEnchantment.common.Formula;
import com.chen1335.ultimateEnchantment.enchantment.EnchantmentBasic;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;
import java.util.Map;

public class DoubleHook extends EnchantmentBasic {
    public static final Formula CHANCE = new Formula("0.05*lvl");

    public DoubleHook() {
        super("double_hook");
        supported_items = new Type.TagType<>(ItemTags.FISHING_ENCHANTABLE);
        max_cost = new Enchantment.Cost(60, 10);
        min_cost = new Enchantment.Cost(20, 10);
        slots = List.of();
        max_level = 5;
        weight = 3;
    }

    @Override
    protected void registerFormula(Map<String, Formula> formulas) {
        formulas.put("chance", CHANCE);
    }

    @Override
    public List<MutableComponent> getDesc(int level) {
        return List.of(Component.translatable(getDescId(), CHANCE.toComponent(buildBindings(level), 100)).withStyle(ChatFormatting.GOLD));
    }
}
