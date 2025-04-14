package com.chen1335.ultimateEnchantment.utils;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.CommonHooks;

import java.util.Objects;

public class ItemEnchantmentHelper {
    public static int getEnchantmentLevel(ItemStack itemStack, ResourceKey<Enchantment> enchantmentResourceKey) {
        return itemStack.getEnchantmentLevel(Objects.requireNonNull(CommonHooks.resolveLookup(Registries.ENCHANTMENT)).getOrThrow(enchantmentResourceKey));
    }
}
