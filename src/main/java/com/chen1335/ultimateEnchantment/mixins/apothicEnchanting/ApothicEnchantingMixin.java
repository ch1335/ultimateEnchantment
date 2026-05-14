package com.chen1335.ultimateEnchantment.mixins.apothicEnchanting;

import com.chen1335.ultimateEnchantment.enchantment.enchatments.UEEnchantments;
import dev.shadowsoffire.apothic_enchanting.ApothicEnchanting;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ApothicEnchanting.class)
public class ApothicEnchantingMixin {
    @Inject(method = "getDefaultMaxLevel", at = @At("RETURN"), cancellable = true)
    private static void getDefaultMaxLevel(Holder<Enchantment> ench, CallbackInfoReturnable<Integer> cir) {
        List<ResourceKey<Enchantment>> list = List.of(
                UEEnchantments.ULTIMATE,
                UEEnchantments.LEGEND,
                UEEnchantments.VANQUISHER,
                UEEnchantments.LAST_STAND,
                UEEnchantments.ETERNAL,
                UEEnchantments.TEAR,
                UEEnchantments.LETHAL_TEMPO
        );
        if (list.stream().anyMatch(ench.getKey()::equals)) {
            cir.setReturnValue(ench.value().getMaxLevel());
        }
    }

}
