package com.chen1335.ultimateEnchantment.enchantment.enchantments;

import com.chen1335.ultimateEnchantment.enchantment.EnchantmentBasic;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;

public class Eternal extends EnchantmentBasic {
    public Eternal() {
        super("eternal");
        supported_items = new Type.TagType<>(ItemTags.DURABILITY_ENCHANTABLE);
        max_cost = new Enchantment.Cost(200, 0);
        min_cost = new Enchantment.Cost(200, 0);
        slots = List.of();
        max_level = 1;
        weight = 1;
    }
}
