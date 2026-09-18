package com.chen1335.ultimate_enchantment.enchantment.enchantments;

import com.chen1335.ultimate_enchantment.enchantment.EnchantmentBasic;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;

public class Smelting extends EnchantmentBasic {
    public Smelting() {
        super("smelting");
        supported_items = new Type.TagType<>(ItemTags.MINING_ENCHANTABLE);
        max_cost = new Enchantment.Cost(80, 0);
        min_cost = new Enchantment.Cost(15, 0);
        slots = List.of(EquipmentSlotGroup.ANY);
        max_level = 1;
        weight = 3;
    }
}
