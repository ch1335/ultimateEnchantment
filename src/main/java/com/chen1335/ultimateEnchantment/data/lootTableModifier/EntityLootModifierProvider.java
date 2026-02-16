package com.chen1335.ultimateEnchantment.data.lootTableModifier;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;

import java.util.concurrent.CompletableFuture;

public class EntityLootModifierProvider extends GlobalLootModifierProvider {
    public EntityLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String modid) {
        super(output, registries, modid);
    }

    @Override
    protected void start() {

    }

    private static ResourceKey<LootTable> key(String pName) {
        return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(UltimateEnchantment.MODID, pName));
    }


}
