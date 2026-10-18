package com.chen1335.ultimateEnchantment.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CommonConfig {

    public static final ModConfigSpec CONFIG_SPEC;

    public static final ModConfigSpec.BooleanValue isUltimateEnchantmentExclusiveEachOther;

    public static final ModConfigSpec.BooleanValue willEndCityTreasureLootUltimateEnchant;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.comment("Common settings").push("Common");

        isUltimateEnchantmentExclusiveEachOther = builder
                .comment("Define whether ultimate enchantments are exclusive with each other")
                .define("isUltimateEnchantmentExclusiveEachOther", true);

        willEndCityTreasureLootUltimateEnchant = builder
                .comment("Define whether ultimate enchantments will appear in end city treasure")
                .define("willEndCityTreasureLootUltimateEnchant", true);

        builder.pop();
        CONFIG_SPEC = builder.build();
    }
}
