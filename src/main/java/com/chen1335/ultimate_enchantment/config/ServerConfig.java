package com.chen1335.ultimate_enchantment.config;


import com.chen1335.ultimate_enchantment.UltimateEnchantment;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = UltimateEnchantment.MODID)
public class ServerConfig {

    /**
     * 是否锁死终极附魔的等级，使其无法被外部手段提升（例如神化的影激宝石）。
     * <p>
     * 默认 true，即保持原有行为。
     */
    public static final ModConfigSpec.BooleanValue ultimateEnchantmentCantBeUpgraded;

    /**
     * {@link #ultimateEnchantmentCantBeUpgraded} 的缓存值。
     * <p>
     * {@code GetEnchantmentLevelEvent} 也发生在服务器配置加载之前（物品附魔在注册期、开局与
     * 创造模式物品栏构建时就会被查询），此时直接读 {@code BooleanValue} 会抛
     * {@code IllegalStateException: Cannot get config value before config is loaded.}。
     * 所以这里额外存一份静态副本，初值取默认值，由 {@link #bake()} 在加载与重载时刷新。
     */
    public static boolean ultimateEnchantmentCantBeUpgradedCache = true;

    public static final ModConfigSpec CONFIG_SPEC;


    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        ultimateEnchantmentCantBeUpgraded = builder
                .comment(
                        "Whether ultimate enchantments have their levels locked to the level stored on the item,",
                        "so that they cannot be raised by outside means such as Endersurge from Apotheosis.",
                        "Set to false to allow other mods and effects to raise ultimate enchantment levels."
                )
                .define("ultimateEnchantmentCantBeUpgraded", true);

        CONFIG_SPEC = builder.build();
    }

    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading ev) {
        if (CONFIG_SPEC == ev.getConfig().getSpec()) {
            bake();
        }
    }

    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent.Loading ev) {
        if (CONFIG_SPEC == ev.getConfig().getSpec()) {
            bake();
        }
    }

    private static void bake() {
        ultimateEnchantmentCantBeUpgradedCache = ultimateEnchantmentCantBeUpgraded.getAsBoolean();
    }
}
