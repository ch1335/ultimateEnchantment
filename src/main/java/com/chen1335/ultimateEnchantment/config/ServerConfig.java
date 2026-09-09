package com.chen1335.ultimateEnchantment.config;


import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = UltimateEnchantment.MODID)
public class ServerConfig {
    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading ev) {
        if (CONFIG_SPEC == ev.getConfig().getSpec()) {
            UltimateEnchantment.LOGGER.info("test");
        }
    }

    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent.Loading ev) {
        if (CONFIG_SPEC == ev.getConfig().getSpec()) {
            UltimateEnchantment.LOGGER.info("test");
        }
    }

    public static final ModConfigSpec CONFIG_SPEC;
    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.define("test",1);
        CONFIG_SPEC = builder.build();
    }
}
