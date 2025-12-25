package com.chen1335.ultimateEnchantment.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.ArrayList;
import java.util.List;

public class CommonConfig extends AbstractConfig {
    private static final CommonConfig INSTANCE = new CommonConfig();
    public static boolean isUltimateEnchantmentExclusiveEachOther = true;

    public static boolean willEndCityTreasureLootUltimateEnchant = true;

    public static List<String> legendBlackList = new ArrayList<>();

    public static List<Attribute> loadedLegendBlackList = new ArrayList<>();


    public static void staticLoad() {
        INSTANCE.loadConfig();
    }


    @Override
    protected void load(CommentedFileConfig config) {
        CommentedConfig common = get(config, "Common", config.createSubConfig());
        isUltimateEnchantmentExclusiveEachOther = get(common, "isUltimateEnchantmentExclusiveEachOther", isUltimateEnchantmentExclusiveEachOther, "define whether ultimate enchantment exclusive each other");
        willEndCityTreasureLootUltimateEnchant = get(common, "isUltimateEnchantmentExclusiveEachOther", willEndCityTreasureLootUltimateEnchant, "define whether ultimate enchantment will appear in end city treasure");

        List<String> defaultBlackList = new ArrayList<>();
        defaultBlackList.add(Attributes.SCALE.getKey().location().toString());
        legendBlackList = get(common, "LegendEnchantmentBlackList", defaultBlackList);

        legendBlackList.forEach(s -> {
            Attribute attribute = BuiltInRegistries.ATTRIBUTE.get(ResourceLocation.parse(s));
            if (attribute != null) {
                loadedLegendBlackList.add(attribute);
            }
        });


    }


    @Override
    public String getFileName() {
        return "commonConfig";
    }
}
