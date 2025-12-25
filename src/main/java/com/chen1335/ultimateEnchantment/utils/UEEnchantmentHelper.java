package com.chen1335.ultimateEnchantment.utils;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.CommonHooks;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface UEEnchantmentHelper {
    static Optional<Holder.Reference<Enchantment>> getEnchantment(ResourceKey<Enchantment> resourceKey) {
        return Objects.requireNonNull(CommonHooks.resolveLookup(Registries.ENCHANTMENT)).get(resourceKey);
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
