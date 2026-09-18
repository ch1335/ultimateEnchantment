package com.chen1335.ultimate_enchantment.mixins.ultimate_enchantment;

import com.chen1335.ultimate_enchantment.enchantment.UEEnchantments;
import com.chen1335.ultimate_enchantment.enchantment.enchantments.QuickBait;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

import javax.script.SimpleBindings;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin {
    @Unique
    private float ue$fishSpeedMultiplier = 1;

    @Shadow
    @Nullable
    public abstract Player getPlayerOwner();


    @Inject(method = "<init>(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;II)V", at = @At("RETURN"))
    private void init(Player player, Level level, int luck, int lureSpeed, CallbackInfo ci) {

        if (player != null) {
            ItemStack rod = null;
            if (player.getMainHandItem().is(ItemTags.FISHING_ENCHANTABLE)) {
                rod = player.getMainHandItem();
            } else if (player.getOffhandItem().is(ItemTags.FISHING_ENCHANTABLE)) {
                rod = player.getOffhandItem();
            }
            if (rod != null) {
                int enchantmentLevel = UEEnchantments.QUICK_BAIT.getEnchantmentLevel(rod, player.level());
                if (enchantmentLevel > 0) {
                    SimpleBindings bindings = UEEnchantments.buildBindings(enchantmentLevel);
                    ue$fishSpeedMultiplier += QuickBait.SPEED.calculate( bindings);
                }
            }
        }
    }

    @ModifyExpressionValue(method = "catchingFish", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/projectile/FishingHook;timeUntilHooked:I", ordinal = 2))
    private int catchingFish(int original, @Local int i) {
        Player player = getPlayerOwner();
        if (player != null) {
            if (player.getRandom().nextFloat() < ue$fishSpeedMultiplier - 1) {
                return original - i;
            }
        }
        return original;
    }

}
