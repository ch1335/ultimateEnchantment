package com.chen1335.ultimate_enchantment.common;

import com.chen1335.ultimate_enchantment.UltimateEnchantment;
import net.minecraft.resources.Identifier;


public class AttributeModifierId {
    public static Identifier LAST_STAND_ARMOR = create("last_stand_armor");
    public static Identifier LAST_STAND_ARMOR_TOUGHNESS = create("last_stand_armor_toughness");
    private static Identifier create(String id){
        return Identifier.fromNamespaceAndPath(UltimateEnchantment.MODID, id);
    }
}
