package com.chen1335.ultimateEnchantment;

import com.chen1335.ultimateEnchantment.dataComponentType.UEDataComponentTypes;
import com.chen1335.ultimateEnchantment.enchantment.effectComponents.UltimateEnchantment.UEEnchantmentEffectComponents;
import com.chen1335.ultimateEnchantment.enchantment.effects.UltimateEnchantment.UEEnchantmentEffects;
import com.chen1335.ultimateEnchantment.tags.UEEnchantmentTags;
import com.mojang.logging.LogUtils;
import dev.shadowsoffire.placebo.registry.DeferredHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(UltimateEnchantment.MODID)
public class UltimateEnchantment {
    public static final String MODID = "ultimate_enchantment";

    public static final DeferredHelper R = DeferredHelper.create(MODID);

    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ENCHANTMENT_TAB = CREATIVE_MODE_TABS.register("ultimate_enchantment", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.ultimate_enchantment"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(Items.ENCHANTED_BOOK::getDefaultInstance)
            .displayItems((parameters, output) -> {

                ItemStack allEnchantmentBook1 = new ItemStack(Items.ENCHANTED_BOOK);
                allEnchantmentBook1.set(DataComponents.CUSTOM_NAME,Component.translatable("ultimate_enchantment.enchantment"));
                parameters.holders().lookupOrThrow(Registries.ENCHANTMENT).get(UEEnchantmentTags.ENCHANTMENTS).ifPresent(holders -> {
                    holders.forEach(enchantmentHolder -> {
                        allEnchantmentBook1.enchant(enchantmentHolder,enchantmentHolder.value().getMaxLevel());
                        output.accept(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantmentHolder, enchantmentHolder.value().getMaxLevel())));
                    });
                });
                output.accept(allEnchantmentBook1);

                ItemStack allEnchantmentBook2 = new ItemStack(Items.ENCHANTED_BOOK);
                allEnchantmentBook2.set(DataComponents.CUSTOM_NAME,Component.translatable("apothic_enchanting.enchantment.addition"));
                parameters.holders().lookupOrThrow(Registries.ENCHANTMENT).get(UEEnchantmentTags.UE_APOTHIC_ENCHANTING_ADDITION).ifPresent(holders -> {
                    holders.forEach(enchantmentHolder -> {
                        allEnchantmentBook2.enchant(enchantmentHolder,enchantmentHolder.value().getMaxLevel());
                    });
                });

                if (!allEnchantmentBook2.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY).isEmpty()) {
                    output.accept(allEnchantmentBook2);
                }
            }).build());

    public UltimateEnchantment(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        CREATIVE_MODE_TABS.register(modEventBus);
        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);

        UEDataComponentTypes.AEA_DATA.register(modEventBus);
        UEEnchantmentEffects.ENCHANTMENT_ENTITY_EFFECT.register(modEventBus);
        UEEnchantmentEffects.ENCHANTMENT_LOCATION_BASED_EFFECT.register(modEventBus);
        UEEnchantmentEffectComponents.TYPES.register(modEventBus);


        modContainer.registerConfig(ModConfig.Type.COMMON, UEConfig.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {


    }

    @SubscribeEvent
    public void onServerAboutToStart(ServerAboutToStartEvent event) {

    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }
    }
}
