package com.chen1335.ultimateEnchantment.mixinsAPI.minecraft;

import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

public interface ILootParamsExtension {
    LootContextParamSet ue$getParamSet();

    void ue$setParamSet(LootContextParamSet paramSet);
}
