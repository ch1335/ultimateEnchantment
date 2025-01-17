package com.chen1335.ultimateEnchantment.mixins.touhouLittleMaid;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.mixinsAPI.touhouLittleMaid.hooks.EntityMaidHook;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityMaid.class, remap = false)
public class EntityMaidMixin {
    @Inject(method = "performRangedAttack", at = @At("RETURN"))
    private void performRangedAttack(LivingEntity target, float distanceFactor, CallbackInfo ci) {
        UltimateEnchantment.LOGGER.info("shoot");
        EntityMaidHook.performRangedAttack((EntityMaid) (Object) this, target, distanceFactor, ci);
    }
}
