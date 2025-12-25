package com.chen1335.ultimateEnchantment.mixins.minecraft;

import com.chen1335.ultimateEnchantment.enchantment.enchatments.UEEnchantments;
import com.chen1335.ultimateEnchantment.mixins.MinecraftMixinUtils;
import com.chen1335.ultimateEnchantment.utils.UEEnchantmentHelper;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {

    @Inject(method = "getFullname", at = @At("RETURN"))
    private static void getFullName(Holder<Enchantment> pEnchantment, int pLevel, CallbackInfoReturnable<Component> cir) {
        MinecraftMixinUtils.EnchantmentMixin.getFullName(pEnchantment, pLevel, cir);
    }


    @Inject(method = "isSupportedItem", at = @At("RETURN"), cancellable = true)
    private void isSupportedItem(ItemStack item, CallbackInfoReturnable<Boolean> cir) {
        UEEnchantmentHelper.getEnchantment(UEEnchantments.ETERNAL).ifPresent(holder -> {
            if (holder.value() == (Object) this) {
                cir.setReturnValue(true);
            }
        });
    }

}
