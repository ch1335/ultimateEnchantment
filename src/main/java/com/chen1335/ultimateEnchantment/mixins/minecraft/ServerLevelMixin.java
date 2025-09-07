package com.chen1335.ultimateEnchantment.mixins.minecraft;

import com.chen1335.ultimateEnchantment.mixinsAPI.minecraft.IEntityMixin;
import com.chen1335.ultimateEnchantment.mixinsAPI.minecraft.IProjectileMixin;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {

    @Inject(method = "addEntity", at = @At("HEAD"))
    private void addEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof Projectile projectile && projectile.getOwner() != null && ((IEntityMixin) projectile.getOwner()).ue$isLethalTempoShooting()) {
            IProjectileMixin projectileMixin = ((IProjectileMixin) projectile);
            if (projectile instanceof AbstractArrow arrow) {
                arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }
            projectileMixin.ue$setLethalTempoAdditionArrow(true);
        }
    }
}
