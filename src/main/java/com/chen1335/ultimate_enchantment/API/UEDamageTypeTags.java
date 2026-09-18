package com.chen1335.ultimate_enchantment.API;

import com.chen1335.ultimate_enchantment.UltimateEnchantment;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

public interface UEDamageTypeTags {
    TagKey<DamageType> IS_ATTACK = create("is_attack");

    private static TagKey<DamageType> create(String name) {
        return TagKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath(UltimateEnchantment.MODID,name));
    }
}
