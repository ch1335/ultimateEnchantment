package com.chen1335.ultimateEnchantment.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EnchantmentEnableInfo extends AbstractConfig {
    private static final EnchantmentEnableInfo INSTANCE = new EnchantmentEnableInfo();

    public static Set<ResourceLocation> registryEnchantments = Set.of();

    public static Map<ResourceLocation, Boolean> enableInfo = new HashMap<>();


    public static void staticLoad() {
        INSTANCE.loadConfig();
    }

    @Override
    protected void load(CommentedFileConfig config) {
        for (ResourceLocation registryEnchantment : registryEnchantments) {
            String id = registryEnchantment.getPath().replace(".json", "");
            boolean enable = get(config, id, true);
            enableInfo.put(ResourceLocation.parse(registryEnchantment.toString().replace(".json", "").replace("enchantment/", "")), enable);
        }
    }

    @Override
    public String getFileName() {
        return "enchantmentEnableInfo";
    }
}
