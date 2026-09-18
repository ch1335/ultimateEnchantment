package com.chen1335.ultimate_enchantment.mixins.ultimate_enchantment;

import com.chen1335.ultimate_enchantment.mixinsAPI.minecraft.ILootParamsExtension;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.LootParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LootParams.class)
public class LootParamsMixin implements ILootParamsExtension {
    @Unique
    private ContextKeySet ue$paramSet = null;

    public ContextKeySet ue$getParamSet() {
        return ue$paramSet;
    }

    public void ue$setParamSet(ContextKeySet paramSet) {
        this.ue$paramSet = paramSet;
    }
}
