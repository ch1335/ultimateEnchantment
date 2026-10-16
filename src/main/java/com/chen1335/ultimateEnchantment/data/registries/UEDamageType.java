package com.chen1335.ultimateEnchantment.data.registries;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class UEDamageType {
    public static ResourceKey<DamageType> TEAR_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(UltimateEnchantment.MODID, "tear"));

    /** 「雷霆」附魔造成的伤害。用独立类型取代原版的 {@code minecraft:lightning_bolt}。 */
    public static ResourceKey<DamageType> THUNDER_BOLT_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(UltimateEnchantment.MODID, "thunder_bolt"));

    public static void bootstrapContext(BootstrapContext<DamageType> context) {
        context.register(TEAR_DAMAGE, new DamageType("tear", 0));
        context.register(THUNDER_BOLT_DAMAGE, new DamageType("thunder_bolt", 0));
    }
}
