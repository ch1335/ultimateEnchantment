package com.chen1335.ultimateEnchantment.enchantment.enchantments;

import com.chen1335.ultimateEnchantment.common.Formula;
import com.chen1335.ultimateEnchantment.enchantment.EnchantmentBasic;
import com.chen1335.ultimateEnchantment.tags.UEEnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;
import java.util.Map;

public class Vanquisher extends EnchantmentBasic {
    public static final Formula BUFF_DURATION = new Formula("400");
    public static final Formula CHARGE_THRESHOLD = new Formula("0.5");
    public static final Formula MAX_STACKS = new Formula("10");
    public static final Formula DAMAGE_PER_STACK = new Formula("0.05");
    public static final Formula SPEED_PER_STACK = new Formula("0.1");
    public static final Formula ACTIVE_DAMAGE = new Formula("0.5");
    public static final Formula ACTIVE_SPEED = new Formula("1");

    public Vanquisher() {
        super("vanquisher");
        supported_items = new Type.TagType<>(ItemTags.SHARP_WEAPON_ENCHANTABLE);
        primary_items = new Type.TagType<>(ItemTags.SWORD_ENCHANTABLE);
        exclusive_set = new Type.TagType<>(UEEnchantmentTags.ULTIMATE_ENCHANTMENT_EXCLUSIVE);
        max_cost = new Enchantment.Cost(999, 0);
        min_cost = new Enchantment.Cost(100, 0);
        slots = List.of(EquipmentSlotGroup.HAND);
        max_level = 1;
        weight = 1;
    }

    @Override
    protected void registerFormula(Map<String, Formula> formulas) {
        formulas.put("buff_duration", BUFF_DURATION);
        formulas.put("charge_threshold", CHARGE_THRESHOLD);
        formulas.put("max_stacks", MAX_STACKS);
        formulas.put("damage_per_stack", DAMAGE_PER_STACK);
        formulas.put("speed_per_stack", SPEED_PER_STACK);
        formulas.put("active_damage", ACTIVE_DAMAGE);
        formulas.put("active_speed", ACTIVE_SPEED);
    }
}
