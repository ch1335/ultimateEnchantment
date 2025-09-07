package com.chen1335.ultimateEnchantment.mixins.minecraft;

import com.chen1335.ultimateEnchantment.enchantment.effectComponents.UEEnchantmentEffectComponents;
import com.chen1335.ultimateEnchantment.enchantment.enchatments.UEEnchantments;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ItemEnchantments.class)
public class ItemEnchantmentsMixin {

    @Unique
    private int ue$addLevel = 0;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(Object2IntOpenHashMap<Holder<Enchantment>> enchantments, boolean showInTooltip, CallbackInfo ci) {
        enchantments.forEach((enchantmentHolder, integer) -> {
            if (enchantmentHolder != null && enchantmentHolder.is(UEEnchantments.ULTIMATE)) {
                ue$addLevel = (int) enchantmentHolder.value().effects().get(UEEnchantmentEffectComponents.ULTIMATE.get()).calculate(integer);
            }
        });
    }

    @ModifyArgs(method = "addToTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;getFullname(Lnet/minecraft/core/Holder;I)Lnet/minecraft/network/chat/Component;"))
    private void addToTooltip(Args args) {
        Holder<Enchantment> holder = args.get(0);
        if (!holder.value().effects().has(UEEnchantmentEffectComponents.ULTIMATE.value()) && (int) args.get(1) > 0 && holder.value().getMaxLevel() > 1) {
            args.set(1, (int) args.get(1) + ue$addLevel);
        }
    }
}
