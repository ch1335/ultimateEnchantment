package com.chen1335.ultimateEnchantment.mixins.minecraft;

import com.chen1335.ultimateEnchantment.mixinsAPI.minecraft.IProjectileMixin;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Projectile.class)
public class ProjectileMixin implements IProjectileMixin {
    @Unique
    private boolean ue$isLethalTempoFirstArrow = false;
    @Unique
    private boolean ue$isLethalTempoAdditionArrow = false;

    public boolean ue$isLethalTempoFirstArrow() {
        return ue$isLethalTempoFirstArrow;
    }

    public void ue$setLethalTempoFirstArrow(boolean ue$isLethalTempoFirstArrow) {
        this.ue$isLethalTempoFirstArrow = ue$isLethalTempoFirstArrow;
    }

    public boolean ue$isLethalTempoAdditionArrow() {
        return ue$isLethalTempoAdditionArrow;
    }

    public void ue$setLethalTempoAdditionArrow(boolean ue$isLethalTempoAdditionArrow) {
        this.ue$isLethalTempoAdditionArrow = ue$isLethalTempoAdditionArrow;
    }
}
