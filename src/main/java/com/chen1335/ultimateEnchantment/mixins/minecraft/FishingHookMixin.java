package com.chen1335.ultimateEnchantment.mixins.minecraft;

import com.chen1335.ultimateEnchantment.enchantment.enchatments.UEEnchantments;
import com.chen1335.ultimateEnchantment.enchantment.specialEnchantEffects.FishingEffect;
import com.chen1335.ultimateEnchantment.utils.ItemEnchantmentHelper;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin {
    @Unique
    private float ue$fishSpeedMultiplier = 1;

    @Shadow
    @Nullable
    public abstract Player getPlayerOwner();

    @Inject(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/neoforged/bus/api/IEventBus;post(Lnet/neoforged/bus/api/Event;)Lnet/neoforged/bus/api/Event;"))
    private void retrieve(ItemStack stack, CallbackInfoReturnable<Integer> cir, @Local List<ItemStack> drops) {
        FishingEffect.retrieve(stack, getPlayerOwner(), (FishingHook) (Object)this, drops);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;II)V", at = @At("RETURN"))
    private void init(Player player, Level level, int luck, int lureSpeed, CallbackInfo ci) {
        ItemStack rod = null;
        if (player != null) {
            if (player.getMainHandItem().is(ItemTags.FISHING_ENCHANTABLE)) {
                rod = player.getMainHandItem();
            } else if (player.getOffhandItem().is(ItemTags.FISHING_ENCHANTABLE)) {
                rod = player.getOffhandItem();
            }

            if (rod != null) {
                int quickBait = ItemEnchantmentHelper.getEnchantmentLevel(rod, UEEnchantments.QUICK_BAIT);
                ue$fishSpeedMultiplier = (float) (ue$fishSpeedMultiplier + quickBait * 0.05);
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
