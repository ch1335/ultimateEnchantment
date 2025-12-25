package com.chen1335.ultimateEnchantment.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.neoforged.fml.loading.FMLPaths;

import java.io.File;

public abstract class AbstractConfig implements IUEConfig {
    protected void loadConfig() {
        File file = FMLPaths.CONFIGDIR.get().resolve("ultimate_enchantment").toFile();
        if (!file.exists()) {
            file.mkdirs();
        }

        try (CommentedFileConfig config = CommentedFileConfig.of(FMLPaths.CONFIGDIR.get().resolve("ultimate_enchantment").resolve(getFileName() + ".toml"))) {
            config.load();
            load(config);
            config.save();
        }
    }


    protected void load(CommentedFileConfig config) {

    }
}
