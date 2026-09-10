package com.chen1335.ultimateEnchantment.enchantment;

import com.chen1335.ultimateEnchantment.enchantment.enchantments.LethalTempo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.HashMap;
import java.util.Map;

public class Enchantments {
    private static final Map<ResourceKey<Enchantment>, EnchantmentBasic> MAP = new HashMap<>();
    public static final LethalTempo LETHAL_TEMPO = register(new LethalTempo());


    private static <T extends EnchantmentBasic> T register(T enchantment) {
        MAP.put(enchantment.createKey(), enchantment);
        return null;
    }

    public static void init() {

    }
}
