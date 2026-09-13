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

        }
    }

    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent.Loading ev) {
        if (CONFIG_SPEC == ev.getConfig().getSpec()) {

        }
    }

    public static final ModConfigSpec CONFIG_SPEC;

    public static final ModConfigSpec.BooleanValue ENABLE_LOOT;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        ENABLE_LOOT = builder
                .comment("Can enchantments be obtained from the default loot table")
                .define("enable_loot", true);

        CONFIG_SPEC = builder.build();
    }
}
