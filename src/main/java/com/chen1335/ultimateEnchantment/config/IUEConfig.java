package com.chen1335.ultimateEnchantment.config;

import com.electronwill.nightconfig.core.CommentedConfig;

public interface IUEConfig {

    String getFileName();

    default <T> T get(CommentedConfig config, String string, T defaultValue) {
        T value = config.get(string);
        if (value == null) {
            config.set(string, defaultValue);
            return defaultValue;
        }
        return value;
    }

    default <T> T get(CommentedConfig config, String name, T defaultValue, String comment) {
        T value = config.get(name);
        if (value == null) {
            config.set(name, defaultValue);
            config.setComment(name, comment);
            return defaultValue;
        }
        return value;
    }
}
