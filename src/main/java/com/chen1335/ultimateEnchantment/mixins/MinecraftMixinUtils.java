package com.chen1335.ultimateEnchantment.mixins;

import com.chen1335.ultimateEnchantment.tags.UEEnchantmentTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class MinecraftMixinUtils {

    public static class EnchantmentMixin{
        public static void getFullName(Holder<Enchantment> pEnchantment, int pLevel, CallbackInfoReturnable<Component> cir){
            if (pEnchantment.tags().toList().contains(UEEnchantmentTags.LEGENDARY_ENCHANTMENT)){
                MutableComponent component = (MutableComponent) cir.getReturnValue();
                component.withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD));
            }

            if (pEnchantment.tags().toList().contains(UEEnchantmentTags.ULTIMATE_ENCHANTMENT)){
                MutableComponent component = (MutableComponent) cir.getReturnValue();
                component.withStyle(Style.EMPTY.withColor(ChatFormatting.LIGHT_PURPLE));
            }
        }
    }
}
