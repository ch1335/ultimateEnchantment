package com.chen1335.ultimateEnchantment.mixins.apothicEnchanting;

import com.chen1335.ultimateEnchantment.mixins.ApothicEnchantingMixinUtils;
import dev.shadowsoffire.apothic_enchanting.mixin.ItemStackMixin;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {ItemStack.class, ItemStackMixin.class}, priority = 501)
public class ItemStackMixin$Mixin {
    @Inject(method = "getTagOrEmpty", at = @At("RETURN"), cancellable = true)
    private static void getTagOrEmpty(HolderLookup.Provider pRegistries, ResourceKey<Registry<Enchantment>> pRegistryKey, TagKey<Enchantment> pKey, CallbackInfoReturnable<HolderSet<Enchantment>> cir) {
        ApothicEnchantingMixinUtils.ItemStackMixin$Mixin.getTagOrEmpty(pRegistries, pRegistryKey, pKey, cir);
    }
}
