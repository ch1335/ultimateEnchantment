package com.chen1335.ultimateEnchantment;

import com.chen1335.ultimateEnchantment.enchantment.config.IEnchantmentConfig;
import dev.shadowsoffire.placebo.config.Configuration;

import java.util.HashSet;
import java.util.Set;

public class UEConfig {

    public static Set<IEnchantmentConfig> configs = new HashSet<>();

    public static void load(){

        Configuration cfg = new Configuration(UltimateEnchantment.getConfigFile(UltimateEnchantment.MODID));


        if (cfg.hasChanged()) {
            cfg.save();
        }
    }

    public static void addConfigs(){

    }
}
