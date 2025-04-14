package com.chen1335.ultimateEnchantment.mixins.minecraft;

import com.chen1335.ultimateEnchantment.tags.UEEnchantmentTags;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.TagLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(TagLoader.class)
public class TagLoaderMixin {
    @Shadow
    @Final
    private String directory;

    @ModifyReturnValue(method = "load", at = @At("RETURN"))
    private Map<ResourceLocation, List<TagLoader.EntryWithSource>> load(Map<ResourceLocation, List<TagLoader.EntryWithSource>> original) {
        if (directory.equals("tags/enchantment")) {
            List<TagLoader.EntryWithSource> oldOrder = original.get(EnchantmentTags.TOOLTIP_ORDER.location());
            List<TagLoader.EntryWithSource> newOrder = new ArrayList<>();
            TagLoader.EntryWithSource ultimateEnchantments = null;
            TagLoader.EntryWithSource legendaryEnchantments = null;
            List<TagLoader.EntryWithSource> otherEnchantments = new ArrayList<>();
            for (TagLoader.EntryWithSource entryWithSource : oldOrder) {
                if (entryWithSource.entry().getId().equals(UEEnchantmentTags.ULTIMATE_ENCHANTMENT.location())) {
                    ultimateEnchantments = entryWithSource;
                } else if (entryWithSource.entry().getId().equals(UEEnchantmentTags.LEGENDARY_ENCHANTMENT.location())) {
                    legendaryEnchantments = entryWithSource;
                } else {
                    otherEnchantments.add(entryWithSource);
                }
            }

            newOrder.add(ultimateEnchantments);
            newOrder.add(legendaryEnchantments);
            newOrder.addAll(otherEnchantments);
            original.put(EnchantmentTags.TOOLTIP_ORDER.location(), newOrder);
        }
        return original;
    }

}
