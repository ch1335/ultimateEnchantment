package com.chen1335.ultimateEnchantment.enchantment.enchantments;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.enchantment.EnchantmentBasic;
import com.chen1335.ultimateEnchantment.tags.UEEnchantmentTags;
import net.minecraft.core.NonNullList;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;

import java.util.List;


public class TheFortress extends EnchantmentBasic {
    public TheFortress(String name) {
        super("the_fortress");
        supported_items = new Type.TagType<>(ItemTags.ARMOR_ENCHANTABLE);
        primary_items = supported_items;
        exclusive_set = new Type.TagType<>(UEEnchantmentTags.ULTIMATE_ENCHANTMENT_EXCLUSIVE);
        max_cost = new Enchantment.Cost(200, 0);
        min_cost = new Enchantment.Cost(200, 0);
        slots = List.of(EquipmentSlotGroup.ARMOR);
        max_level = 5;
        weight = 1;
    }

    @EventBusSubscriber(modid = UltimateEnchantment.MODID)
    public static class Handler {
        public final NonNullList<ItemStack> armor = NonNullList.withSize(4, ItemStack.EMPTY);

        public void tick(LivingEntity owner) {

        }

        public static void LivingEquipmentChangeEvent(LivingEquipmentChangeEvent event) {

        }
    }
}
