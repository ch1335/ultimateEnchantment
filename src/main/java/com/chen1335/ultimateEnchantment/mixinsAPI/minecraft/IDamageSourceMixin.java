package com.chen1335.ultimateEnchantment.mixinsAPI.minecraft;

import net.minecraft.world.entity.Entity;

public interface IDamageSourceMixin {
    Entity ue$getDirectAttackedEntity();

    void ue$setDirectAttackedEntity(Entity directAttackedEntity);

    default boolean isDirectAttackedEntity(Entity entity) {
        return ue$getDirectAttackedEntity() == entity;
    }
}
