package com.chen1335.ultimateEnchantment.data.lootTable;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.enchantment.enchatments.UEEnchantments;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class UELootTableProvider extends LootTableProvider {
    public UELootTableProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pRegistries) {
        super(pOutput, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(LootTableModifier::new, LootContextParamSets.CHEST)
        ), pRegistries);
    }

    public static class LootTableModifier implements LootTableSubProvider {
        private final HolderLookup.Provider provider;

        public static final ResourceKey<LootTable> END_CITY_TREASURE_MODIFIER = register("modifier/end_city_treasure_modifier");

        public LootTableModifier(HolderLookup.Provider provider) {
            this.provider = provider;
        }

        @Override
        public void generate(@NotNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
            output.accept(END_CITY_TREASURE_MODIFIER,
                    LootTable.lootTable().withPool(
                            LootPool.lootPool().setRolls(new ConstantValue(1))
                                    .add(EmptyLootItem.emptyItem().setWeight(30))
                                    .add(LootItem.lootTableItem(Items.BOOK).setWeight(10).apply(new SetEnchantmentsFunction.Builder().withEnchantment(provider.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(UEEnchantments.LETHAL_TEMPO), UniformGenerator.between(1, 2))))
                                    .add(LootItem.lootTableItem(Items.BOOK).setWeight(10).apply(new SetEnchantmentsFunction.Builder().withEnchantment(provider.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(UEEnchantments.VANQUISHER), UniformGenerator.between(1, 2))))
                                    .add(LootItem.lootTableItem(Items.BOOK).setWeight(10).apply(new SetEnchantmentsFunction.Builder().withEnchantment(provider.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(UEEnchantments.TEAR), UniformGenerator.between(1, 2))))
                    )
            );


        }

        private static ResourceKey<LootTable> registerModifier(ResourceLocation resourceLocation) {
            return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(UltimateEnchantment.MODID, resourceLocation.getPath() + "_modifier"));
        }
    }

    private static ResourceKey<LootTable> register(String name) {
        return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(UltimateEnchantment.MODID, name));
    }
}
