package com.chen1335.ultimate_enchantment.tags;

import com.chen1335.ultimate_enchantment.UltimateEnchantment;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public interface UEItemTags {
    TagKey<Item> WEAPON_TOOLS = create("weapon_tools");
    TagKey<Item> RANGE_WEAPON_ENCHANTABLE = create("range_weapon_enchantable");

    private static TagKey<Item> create(String pName) {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(UltimateEnchantment.MODID,pName));
    }
}
