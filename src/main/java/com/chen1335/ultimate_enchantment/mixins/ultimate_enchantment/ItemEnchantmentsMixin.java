package com.chen1335.ultimate_enchantment.mixins.ultimate_enchantment;

import com.chen1335.ultimate_enchantment.enchantment.UEEnchantments;
import com.chen1335.ultimate_enchantment.enchantment.enchantments.Ultimate;
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

    // 26.1：ItemEnchantments 的构造器去掉了 boolean showInTooltip
    // （该职责移交 TooltipDisplay 数据组件），故注入方法只接受 map 参数。
    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(Object2IntOpenHashMap<Holder<Enchantment>> enchantments, CallbackInfo ci) {
        enchantments.forEach((enchantmentHolder, lvl) -> {
            if (enchantmentHolder != null && enchantmentHolder.is(UEEnchantments.ULTIMATE.getKey())) {
                ue$addLevel = Math.round(Ultimate.LEVEL_ADD.calculate(Ultimate.buildBindings(lvl)));
            }
        });
    }

    @ModifyArgs(method = "addToTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;getFullname(Lnet/minecraft/core/Holder;I)Lnet/minecraft/network/chat/Component;"))
    private void addToTooltip(Args args) {
        Holder<Enchantment> holder = args.get(0);
        if (!holder.is(UEEnchantments.ULTIMATE.getKey()) && (int) args.get(1) > 0 && holder.value().getMaxLevel() > 1) {
            args.set(1, (int) args.get(1) + ue$addLevel);
        }
    }
}
