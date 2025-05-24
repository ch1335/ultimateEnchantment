package com.chen1335.ultimateEnchantment;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.fml.loading.FMLPaths;

import java.util.*;

public class UEConfig {
    public static void loadConfig() {
        try (CommentedFileConfig config = CommentedFileConfig.of(FMLPaths.CONFIGDIR.get().resolve("ultimate_enchantment.toml"))) {
            config.load();
            CommonConfig.load(config);
            config.save();
        }
    }


    public static class CommonConfig {
        public static boolean isUltimateEnchantmentExclusiveEachOther = true;

        public static List<String> legendBlackList = new ArrayList<>();

        public static List<Attribute> loadedLegendBlackList = new ArrayList<>();

        private static void load(CommentedConfig config) {
            CommentedConfig common = get(config, "Common", config.createSubConfig());
            isUltimateEnchantmentExclusiveEachOther = get(common, "isUltimateEnchantmentExclusiveEachOther", isUltimateEnchantmentExclusiveEachOther, "define whether ultimate enchantment exclusive each other");
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
    }

    private static <T> T get(CommentedConfig config, String string, T defaultValue) {
        T value = config.get(string);
        if (value == null) {
            config.set(string, defaultValue);
            return defaultValue;
        }
        return value;
    }

    private static <T> T get(CommentedConfig config, String name, T defaultValue, String comment) {
        T value = config.get(name);
        if (value == null) {
            config.set(name, defaultValue);
            config.setComment(name, comment);
            return defaultValue;
        }
        return value;
    }
}
