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

public class QuickShooting extends EnchantmentBasic {
    public static final Formula DRAW_SPEED = new Formula("0.05*lvl");

    public QuickShooting() {
        super("quick_shooting", "apothic_enchanting");
        supported_items = new Type.TagType<>(ItemTags.BOW_ENCHANTABLE);
        max_cost = new Enchantment.Cost(90, 10);
        min_cost = new Enchantment.Cost(50, 10);
        slots = List.of(EquipmentSlotGroup.HAND);
        max_level = 5;
        weight = 2;
    }

    @Override
    protected void registerFormula(Map<String, Formula> formulas) {
        formulas.put("draw_speed", DRAW_SPEED);
    }

    @Override
    public List<MutableComponent> getDesc(int level) {
        return List.of(Component.translatable(getDescId(), DRAW_SPEED.toComponent(buildBindings(level), 100)).withStyle(ChatFormatting.GOLD));
    }
}
