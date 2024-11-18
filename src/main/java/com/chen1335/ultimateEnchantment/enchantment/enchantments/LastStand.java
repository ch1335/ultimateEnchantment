package com.chen1335.ultimateEnchantment.enchantment.enchantments;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.enchantment.CommonEnchantmentBase;
import com.chen1335.ultimateEnchantment.enchantment.Enchantments;
import net.minecraft.Util;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.EnumMap;
import java.util.UUID;

public class LastStand extends CommonEnchantmentBase {

    public static final EnumMap<ArmorItem.Type, UUID> ARMOR_MODIFIER_UUID_PER_TYPE = Util.make(new EnumMap<>(ArmorItem.Type.class), (p_266744_) -> {
        p_266744_.put(ArmorItem.Type.BOOTS, UUID.fromString("ACD62662-0C15-4B2E-94F7-E2FBAAB2B897"));
        p_266744_.put(ArmorItem.Type.LEGGINGS, UUID.fromString("876EAFA4-21FD-4706-BFDA-F7C6D05BC4D3"));
        p_266744_.put(ArmorItem.Type.CHESTPLATE, UUID.fromString("28483696-D8C1-4A91-A71C-77597BCCFA02"));
        p_266744_.put(ArmorItem.Type.HELMET, UUID.fromString("8D91DEEE-EC15-4DCA-9640-3BEADC7F4F28"));
    });

    public static final EnumMap<ArmorItem.Type, UUID> ARMOR_TOUGHNESS_MODIFIER_UUID_PER_TYPE = Util.make(new EnumMap<>(ArmorItem.Type.class), (p_266744_) -> {
        p_266744_.put(ArmorItem.Type.BOOTS, UUID.fromString("2930A5C5-28BD-4A0C-BA6B-4BCC35239688"));
        p_266744_.put(ArmorItem.Type.LEGGINGS, UUID.fromString("7EB50B5F-5DF4-41BB-920E-363AA4696940"));
        p_266744_.put(ArmorItem.Type.CHESTPLATE, UUID.fromString("4347B5D2-5306-489B-ABA8-9D5F49377257"));
        p_266744_.put(ArmorItem.Type.HELMET, UUID.fromString("58053BDD-358C-4B1A-9B02-E858F720BE27"));
    });

    public float armorBonusPerLevel = 0.05F;

    public float armorToughnessBonusPerLevel = 0.05F;

    public float maxHealthPercentage = 0.34F;

    public LastStand() {
        super(Rarity.VERY_RARE,EnchantmentCategory.ARMOR, Enchantments.ARMOR_SLOTS, UltimateEnchantment.EnchantmentType.ULTIMATE_ENCHANTMENT);
    }

    public float getEffectiveMaximumHealthPercentage() {
        return maxHealthPercentage;
    }

    public float getArmorBonus(int level) {
        return armorBonusPerLevel * level;
    }

    public float getArmorToughnessBonus(int level) {
        return armorToughnessBonusPerLevel * level;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }
}
