package com.chen1335.ultimateEnchantment.enchantment.enchantments;

import com.chen1335.ultimateEnchantment.common.Formula;
import com.chen1335.ultimateEnchantment.enchantment.EnchantmentBasic;
import com.chen1335.ultimateEnchantment.tags.UEEnchantmentTags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;
import java.util.Map;

public class LastStand extends EnchantmentBasic {
    public static final Formula HEALTH_THRESHOLD = new Formula("0.4");
    public static final Formula ARMOR_BONUS = new Formula("0.05*lvl");

    public LastStand() {
        super("last_stand");
        supported_items = new Type.TagType<>(ItemTags.ARMOR_ENCHANTABLE);
        exclusive_set = new Type.TagType<>(UEEnchantmentTags.ULTIMATE_ENCHANTMENT_EXCLUSIVE);
        max_cost = new Enchantment.Cost(200, 0);
        min_cost = new Enchantment.Cost(200, 0);
        slots = List.of(EquipmentSlotGroup.ARMOR);
        max_level = 5;
        weight = 1;
    }

    @Override
    protected void registerFormula(Map<String, Formula> formulas) {
        formulas.put("health_threshold", HEALTH_THRESHOLD);
        formulas.put("armor_bonus", ARMOR_BONUS);
    }

    @Override
    public MutableComponent getDesc(int level) {
        return Component.translatable(getDescId(),HEALTH_THRESHOLD.toComponent(buildBindings(level), 100),ARMOR_BONUS.toComponent(buildBindings(level), 100)).withStyle(ChatFormatting.LIGHT_PURPLE);
    }
}
