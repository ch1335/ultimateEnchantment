package com.chen1335.ultimateEnchantment.enchantment.enchantments;

import com.chen1335.ultimateEnchantment.common.Formula;
import com.chen1335.ultimateEnchantment.enchantment.EnchantmentBasic;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;
import java.util.Map;

public class LifeSteal extends EnchantmentBasic {
    public static final Formula HEAL_PERCENT = new Formula("(3+lvl)*0.01");
    public static final Formula MAX_PERCENT = new Formula("Math.min((4+lvl),8)*0.01");

    public LifeSteal() {
        super("life_steal");
        supported_items = new Type.TagType<>(ItemTags.WEAPON_ENCHANTABLE);
        primary_items = new Type.TagType<>(ItemTags.SWORD_ENCHANTABLE);
        exclusive_set = new Type.TagType<>(com.chen1335.ultimateEnchantment.tags.UEEnchantmentTags.LIFE_STEAL_ENCHANTMENT);
        max_cost = new Enchantment.Cost(65, 9);
        min_cost = new Enchantment.Cost(15, 8);
        slots = List.of(EquipmentSlotGroup.HAND);
        max_level = 4;
        weight = 4;
    }

    @Override
    protected void registerFormula(Map<String, Formula> formulas) {
        formulas.put("heal_percent", HEAL_PERCENT);
        formulas.put("max_percent", MAX_PERCENT);
    }

    @Override
    public MutableComponent getDesc(int level) {
        return Component.translatable(getDescId(),
                HEAL_PERCENT.toComponent(buildBindings(level), 100),
                MAX_PERCENT.toComponent(buildBindings(level), 100));
    }
}
