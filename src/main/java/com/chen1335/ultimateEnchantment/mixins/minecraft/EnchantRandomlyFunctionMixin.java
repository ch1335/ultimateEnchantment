package com.chen1335.ultimateEnchantment.mixins.minecraft;

import com.chen1335.ultimateEnchantment.tags.UEEnchantmentTags;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(EnchantRandomlyFunction.class)
public class EnchantRandomlyFunctionMixin {
    @WrapOperation(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/Util;getRandomSafe(Ljava/util/List;Lnet/minecraft/util/RandomSource;)Ljava/util/Optional;"))
    private <T extends Holder<Enchantment>> Optional<T> getRandomSafe(List<T> selections, RandomSource random, Operation<Optional<T>> original) {
        ArrayList<Holder<Enchantment>> holders = new ArrayList<>(selections);
        holders.removeIf(holder -> holder.is(UEEnchantmentTags.ULTIMATE_ENCHANTMENT));
        return original.call(holders, random);
    }
}
