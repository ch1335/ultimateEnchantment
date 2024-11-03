package com.chen1335.ultimateEnchantment.mixins.apothicEnchanting;

import com.chen1335.ultimateEnchantment.tags.UEEnchantmentTags;
import dev.shadowsoffire.apothic_enchanting.util.TooltipUtil;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TooltipUtil.class)
public class TooltipUtilMixin {
    @Inject(method = "applyOverMaxLevelColor",at = @At("HEAD"), cancellable = true)
    private static void applyOverMaxLevelColor(Holder<Enchantment> ench, int level, Component name, CallbackInfo ci){
        if (ench.tags().anyMatch(enchantmentTagKey -> enchantmentTagKey == UEEnchantmentTags.USE_CUSTOM_COLOR)) {
            ci.cancel();
        }
    }
}
