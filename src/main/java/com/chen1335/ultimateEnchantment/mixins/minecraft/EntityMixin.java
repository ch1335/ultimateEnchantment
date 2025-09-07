package com.chen1335.ultimateEnchantment.mixins.minecraft;

import com.chen1335.ultimateEnchantment.enchantment.enchatments.UEEnchantments;
import com.chen1335.ultimateEnchantment.mixinsAPI.minecraft.IEntityMixin;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements IEntityMixin {
    @Unique
    public boolean ue$isLethalTempoShooting = false;

    @Override
    public boolean ue$isLethalTempoShooting() {
        return ue$isLethalTempoShooting;
    }

    @Override
    public void ue$setLethalTempoShooting(boolean flag) {
        ue$isLethalTempoShooting = flag;
    }

    @Shadow
    public abstract void setNoGravity(boolean pNoGravity);

    @Shadow
    public abstract void setDeltaMovement(double pX, double pY, double pZ);

    @Shadow
    public abstract Level level();

    @Shadow
    public abstract void setGlowingTag(boolean hasGlowingTag);

    @Inject(method = "onBelowWorld", at = @At("HEAD"), cancellable = true)
    private void onBelowWorld(CallbackInfo ci) {
        if ((Entity) (Object) this instanceof ItemEntity itemEntity && itemEntity.getItem().getEnchantmentLevel(level().registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(UEEnchantments.ETERNAL)) > 0) {
            this.setNoGravity(true);
            this.setDeltaMovement(0, 1.5, 0);
            this.setGlowingTag(true);
            ci.cancel();
        }
    }

    @Inject(method = {"discard", "kill"}, at = @At("HEAD"), cancellable = true)
    private void discard(CallbackInfo ci) {
        if ((Entity) (Object) this instanceof ItemEntity itemEntity && itemEntity.getItem().getEnchantmentLevel(level().registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(UEEnchantments.ETERNAL)) > 0) {
            ci.cancel();
        }
    }
}
