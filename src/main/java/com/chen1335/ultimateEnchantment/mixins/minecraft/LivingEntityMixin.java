package com.chen1335.ultimateEnchantment.mixins.minecraft;

import com.chen1335.ultimateEnchantment.dataComponentType.UEDataComponentTypes;
import com.chen1335.ultimateEnchantment.mobEffect.MobEffects;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    @Nullable
    public abstract MobEffectInstance getEffect(Holder<MobEffect> effect);

    @Shadow
    public abstract boolean removeEffect(Holder<MobEffect> effect);

    @Shadow
    public abstract boolean addEffect(MobEffectInstance effectInstance);

    @Shadow
    protected abstract void onEffectUpdated(MobEffectInstance effectInstance, boolean forced, @org.jetbrains.annotations.Nullable Entity entity);

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        LivingEntity living = (LivingEntity) (Object) this;
        if (!(living instanceof Player)) {
            living.getArmorSlots().forEach(itemStack -> {
                if (!itemStack.isEmpty() && itemStack.isEnchanted()) {
                    itemStack.set(UEDataComponentTypes.USER_HEALTH, Math.max(living.getHealth(), 0));
                    itemStack.set(UEDataComponentTypes.USER_MAX_HEALTH, Math.max(living.getMaxHealth(), 0));
                }
            });
            living.getHandSlots().forEach(itemStack -> {
                if (!itemStack.isEmpty() && itemStack.isEnchanted()) {
                    itemStack.set(UEDataComponentTypes.USER_HEALTH, Math.max(living.getHealth(), 0));
                    itemStack.set(UEDataComponentTypes.USER_MAX_HEALTH, Math.max(living.getMaxHealth(), 0));
                }
            });
        }
    }

    @Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At("RETURN"))
    private void onAddEffect(MobEffectInstance effectInstance, Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            MobEffectInstance unActiveVanquisher = this.getEffect(MobEffects.UN_ACTIVE_VANQUISHER);
            MobEffectInstance activeVanquisher = this.getEffect(MobEffects.ACTIVE_VANQUISHER);

            if (!(effectInstance.getEffect().value() == MobEffects.UN_ACTIVE_VANQUISHER.value() || effectInstance.getEffect().value() == MobEffects.ACTIVE_VANQUISHER.value())) {
                return;
            }

            if (activeVanquisher != null) {
                this.removeEffect(MobEffects.UN_ACTIVE_VANQUISHER);
                this.addEffect(new MobEffectInstance(MobEffects.ACTIVE_VANQUISHER, 400, 0, false, false, true));
                return;
            }

            if (unActiveVanquisher == null) {
                return;
            }

            if (unActiveVanquisher.getAmplifier() + 1 < 10) {
                unActiveVanquisher.update(new MobEffectInstance(MobEffects.UN_ACTIVE_VANQUISHER, 400, unActiveVanquisher.getAmplifier(), false, false, true));
                this.onEffectUpdated(unActiveVanquisher, true, entity);
            } else {
                this.removeEffect(MobEffects.UN_ACTIVE_VANQUISHER);
                this.addEffect(new MobEffectInstance(MobEffects.ACTIVE_VANQUISHER, 400, 0, false, false, true));
            }
        }
    }
}
