package com.chen1335.ultimateEnchantment;

import com.chen1335.ultimateEnchantment.API.AttachmentTypes;
import com.chen1335.ultimateEnchantment.API.objects.Conditions;
import com.chen1335.ultimateEnchantment.API.objects.LootItemConditions;
import com.chen1335.ultimateEnchantment.common.EventHandler;
import com.chen1335.ultimateEnchantment.config.CommonConfig;
import com.chen1335.ultimateEnchantment.dataComponentType.UEDataComponentTypes;
import com.chen1335.ultimateEnchantment.enchantment.effectComponents.UEEnchantmentEffectComponents;
import com.chen1335.ultimateEnchantment.enchantment.effects.UEEnchantmentEffects;
import com.chen1335.ultimateEnchantment.mobEffect.MobEffects;
import com.chen1335.ultimateEnchantment.tags.UEEnchantmentTags;
import com.mojang.logging.LogUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforgespi.Environment;
import org.slf4j.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

@Mod(UltimateEnchantment.MODID)
public class UltimateEnchantment {
    public static final String MODID = "ultimate_enchantment";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final ScriptEngine SCRIPT_ENGINE = new ScriptEngineManager().getEngineByName("nashorn");
    private static boolean IRONS_SPELL_BOOKS_LOADED = false;

    private static boolean TWILIGHT_FOREST_LOADED = false;
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
                        output.accept(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantmentHolder, enchantmentHolder.value().getMaxLevel())));
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
        UEEnchantmentEffects.ENCHANTMENT_ENTITY_EFFECT.register(modEventBus);
        UEEnchantmentEffects.ENCHANTMENT_LOCATION_BASED_EFFECT.register(modEventBus);
        UEEnchantmentEffectComponents.TYPES.register(modEventBus);
        AttachmentTypes.ATTACHMENT_TYPES.register(modEventBus);
        MobEffects.MOB_EFFECT_DEFERRED_REGISTER.register(modEventBus);
        modEventBus.addListener(this::setUp);
        if (ModList.get().isLoaded("irons_spellbooks")) {
            IRONS_SPELL_BOOKS_LOADED = true;
            NeoForge.EVENT_BUS.register(EventHandler.Game.IronsSpellBooksEvents.class);
        }
        if (ModList.get().isLoaded("twilightforest")) {
            TWILIGHT_FOREST_LOADED = true;
        }


        if (ModList.get().isLoaded("cloth_config")) {
            if (Environment.get().getDist().isClient()) {

            }
        }

    }


    public void setUp(FMLCommonSetupEvent event) {
        CommonConfig.staticLoad();
    }

    public static boolean isIronsSpellBooksLoaded() {
        return IRONS_SPELL_BOOKS_LOADED;
    }

    public static boolean isTwilightForestLoaded() {
        return TWILIGHT_FOREST_LOADED;
    }
}
