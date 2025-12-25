package com.chen1335.ultimateEnchantment.utils;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.CommonHooks;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ItemEnchantmentHelper {
    public static int getEnchantmentLevel(ItemStack itemStack, ResourceKey<Enchantment> enchantmentResourceKey) {
        Optional<Holder.Reference<Enchantment>> optionalHolder = Objects.requireNonNull(CommonHooks.resolveLookup(Registries.ENCHANTMENT)).get(enchantmentResourceKey);
        return optionalHolder.map(itemStack::getEnchantmentLevel).orElse(0);
    }

    public static void runIfItemStackHaveEnchant(ItemStack itemStack, Holder<Enchantment> holder, Consumer<Integer> consumer) {
        int i = itemStack.getEnchantmentLevel(holder);
        if (i > 0) {
            consumer.accept(i);
        }
    }

    public static <T> void runIfItemStackHaveEnchantComponent(ItemStack itemStack, Supplier<DataComponentType<T>> supplier, BiConsumer<T, Integer> biConsumer) {
        for (Object2IntMap.Entry<Holder<Enchantment>> holderEntry : itemStack.getAllEnchantments(Objects.requireNonNull(CommonHooks.resolveLookup(Registries.ENCHANTMENT))).entrySet()) {
            T component = holderEntry.getKey().value().effects().get(supplier.get());
            if (component != null) {
                biConsumer.accept(component, holderEntry.getIntValue());
                return;
            }
        }
    }
}
