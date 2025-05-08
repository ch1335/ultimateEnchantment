package com.chen1335.ultimateEnchantment.mixins.minecraft;

import com.chen1335.ultimateEnchantment.mixinsAPI.minecraft.IItemStackMixin;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {


    @Inject(method = "processAmmoUse", at = @At("RETURN"), cancellable = true)
    private static void processAmmoUse(ServerLevel level, ItemStack weapon, ItemStack ammo, int count, CallbackInfoReturnable<Integer> cir) {
        IItemStackMixin iItemStackMixin = (IItemStackMixin) (Object) weapon;
        if (cir.getReturnValue() > 0 && iItemStackMixin != null && iItemStackMixin.ue$isLethalTempoShoot()) {
            cir.setReturnValue(0);
        }
    }
}
