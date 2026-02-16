package com.chen1335.ultimateEnchantment.mixins.minecraft;

import com.chen1335.ultimateEnchantment.mixinsAPI.minecraft.ILootParamsExtension;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LootParams.class)
public class LootParamsMixin implements ILootParamsExtension {
    @Unique
    private LootContextParamSet ue$paramSet = null;

    public LootContextParamSet ue$getParamSet() {
        return ue$paramSet;
    }

    public void ue$setParamSet(LootContextParamSet paramSet) {
        this.ue$paramSet = paramSet;
    }
}
