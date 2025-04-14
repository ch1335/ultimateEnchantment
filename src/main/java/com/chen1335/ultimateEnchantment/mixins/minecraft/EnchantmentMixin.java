package com.chen1335.ultimateEnchantment.mixins.minecraft;

import com.chen1335.ultimateEnchantment.enchantment.enchatments.UEEnchantments;
import com.chen1335.ultimateEnchantment.mixins.MinecraftMixinUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.CommonHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(Enchantment.class)
public class EnchantmentMixin {

    @Inject(method = "getFullname", at = @At("RETURN"))
    private static void getFullName(Holder<Enchantment> pEnchantment, int pLevel, CallbackInfoReturnable<Component> cir) {
        MinecraftMixinUtils.EnchantmentMixin.getFullName(pEnchantment, pLevel, cir);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(ServerLevel pLevel, int pEnchantmentLevel, EnchantedItemInUse pItem, Entity pEntity, CallbackInfo ci) {
        if (pEntity instanceof LivingEntity livingEntity) {

        }
    }

    @Inject(method = "isSupportedItem", at = @At("RETURN"), cancellable = true)
    private void isSupportedItem(ItemStack item, CallbackInfoReturnable<Boolean> cir) {
        if ((item.getCount() == 1) && (Objects.requireNonNull(CommonHooks.resolveLookup(Registries.ENCHANTMENT)).getOrThrow(UEEnchantments.ETERNAL).value() == (Object) this)) {
            cir.setReturnValue(true);
        }
    }

}
