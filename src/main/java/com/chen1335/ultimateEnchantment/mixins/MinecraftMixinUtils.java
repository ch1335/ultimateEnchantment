package com.chen1335.ultimateEnchantment.mixins;

import com.chen1335.ultimateEnchantment.tags.UEEnchantmentTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

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
