package com.chen1335.ultimateEnchantment.data.lootTableModifier;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.data.lootTable.UELootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.AddTableLootModifier;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.concurrent.CompletableFuture;

public class EntityLootModifierProvider extends GlobalLootModifierProvider {
    public EntityLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String modid) {
        super(output, registries, modid);
    }

    @Override
    protected void start() {
        add("wither_loot_modifier", new AddTableLootModifier(new LootItemCondition[]{LootTableIdCondition.builder(EntityType.WITHER.getDefaultLootTable().location()).build()}, key("modifier/wither_loot_addition")));
        add("ender_dragon_loot_modifier", new AddTableLootModifier(new LootItemCondition[]{LootTableIdCondition.builder(EntityType.ENDER_DRAGON.getDefaultLootTable().location()).build()}, key("modifier/ender_dragon_loot_addition")));
        add("warden_loot_modifier", new AddTableLootModifier(new LootItemCondition[]{LootTableIdCondition.builder(EntityType.WARDEN.getDefaultLootTable().location()).build()}, key("modifier/warden_loot_addition")));
        add("end_city_treasure_modifier", new AddTableLootModifier(new LootItemCondition[]{LootTableIdCondition.builder(BuiltInLootTables.END_CITY_TREASURE.location()).build()}, UELootTableProvider.LootTableModifier.END_CITY_TREASURE_MODIFIER));

    }

    private static ResourceKey<LootTable> key(String pName) {
        return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(UltimateEnchantment.MODID, pName));
    }


}
