package com.chen1335.ultimateEnchantment.enchantment.enchantments;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.enchantment.CommonEnchantmentBase;
import com.chen1335.ultimateEnchantment.enchantment.UEEnchantmentCategory;
import dev.shadowsoffire.placebo.config.Configuration;
import net.minecraft.world.entity.EquipmentSlot;

public class Legend extends CommonEnchantmentBase {

    public float attributeBonusPerLevel = 0.02F;

    public Legend() {
        super(Rarity.VERY_RARE, UEEnchantmentCategory.CAN_ENCHANT, EquipmentSlot.values(), UltimateEnchantment.EnchantmentType.ULTIMATE_ENCHANTMENT);
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    public float getAttributeBonus(int level) {
        return attributeBonusPerLevel * level;
    }

    @Override
    public void loadConfig(Configuration config) {
        attributeBonusPerLevel = config.getFloat("attributeBonusPerLevel",this.getSimpleName(),attributeBonusPerLevel,Float.MIN_VALUE,Float.MAX_VALUE,"the attributeBonusPerLevel");
    }
}
