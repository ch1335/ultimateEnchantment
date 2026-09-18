package com.chen1335.ultimate_enchantment;

import com.chen1335.ultimate_enchantment.API.AttachmentTypes;
import com.chen1335.ultimate_enchantment.API.objects.Conditions;
import com.chen1335.ultimate_enchantment.API.objects.LootItemConditions;
import com.chen1335.ultimate_enchantment.API.objects.UESounds;
import com.chen1335.ultimate_enchantment.apotheosis.attachmentDatas.ApothBossAttachmentTypes;
import com.chen1335.ultimate_enchantment.common.EnchantmentLookup;
import com.chen1335.ultimate_enchantment.common.EventHandler;
import com.chen1335.ultimate_enchantment.common.Formula;
import com.chen1335.ultimate_enchantment.config.CommonConfig;
import com.chen1335.ultimate_enchantment.config.ServerConfig;
import com.chen1335.ultimate_enchantment.data.registries.UERegistries;
import com.chen1335.ultimate_enchantment.dataComponentType.UEDataComponentTypes;
import com.chen1335.ultimate_enchantment.enchantment.UEEnchantments;
import com.chen1335.ultimate_enchantment.enchantment.effectComponents.UEEnchantmentEffectComponents;
import com.chen1335.ultimate_enchantment.enchantment.effectComponents.FormulaComponent;
import com.chen1335.ultimate_enchantment.mobEffect.MobEffects;
import com.chen1335.ultimate_enchantment.tags.UEEnchantmentTags;
import com.mojang.logging.LogUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Map;

@Mod(UltimateEnchantment.MODID)
public class UltimateEnchantment {
    public static final String MODID = "ultimate_enchantment";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    /**
     * 公式求值用的 JS 引擎。
     * <p>
     * JDK 15 起内置 Nashorn 已被移除，本模组改为自带的 standalone Nashorn（构建期 jarJar 内嵌），
     * 它通过 ServiceLoader 自行注册 {@code javax.script} 引擎，故这里仍用标准的
     * {@link ScriptEngineManager} 取用。
     * <p>
     * <b>刻意 fail-fast</b>：引擎取不到时直接抛异常终止类初始化，而不是留一个 {@code null}
     * 让后续每次 {@code Formula.calculate} 都走异常回滚分支 —— 那种失败会表现为"所有附魔数值
     * 恒为 0"，既没有报错也没有日志，极难定位。这里崩在加载期，原因一目了然。
     */
    public static final ScriptEngine SCRIPT_ENGINE = createScriptEngine();

    private static ScriptEngine createScriptEngine() {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        if (engine == null) {
            throw new IllegalStateException(
                    "Nashorn script engine unavailable. The bundled org.openjdk.nashorn:nashorn-core "
                            + "is missing or failed to register its ScriptEngineFactory; enchantment formulas cannot be evaluated."
            );
        }
        LOGGER.info("Enchantment formula engine: {} {}", engine.getFactory().getEngineName(), engine.getFactory().getEngineVersion());
        return engine;
    }
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ENCHANTMENT_TAB = CREATIVE_MODE_TABS.register("ultimate_enchantment", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.ultimate_enchantment"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(Items.ENCHANTED_BOOK::getDefaultInstance)
            .displayItems((parameters, output) -> {

                ItemStack allEnchantmentBook1 = new ItemStack(Items.ENCHANTED_BOOK);
                allEnchantmentBook1.set(DataComponents.CUSTOM_NAME, Component.translatable("ultimate_enchantment.enchantment"));
                parameters.holders().lookupOrThrow(Registries.ENCHANTMENT).get(UEEnchantmentTags.ENCHANTMENTS).ifPresent(holders -> {
                    holders.forEach(enchantmentHolder -> {
                        allEnchantmentBook1.enchant(enchantmentHolder, enchantmentHolder.value().getMaxLevel());
                        output.accept(EnchantmentHelper.createBook(new EnchantmentInstance(enchantmentHolder, enchantmentHolder.value().getMaxLevel())));
                    });
                });
                output.accept(allEnchantmentBook1);

                ItemStack allEnchantmentBook2 = new ItemStack(Items.ENCHANTED_BOOK);
                allEnchantmentBook2.set(DataComponents.CUSTOM_NAME, Component.translatable("apothic_enchanting.enchantment.addition"));
                parameters.holders().lookupOrThrow(Registries.ENCHANTMENT).get(UEEnchantmentTags.UE_APOTHIC_ENCHANTING_ADDITION).ifPresent(holders -> {
                    holders.forEach(enchantmentHolder -> {
                        allEnchantmentBook2.enchant(enchantmentHolder, enchantmentHolder.value().getMaxLevel());
                    });
                });

                if (!allEnchantmentBook2.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY).isEmpty()) {
                    output.accept(allEnchantmentBook2);
                }
            }).build());

    public UltimateEnchantment(IEventBus modEventBus, ModContainer modContainer) {
        CREATIVE_MODE_TABS.register(modEventBus);
        Conditions.CONDITION_CODECS.register(modEventBus);
        LootItemConditions.LOOT_ITEM_CONDITION_TYPES.register(modEventBus);
        UEDataComponentTypes.AEA_DATA.register(modEventBus);
        UEEnchantmentEffectComponents.TYPES.register(modEventBus);
        AttachmentTypes.ATTACHMENT_TYPES.register(modEventBus);
        // 神化专属的 attachment 必须等确认神化加载后再注册：ApothBossInfo 直接引用神化类型，
        // 神化缺席时连加载都不行。隔着一层类的理由见 ApothBossAttachmentTypes 的类注释。
        if (ModList.get().isLoaded("apotheosis")) {
            ApothBossAttachmentTypes.ATTACHMENT_TYPES.register(modEventBus);
        }
        MobEffects.MOB_EFFECT_DEFERRED_REGISTER.register(modEventBus);
        modEventBus.addListener(this::setUp);
        // 注意：不要在此显式注册 UERegistries::gatherData。
        // 它已由类上的 @EventBusSubscriber + 方法上的 @SubscribeEvent 自动注册；
        // 重复注册会让 gatherData 执行两遍，数据生成器随即抛
        // "Duplicate provider: Registries" 而失败。
        // （先前 runData 产出 0 个文件的真正原因是 build.gradle 用了 clientData()，
        //  而该监听器是 GatherDataEvent.Server —— 已改为 serverData()。）
        NeoForge.EVENT_BUS.addListener(this::ServerStartedEvent);
        UESounds.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfig.CONFIG_SPEC,"ultimate_enchantment/server.toml");
        modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.CONFIG_SPEC,"ultimate_enchantment/common.toml");

        // Iron's Spells 'n Spellbooks：官方仅支持到 1.21.1，无 26.1.2 版本，
        // 其联动功能（hardened_mana / mana_steal 附魔、施法事件）已按移植决策整体删除。
        // Twilight Forest：官方未发布 26.1.2 版本，相关集成暂时禁用。
        //
        // 另：原有一段 `if (ModList.get().isLoaded("cloth_config")) { if (Environment...isClient()) {} }`
        // 属空逻辑（无任何副作用），且 26.1 已移除 net.neoforged.neoforgespi.Environment，故整段删除。

    }

    public static Identifier id(String id) {
        return Identifier.fromNamespaceAndPath(MODID, id);
    }

    public void setUp(FMLCommonSetupEvent event) {
        UEEnchantments.init();
    }

    public void ServerStartedEvent(ServerStartedEvent event) {
        // 附魔注册表在 RegistryLayer.WORLDGEN，/reload 不会重建它，所以缓存在这里填一次即可。
        // 详见 EnchantmentLookup 的类注释。
        EnchantmentLookup.refreshServer(event.getServer().registryAccess());

        int[] registered = {0};
        event.getServer().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).listElements().forEach(r -> {
            if (r.key().identifier().getNamespace().equals(MODID)) {
                registered[0]++;
                UEEnchantments.getEnchantment(r.key()).ifPresent(old -> {
                    FormulaComponent loaded = r.value().effects().get(UEEnchantmentEffectComponents.FORMULA.value());
                    if (loaded != null) {
                        Map<String, Formula> formulas = loaded.formulas();
                        // 编译失败由 Formula 自己回滚到默认公式，这里不需要兜异常
                        old.formulas.forEach((s, formula) ->
                                formula.compile(formulas.getOrDefault(s, formula).getFormula()));
                    }
                });
            }
        });
        // 与 DynamicDataPackManager 的「动态包已建立」日志配对使用：
        // 前者说明数据被喂了进去，这里说明注册表最终真的收下了多少条。
        // 两者数字不一致即意味着有数据被条件过滤或 codec 拒绝。
        LOGGER.info("Enchantment registry contains {} entr(ies) in namespace {}", registered[0], MODID);
    }
}
