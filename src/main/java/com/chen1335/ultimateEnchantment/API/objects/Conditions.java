package com.chen1335.ultimateEnchantment.API.objects;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.common.conditions.EnchantmentEnableCondition;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class Conditions {
    public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_CODECS = DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, UltimateEnchantment.MODID);

    public static final DeferredHolder<MapCodec<? extends ICondition>, MapCodec<EnchantmentEnableCondition>> ENCHANTMENT_ENABLE_CONDITION = CONDITION_CODECS.register("enchantment_enable_condition", () -> EnchantmentEnableCondition.CODEC);
}
