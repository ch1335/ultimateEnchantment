package com.chen1335.ultimateEnchantment.mixins.minecraft.client;

import com.chen1335.ultimateEnchantment.mobEffect.MobEffects;
import com.chen1335.ultimateEnchantment.mobEffect.effects.UnActiveVanquisher;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow
    public abstract Font getFont();

    @Inject(method = "renderEffects", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", shift = At.Shift.AFTER))
    private void onRenderOne(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci, @Local MobEffectInstance mobEffectInstance, @Local(name = "l1") int l1, @Local(name = "i1") int i1) {
        if (mobEffectInstance.getEffect().value() == MobEffects.UN_ACTIVE_VANQUISHER.value()) {
            UnActiveVanquisher.renderLevel(guiGraphics, getFont(), l1 + 3, i1 + 3, mobEffectInstance);

        }
    }
}
