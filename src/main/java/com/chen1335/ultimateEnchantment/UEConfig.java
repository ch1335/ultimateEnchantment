package com.chen1335.ultimateEnchantment;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.loading.FMLPaths;

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

        private static void load(CommentedConfig config) {
            CommentedConfig common = get(config, "Common", config.createSubConfig());
            isUltimateEnchantmentExclusiveEachOther = get(common, "isUltimateEnchantmentExclusiveEachOther", isUltimateEnchantmentExclusiveEachOther, "define whether ultimate enchantment exclusive each other");
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
