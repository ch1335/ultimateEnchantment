package com.chen1335.ultimate_enchantment.utils;

import com.chen1335.ultimate_enchantment.common.EnchantmentLookup;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface UEEnchantmentHelper {
    static Optional<Holder.Reference<Enchantment>> getEnchantment(ResourceKey<Enchantment> resourceKey) {
        HolderLookup.RegistryLookup<Enchantment> lookup = EnchantmentLookup.getOrNull();
        if (lookup == null) {
            return Optional.empty();
        }
        return lookup.get(resourceKey);
    }

    static Optional<Holder.Reference<Enchantment>> getEnchantment(HolderLookup.Provider provider, ResourceKey<Enchantment> resourceKey) {
        return provider.lookupOrThrow(Registries.ENCHANTMENT).get(resourceKey);
    }

    static void runIfEnchantmentExist(ResourceKey<Enchantment> resourceKey, Consumer<Holder<Enchantment>> consumer) {
        getEnchantment(resourceKey).ifPresent(consumer);
    }

    static <T> void runIfComponentExist(Holder<Enchantment> holder, Supplier<DataComponentType<T>> supplier, Consumer<T> consumer) {
        T dataComponent = holder.value().effects().get(supplier.get());
        if (dataComponent != null) {
            consumer.accept(dataComponent);
        }
    }
}
