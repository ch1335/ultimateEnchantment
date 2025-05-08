package com.chen1335.ultimateEnchantment.data.registries;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class UEDamageType {
    public static ResourceKey<DamageType> TEAR_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(UltimateEnchantment.MODID, "tear"));

    public static void bootstrapContext(BootstrapContext<DamageType> context) {
        context.register(TEAR_DAMAGE, new DamageType("tear", 0));
    }
}
