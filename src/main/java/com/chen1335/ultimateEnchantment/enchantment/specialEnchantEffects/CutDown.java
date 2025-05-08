package com.chen1335.ultimateEnchantment.enchantment.specialEnchantEffects;

public class CutDown {
    public static float getDamageMultiplier(float attackerMaxHealth, float targetCurrentHealth, int enchantmentLevel) {
        return Math.clamp((targetCurrentHealth - attackerMaxHealth) / attackerMaxHealth * getDamageMultiplierByLevel(enchantmentLevel)*100, 0, getMaxDamageMultiplier(enchantmentLevel));
    }

    public static float getDamageMultiplierByLevel(int enchantmentLevel) {
        return 0.0002F;
    }

    public static float getMaxDamageMultiplier(int enchantmentLevel) {
        return (float) (enchantmentLevel * 0.1);
    }

}
