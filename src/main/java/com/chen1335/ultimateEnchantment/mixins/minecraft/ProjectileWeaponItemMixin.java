package com.chen1335.ultimateEnchantment.mixins.minecraft;

import com.chen1335.ultimateEnchantment.mixinsAPI.minecraft.IItemStackMixin;
import com.chen1335.ultimateEnchantment.mixinsAPI.minecraft.IProjectileMixin;
import com.chen1335.ultimateEnchantment.mixinsAPI.minecraft.IProjectileWeaponItemMixin;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProjectileWeaponItem.class)
public abstract class ProjectileWeaponItemMixin implements IProjectileWeaponItemMixin {

    @Inject(method = "createProjectile", at = @At("RETURN"))
    private void createProjectile(Level level, LivingEntity shooter, ItemStack weapon, ItemStack ammo, boolean isCrit, CallbackInfoReturnable<Projectile> cir) {
        IItemStackMixin iItemStackMixin = (IItemStackMixin) (Object) weapon;
        IProjectileMixin projectileMixin = ((IProjectileMixin) cir.getReturnValue());
        if (iItemStackMixin != null && iItemStackMixin.ue$isLethalTempoShoot()) {
            if (cir.getReturnValue() instanceof AbstractArrow arrow) {
                arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }
            projectileMixin.ue$setLethalTempoAdditionArrow(true);
        }
    }
}
