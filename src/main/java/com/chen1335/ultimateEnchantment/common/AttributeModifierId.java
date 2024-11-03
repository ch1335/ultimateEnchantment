package com.chen1335.ultimateEnchantment.common;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import net.minecraft.resources.ResourceLocation;


public class AttributeModifierId {
    public static ResourceLocation LAST_STAND_ARMOR = create("last_stand_armor");

    private static ResourceLocation create(String id){
        return ResourceLocation.fromNamespaceAndPath(UltimateEnchantment.MODID, id);
    }
}
