package com.chen1335.ultimateEnchantment.enchantment;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.mixinsAPI.IEnchantmentExtension;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class CommonEnchantmentBase extends Enchantment {
    public CommonEnchantmentBase(Rarity pRarity, EnchantmentCategory pCategory, EquipmentSlot[] pApplicableSlots, UltimateEnchantment.EnchantmentType enchantmentType) {
        super(pRarity, pCategory, pApplicableSlots);
        ((IEnchantmentExtension) this).ue$setEnchantmentType(enchantmentType);
    }

    @Override
    public boolean isTradeable() {
        return !(((IEnchantmentExtension) this).ue$getEnchantmentType() == UltimateEnchantment.EnchantmentType.ULTIMATE_ENCHANTMENT);
    }

    @Override
    public boolean isTreasureOnly() {
        return (((IEnchantmentExtension) this).ue$getEnchantmentType() == UltimateEnchantment.EnchantmentType.ULTIMATE_ENCHANTMENT);
    }

    @Override
    public boolean isDiscoverable() {
        return !(((IEnchantmentExtension) this).ue$getEnchantmentType() == UltimateEnchantment.EnchantmentType.ULTIMATE_ENCHANTMENT);
    }
}
