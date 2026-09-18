package com.chen1335.ultimate_enchantment.mixins.ultimate_enchantment;

import com.chen1335.ultimate_enchantment.mixinsAPI.minecraft.ILootParamsExtension;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.LootParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 把创建 {@code LootParams} 时使用的参数集记到结果对象上。
 * <p>
 * 26.1 的 {@code LootParams.Builder.create} 签名为 {@code create(ContextKeySet contextKeySet)}，
 * 形参名仍为 {@code contextKeySet}（原 {@code LootContextParamSet} 的更名），
 * 故 {@code @Local(argsOnly = true)} 的写法保持不变。
 */
@Mixin(LootParams.Builder.class)
public class LootParams$BuilderMixin {
    @ModifyReturnValue(method = "create", at = @At("RETURN"))
    private LootParams onCreate(LootParams lootParams, @Local(argsOnly = true) ContextKeySet params) {
        ((ILootParamsExtension) lootParams).ue$setParamSet(params);
        return lootParams;
    }
}
