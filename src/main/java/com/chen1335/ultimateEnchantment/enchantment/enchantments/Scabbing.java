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

public class Scabbing extends EnchantmentBasic {
    public static final Formula ARMOR_SHRED = new Formula("0.05*lvl");

    public Scabbing() {
        super("scabbing", "apothic_enchanting");
        supported_items = new Type.TagType<>(ItemTags.WEAPON_ENCHANTABLE);
        max_cost = new Enchantment.Cost(90, 10);
        min_cost = new Enchantment.Cost(50, 10);
        slots = List.of(EquipmentSlotGroup.HAND);
        max_level = 5;
        weight = 2;
    }

    @Override
    protected void registerFormula(Map<String, Formula> formulas) {
        formulas.put("armor_shred", ARMOR_SHRED);
    }

    @Override
    public MutableComponent getDesc(int level) {
        return Component.translatable(getDescId(), ARMOR_SHRED.toComponent(buildBindings(level), 100));
    }
}
