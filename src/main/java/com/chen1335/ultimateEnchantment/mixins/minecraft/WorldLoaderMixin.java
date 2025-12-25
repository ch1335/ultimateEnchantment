package com.chen1335.ultimateEnchantment.mixins.minecraft;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.config.EnchantmentEnableInfo;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.Resource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(WorldLoader.class)
public class WorldLoaderMixin {
    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/WorldLoader;loadAndReplaceLayer(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/core/LayeredRegistryAccess;Lnet/minecraft/server/RegistryLayer;Ljava/util/List;)Lnet/minecraft/core/LayeredRegistryAccess;"))
    private static void beforeLoadResource(WorldLoader.InitConfig initConfig, WorldLoader.WorldDataSupplier<?> worldDataSupplier, WorldLoader.ResultFactory<?, ?> resultFactory, Executor backgroundExecutor, Executor gameExecutor, CallbackInfoReturnable<CompletableFuture<?>> cir, @Local CloseableResourceManager closeableresourcemanager) {
        Map<ResourceLocation, Resource> list = closeableresourcemanager.listResources("enchantment", resourceLocation -> resourceLocation.getNamespace().equals(UltimateEnchantment.MODID));
        EnchantmentEnableInfo.registryEnchantments = list.keySet();
        EnchantmentEnableInfo.staticLoad();
    }
}
