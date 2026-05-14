package com.chen1335.ultimateEnchantment.utils;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.CommonHooks;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ItemEnchantmentHelper {
    public static int getEnchantmentLevel(ItemStack itemStack, ResourceKey<Enchantment> enchantmentResourceKey) {
        HolderLookup.RegistryLookup<Enchantment> lookup = CommonHooks.resolveLookup(Registries.ENCHANTMENT);
        if (lookup == null) {
            return 0;
        }
        Optional<Holder.Reference<Enchantment>> optionalHolder = lookup.get(enchantmentResourceKey);
        return optionalHolder.map(itemStack::getEnchantmentLevel).orElse(0);
    }

    public static void runIfItemStackHaveEnchant(ItemStack itemStack, Holder<Enchantment> holder, Consumer<Integer> consumer) {
        int i = itemStack.getEnchantmentLevel(holder);
        if (i > 0) {
            consumer.accept(i);
        }
    }

    public static <T> void runIfItemStackHaveEnchantComponent(ItemStack itemStack, Supplier<DataComponentType<T>> supplier, BiConsumer<T, Integer> biConsumer) {
        HolderLookup.RegistryLookup<Enchantment> lookup = CommonHooks.resolveLookup(Registries.ENCHANTMENT);
        if (lookup == null) {
            return;
        }
        for (Object2IntMap.Entry<Holder<Enchantment>> holderEntry : itemStack.getAllEnchantments(lookup).entrySet()) {
            T component = holderEntry.getKey().value().effects().get(supplier.get());
            if (component != null) {
                biConsumer.accept(component, holderEntry.getIntValue());
                return;
            }
        }
    }
}
