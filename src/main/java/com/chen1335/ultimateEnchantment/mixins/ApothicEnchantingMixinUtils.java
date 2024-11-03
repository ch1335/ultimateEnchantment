package com.chen1335.ultimateEnchantment.mixins;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class ApothicEnchantingMixinUtils {
    public static class ItemStackMixin$Mixin{
        public static void getTagOrEmpty(HolderLookup.Provider pRegistries, ResourceKey<Registry<Enchantment>> pRegistryKey, TagKey<Enchantment> pKey, CallbackInfoReturnable<HolderSet<Enchantment>> cir) {
            MinecraftMixinUtils.ItemEnchantmentsMixin.getTagOrEmpty(pRegistries, pRegistryKey, pKey, cir);
        }
    }
}
