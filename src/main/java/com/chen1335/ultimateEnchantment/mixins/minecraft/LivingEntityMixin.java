package com.chen1335.ultimateEnchantment.mixins.minecraft;

import com.chen1335.ultimateEnchantment.enchantment.EnchantmentEffectsHook;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow public abstract float getHealth();

    @Unique
    private float ue$oldHealth;
    @Inject(method = "tick",at = @At("HEAD"))
    private void tick(CallbackInfo ci){
        if (ue$oldHealth != this.getHealth()) {
            EnchantmentEffectsHook.onLivingHealthChange((LivingEntity) (Object)this);
            ue$oldHealth=this.getHealth();
        }
    }
}
