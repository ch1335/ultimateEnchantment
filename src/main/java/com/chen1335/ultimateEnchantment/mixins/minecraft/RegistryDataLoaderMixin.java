package com.chen1335.ultimateEnchantment.mixins.minecraft;

import com.chen1335.ultimateEnchantment.config.EnchantmentEnableInfo;
import com.mojang.serialization.Decoder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(RegistryDataLoader.class)
public class RegistryDataLoaderMixin {
    @Inject(method = "loadContentsFromNetwork", at = @At(value = "RETURN"))
    private static void onLoadContentsFromNetwork(Map<ResourceKey<? extends Registry<?>>, List<RegistrySynchronization.PackedRegistryEntry>> elements, ResourceProvider resourceProvider, RegistryOps.RegistryInfoLookup registryInfoLookup, WritableRegistry<?> registry, Decoder<?> codec, Map<ResourceKey<?>, Exception> loadingErrors, CallbackInfo ci) {
        EnchantmentEnableInfo.enableInfo.clear();
    }
}
