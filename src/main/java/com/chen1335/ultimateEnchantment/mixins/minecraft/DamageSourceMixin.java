package com.chen1335.ultimateEnchantment.mixins.minecraft;

import com.chen1335.ultimateEnchantment.mixinsAPI.minecraft.IDamageSourceMixin;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DamageSource.class)
public class DamageSourceMixin implements IDamageSourceMixin {
    @Unique
    private Entity ue$directAttackedEntity = null;

    public Entity ue$getDirectAttackedEntity() {
        return ue$directAttackedEntity;
    }

    public void ue$setDirectAttackedEntity(Entity directAttackedEntity) {
        this.ue$directAttackedEntity = directAttackedEntity;

    }
}
