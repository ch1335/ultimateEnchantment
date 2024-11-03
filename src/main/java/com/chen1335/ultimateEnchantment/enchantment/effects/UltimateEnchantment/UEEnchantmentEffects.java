package com.chen1335.ultimateEnchantment.enchantment.effects.UltimateEnchantment;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentLocationBasedEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class UEEnchantmentEffects {
    public static DeferredRegister<MapCodec<? extends EnchantmentEntityEffect>> ENCHANTMENT_ENTITY_EFFECT = DeferredRegister.create(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE,UltimateEnchantment.MODID);


    public static DeferredHolder<MapCodec<? extends EnchantmentEntityEffect>,MapCodec<LastStandEffect>> LAST_STAND = ENCHANTMENT_ENTITY_EFFECT.register("last_stand",()->LastStandEffect.CODEC);

    public static DeferredRegister<MapCodec<? extends EnchantmentLocationBasedEffect>> ENCHANTMENT_LOCATION_BASED_EFFECT = DeferredRegister.create(Registries.ENCHANTMENT_LOCATION_BASED_EFFECT_TYPE,UltimateEnchantment.MODID);

    private static ResourceKey<MapCodec<? extends EnchantmentLocationBasedEffect>> key(String pName) {
        return ResourceKey.create(Registries.ENCHANTMENT_LOCATION_BASED_EFFECT_TYPE, ResourceLocation.fromNamespaceAndPath(UltimateEnchantment.MODID, pName));
    }
}
