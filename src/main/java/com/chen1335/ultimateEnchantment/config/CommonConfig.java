package com.chen1335.ultimateEnchantment.config;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = UltimateEnchantment.MODID)
public class CommonConfig {

    public static final ModConfigSpec CONFIG_SPEC;

    public static final ModConfigSpec.BooleanValue isUltimateEnchantmentExclusiveEachOther;

    public static final ModConfigSpec.BooleanValue willEndCityTreasureLootUltimateEnchant;

    public static final ModConfigSpec.ConfigValue<List<? extends String>> legendBlackList;

    /**
     * 配置里存的是属性 ID 字符串，这里是查表之后的结果。
     * <p>
     * {@link Attribute} 是注册表对象，没有序列化形式，配置项承载不了它 —— 所以它只能是派生值，
     * 由 {@link #bake()} 在配置加载与重载时重建。
     */
    public static List<Attribute> loadedLegendBlackList = List.of();

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.comment("Common settings").push("Common");

        isUltimateEnchantmentExclusiveEachOther = builder
                .comment("Define whether ultimate enchantments are exclusive with each other")
                .define("isUltimateEnchantmentExclusiveEachOther", true);

        willEndCityTreasureLootUltimateEnchant = builder
                .comment("Define whether ultimate enchantments will appear in end city treasure")
                .define("willEndCityTreasureLootUltimateEnchant", true);

        legendBlackList = builder
                .comment("Attributes the Legend enchantment will not boost. Entries are attribute IDs.")
                .defineListAllowEmpty(
                        "LegendEnchantmentBlackList",
                        List.of(Attributes.SCALE.getKey().location().toString()),
                        () -> "",
                        o -> o instanceof String s && ResourceLocation.tryParse(s) != null);

        builder.pop();
        CONFIG_SPEC = builder.build();
    }

    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent.Loading event) {
        if (CONFIG_SPEC == event.getConfig().getSpec()) {
            bake();
        }
    }

    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        if (CONFIG_SPEC == event.getConfig().getSpec()) {
            bake();
        }
    }

    /**
     * 把配置里的属性 ID 解析成 {@link Attribute}，整体替换而不是往旧列表里追加 ——
     * 加载和重载都会走到这里，追加会让旧条目越积越多。
     * <p>
     * 认不出来的 ID 直接丢掉：玩家写错一个不认识的属性，不该让整个配置失效。
     */
    private static void bake() {
        List<Attribute> attributes = new ArrayList<>();
        for (String id : legendBlackList.get()) {
            Attribute attribute = BuiltInRegistries.ATTRIBUTE.get(ResourceLocation.parse(id));
            if (attribute != null) {
                attributes.add(attribute);
            }
        }
        loadedLegendBlackList = List.copyOf(attributes);
    }
}
