package com.chen1335.ultimateEnchantment;

import com.chen1335.ultimateEnchantment.common.AttributeTypeInfo;
import com.chen1335.ultimateEnchantment.data.LootProvider;
import com.chen1335.ultimateEnchantment.enchantment.Enchantments;
import com.chen1335.ultimateEnchantment.mixinsAPI.IEnchantmentExtension;
import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mod(UltimateEnchantment.MODID)
public class UltimateEnchantment {
    public static final String MODID = "ultimate_enchantment";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);


    public static final RegistryObject<CreativeModeTab> ULTIMATE_ENCHANTMENT_TAB = CREATIVE_MODE_TABS.register("ultimate_enchantment_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(Items.ENCHANTED_BOOK::getDefaultInstance)
            .displayItems((parameters, output) -> {
                ArrayList<Enchantment> ultimateEnchantments = new ArrayList<>();
                ArrayList<Enchantment> legendaryEnchantments = new ArrayList<>();
                ArrayList<Enchantment> otherEnchantments = new ArrayList<>();

                Enchantments.ENCHANTMENT_DEFERRED_REGISTER.getEntries().forEach(object -> {
                    Enchantment enchantment = object.get();
                    IEnchantmentExtension enchantmentExtension = (IEnchantmentExtension) enchantment;
                    if (enchantmentExtension.ue$getEnchantmentType() == EnchantmentType.ULTIMATE_ENCHANTMENT) {
                        ultimateEnchantments.add(enchantment);
                    } else if (enchantmentExtension.ue$getEnchantmentType() == EnchantmentType.LEGENDARY_ENCHANTMENT) {
                        legendaryEnchantments.add(enchantment);
                    } else {
                        otherEnchantments.add(enchantment);
                    }
                });

                ultimateEnchantments.forEach(ultimateEnchantment -> {
                    ItemStack book = Items.ENCHANTED_BOOK.getDefaultInstance();
                    EnchantmentHelper.setEnchantments(ImmutableMap.of(ultimateEnchantment, ultimateEnchantment.getMaxLevel()), book);
                    output.accept(book);
                });

                legendaryEnchantments.forEach(ultimateEnchantment -> {
                    ItemStack book = Items.ENCHANTED_BOOK.getDefaultInstance();
                    EnchantmentHelper.setEnchantments(ImmutableMap.of(ultimateEnchantment, ultimateEnchantment.getMaxLevel()), book);
                    output.accept(book);
                });

                otherEnchantments.forEach(ultimateEnchantment -> {
                    ItemStack book = Items.ENCHANTED_BOOK.getDefaultInstance();
                    EnchantmentHelper.setEnchantments(ImmutableMap.of(ultimateEnchantment, ultimateEnchantment.getMaxLevel()), book);
                    output.accept(book);
                });

            }).build());

    public UltimateEnchantment() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::runData);
        CREATIVE_MODE_TABS.register(modEventBus);

        Enchantments.ENCHANTMENT_DEFERRED_REGISTER.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public void runData(GatherDataEvent event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();
        event.getGenerator().addProvider(true, new LootProvider(packOutput, Set.of()));
    }


    private void commonSetup(final FMLCommonSetupEvent event) {
        AttributeTypeInfo.init();
    }

    public enum EnchantmentType {
        ULTIMATE_ENCHANTMENT,
        LEGENDARY_ENCHANTMENT,
        OTHER
    }
}
