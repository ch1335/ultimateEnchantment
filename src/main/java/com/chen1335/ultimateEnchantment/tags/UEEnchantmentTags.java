package com.chen1335.ultimateEnchantment.tags;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;

public interface UEEnchantmentTags {

    TagKey<Enchantment> ENCHANTMENTS = create("enchantments");

    TagKey<Enchantment> COMMON_ENCHANTMENT = create("common_enchantment");

    TagKey<Enchantment> ULTIMATE_ENCHANTMENT = create("ultimate_enchantment");

    TagKey<Enchantment> LEGENDARY_ENCHANTMENT = create("legendary_enchantment");

    TagKey<Enchantment> TRADEABLE_LEGENDARY_ENCHANTMENT = create("tradeable_legendary_enchantment");

    TagKey<Enchantment> UN_TRADEABLE_LEGENDARY_ENCHANTMENT = create("un_tradeable_legendary_enchantment");

    TagKey<Enchantment> USE_CUSTOM_COLOR = create("use_custom_color");

    TagKey<Enchantment> LIFE_STEAL_ENCHANTMENT = create("life_steal_enchantment");

    TagKey<Enchantment> UE_APOTHIC_ENCHANTING_ADDITION = create("ue_apothic_enchanting_addition");

    private static TagKey<Enchantment> create(String pName) {
        return TagKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(UltimateEnchantment.MODID,pName));
    }
}
