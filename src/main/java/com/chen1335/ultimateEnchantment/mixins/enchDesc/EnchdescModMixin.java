package com.chen1335.ultimateEnchantment.mixins.enchDesc;

import com.chen1335.ultimateEnchantment.client.EnchantmentSpecialDesc;
import net.darkhax.enchdesc.common.impl.EnchdescMod;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchdescMod.class)
public abstract class EnchdescModMixin {


    @Inject(method = "getDescription(Lnet/minecraft/core/Holder;Lnet/minecraft/resources/ResourceLocation;I)Lnet/minecraft/network/chat/MutableComponent;", cancellable = true, at = @At("HEAD"))
    private void onInsertDescriptions(Holder<Enchantment> enchantment, ResourceLocation id, int level, CallbackInfoReturnable<MutableComponent> cir) {
        MutableComponent newDescription = EnchantmentSpecialDesc.getNewDescription(enchantment, level);
        if (newDescription != null) {
            cir.setReturnValue(newDescription);
            cir.cancel();
        }

    }
}
