package com.chen1335.ultimateEnchantment.mixins.minecraft;

import com.chen1335.ultimateEnchantment.dataComponentType.UEDataComponentTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow public abstract void tick();

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        LivingEntity living = (LivingEntity)(Object) this;
        if (!(living instanceof Player)) {
            living.getArmorSlots().forEach(itemStack -> {
                if (!itemStack.isEmpty() && itemStack.isEnchanted()) {
                    itemStack.set(UEDataComponentTypes.USER_HEALTH, Math.max(living.getHealth(),0));
                    itemStack.set(UEDataComponentTypes.USER_MAX_HEALTH,Math.max(living.getMaxHealth(),0));
                }
            });
            living.getHandSlots().forEach(itemStack -> {
                if (!itemStack.isEmpty() && itemStack.isEnchanted()) {
                    itemStack.set(UEDataComponentTypes.USER_HEALTH, Math.max(living.getHealth(),0));
                    itemStack.set(UEDataComponentTypes.USER_MAX_HEALTH,Math.max(living.getMaxHealth(),0));
                }
            });
        }
    }
}
