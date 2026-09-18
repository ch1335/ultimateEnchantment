package com.chen1335.ultimate_enchantment.enchantment.enchantments;

import com.chen1335.ultimate_enchantment.common.Formula;
import com.chen1335.ultimate_enchantment.enchantment.EnchantmentBasic;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;
import java.util.Map;

public class QuickLatch extends EnchantmentBasic {
    public static final Formula POWER_THRESHOLD = new Formula("1");

    public QuickLatch() {
        super("quick_latch");
        supported_items = new Type.TagType<>(ItemTags.BOW_ENCHANTABLE);
        max_cost = new Enchantment.Cost(60, 0);
        min_cost = new Enchantment.Cost(20, 0);
        slots = List.of(EquipmentSlotGroup.HAND);
        max_level = 1;
        weight = 3;
    }

    @Override
    protected void registerFormula(Map<String, Formula> formulas) {
        formulas.put("power_threshold", POWER_THRESHOLD);
    }
}
