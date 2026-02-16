package com.chen1335.ultimateEnchantment.mixins.minecraft;

import com.chen1335.ultimateEnchantment.mixinsAPI.minecraft.ILootParamsExtension;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LootParams.Builder.class)
public class LootParams$BuilderMixin {
    @ModifyReturnValue(method = "create", at = @At("RETURN"))
    private LootParams onCreate(LootParams lootParams, @Local(argsOnly = true) LootContextParamSet params) {
        ((ILootParamsExtension) lootParams).ue$setParamSet(params);
        return lootParams;
    }
}
