package com.chen1335.ultimateEnchantment.data.registries;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.data.lootTableModifier.EntityLootModifierProvider;
import com.chen1335.ultimateEnchantment.data.tags.UEEnchantmentTagsProvider;
import com.chen1335.ultimateEnchantment.data.tags.UEItemTagsProvider;
import com.chen1335.ultimateEnchantment.enchantment.ApothicEnchantingEnchantments;
import com.chen1335.ultimateEnchantment.enchantment.UEEnchantments;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@EventBusSubscriber(modid = UltimateEnchantment.MODID, bus = EventBusSubscriber.Bus.MOD)
public class UERegistries {

    private static final Map<ResourceKey<?>, List<ICondition>> conditions = new HashMap<>();

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();

        DatapackBuiltinEntriesProvider provider = generator.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(
                generator.getPackOutput(),
                event.getLookupProvider(),
                new RegistrySetBuilder()
                        .add(Registries.ENCHANTMENT, bootstrapContext -> {
                            UEEnchantments.bootstrap(bootstrapContext);
                            ApothicEnchantingEnchantments.bootstrap(bootstrapContext);

                            conditions.putAll(UEEnchantments.conditions);
                            conditions.putAll(ApothicEnchantingEnchantments.conditions);
                        }),
                conditions,
                Set.of(UltimateEnchantment.MODID)
        ));

        generator.addProvider(event.includeServer(), new UEEnchantmentTagsProvider(
                generator.getPackOutput(),
                provider.getRegistryProvider(),
                event.getExistingFileHelper()
        ));

        BlockTagsProvider blockTagsProvider = new BlockTagsProvider(generator.getPackOutput(), event.getLookupProvider(), UltimateEnchantment.MODID, event.getExistingFileHelper()) {
            @Override
            protected void addTags(HolderLookup.@NotNull Provider provider) {

            }
        };

        generator.addProvider(event.includeServer(), blockTagsProvider);

        generator.addProvider(event.includeServer(), new UEItemTagsProvider(
                generator.getPackOutput(),
                event.getLookupProvider(),
                blockTagsProvider.contentsGetter(),
                event.getExistingFileHelper()
        ));

        generator.addProvider(event.includeServer(), new EntityLootModifierProvider(
                generator.getPackOutput(),
                event.getLookupProvider(),
                UltimateEnchantment.MODID
        ));
    }
}
