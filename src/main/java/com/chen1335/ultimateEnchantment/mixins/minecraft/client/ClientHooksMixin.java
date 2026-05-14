package com.chen1335.ultimateEnchantment.mixins.minecraft.client;

import com.chen1335.ultimateEnchantment.enchantment.enchatments.UEEnchantments;
import com.chen1335.ultimateEnchantment.utils.UEEnchantmentHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.ClientHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientHooks.class)
public class ClientHooksMixin {
    @Inject(method = "shouldCauseReequipAnimation", at = @At("RETURN"), cancellable = true)
    private static void shouldCauseReequipAnimation(ItemStack from, ItemStack _to, int slot, CallbackInfoReturnable<Boolean> cir) {
        UEEnchantmentHelper.runIfEnchantmentExist(UEEnchantments.LETHAL_TEMPO, holder -> {
            if (from.is(_to.getItem()) && from.getEnchantmentLevel(holder) > 0 && !Minecraft.getInstance().player.getUseItem().isEmpty()) {
                cir.setReturnValue(false);
            }
        });
    }
}
