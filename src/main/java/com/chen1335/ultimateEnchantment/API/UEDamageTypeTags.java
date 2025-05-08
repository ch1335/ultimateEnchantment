package com.chen1335.ultimateEnchantment.API;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

public interface UEDamageTypeTags {
    TagKey<DamageType> IS_ATTACK = create("is_attack");

    private static TagKey<DamageType> create(String name) {
        return TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(UltimateEnchantment.MODID,name));
    }
}
