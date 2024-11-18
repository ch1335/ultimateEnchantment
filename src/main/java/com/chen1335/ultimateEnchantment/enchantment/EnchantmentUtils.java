package com.chen1335.ultimateEnchantment.enchantment;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.mixinsAPI.IEnchantmentExtension;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.HashMap;
import java.util.Map;

public class EnchantmentUtils {

    public static void sortInItem(ItemStack itemStack) {
        EnchantmentHelper.setEnchantments(EnchantmentHelper.deserializeEnchantments(itemStack.getEnchantmentTags()), itemStack);
    }

    public static void sortInMap(Map<Enchantment, Integer> map) {
        Map<Enchantment, Integer> UltimateEnchantments = new HashMap<>();
        Map<Enchantment, Integer> LegendaryEnchantments = new HashMap<>();
        Map<Enchantment, Integer> otherEnchantments = new HashMap<>();
        map.forEach((enchantment, level) -> {
            IEnchantmentExtension enchantmentExtension = (IEnchantmentExtension) enchantment;
            if (enchantmentExtension.ue$getEnchantmentType() == UltimateEnchantment.EnchantmentType.ULTIMATE_ENCHANTMENT) {
                UltimateEnchantments.put(enchantment, level);
            } else if (enchantmentExtension.ue$getEnchantmentType() == UltimateEnchantment.EnchantmentType.LEGENDARY_ENCHANTMENT) {
                LegendaryEnchantments.put(enchantment, level);
            } else {
                otherEnchantments.put(enchantment, level);
            }
        });
        map.clear();
        map.putAll(UltimateEnchantments);
        map.putAll(LegendaryEnchantments);
        map.putAll(otherEnchantments);

    }
}
