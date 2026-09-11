package com.chen1335.ultimateEnchantment.enchantment.enchantments;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.common.Formula;
import com.chen1335.ultimateEnchantment.enchantment.EnchantmentBasic;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;
import java.util.Map;

public class OverGrow extends EnchantmentBasic {
    public static final ResourceLocation MODIFIER_ID = UltimateEnchantment.id("over_grow");

    public static final Formula HEALTH_BONUS = new Formula("0.02*lvl");

    public OverGrow() {
        super("over_grow");
        supported_items = new Type.TagType<>(ItemTags.ARMOR_ENCHANTABLE);
        max_cost = new Enchantment.Cost(80, 10);
        min_cost = new Enchantment.Cost(25, 5);
        slots = List.of(EquipmentSlotGroup.ARMOR);
        max_level = 5;
        weight = 2;
    }

    public static ResourceLocation makeId(String serializedName) {
        return MODIFIER_ID.withSuffix("/" + serializedName);
    }

    @Override
    protected void registerFormula(Map<String, Formula> formulas) {
        formulas.put("health_bonus", HEALTH_BONUS);
    }


    @Override
    public MutableComponent getDesc(int level) {
        return Component.translatable(getDescId(), HEALTH_BONUS.toComponent(buildBindings(level), 100));
    }
}
