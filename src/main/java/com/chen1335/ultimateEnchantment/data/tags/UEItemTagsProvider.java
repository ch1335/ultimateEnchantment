package com.chen1335.ultimateEnchantment.data.tags;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.tags.UEItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import twilightforest.data.tags.ItemTagGenerator;

import java.util.concurrent.CompletableFuture;

public class UEItemTagsProvider extends ItemTagsProvider {
    public UEItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, UltimateEnchantment.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(UEItemTags.WEAPON_TOOLS)
                .addTag(ItemTags.BOW_ENCHANTABLE)
                .addTag(ItemTags.CROSSBOW_ENCHANTABLE)
                .addTag(ItemTags.SWORD_ENCHANTABLE)
                .addTag(ItemTags.AXES)
                //Twilight Forest Special Weapon
                .addOptionalTag(ItemTagGenerator.BLOCK_AND_CHAIN_ENCHANTABLE)

                .addTag(ItemTags.SHARP_WEAPON_ENCHANTABLE)
                .addTag(Tags.Items.RANGED_WEAPON_TOOLS)
                .addTag(Tags.Items.MELEE_WEAPON_TOOLS);
    }
}
