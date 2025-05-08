package com.chen1335.ultimateEnchantment.mixins.minecraft;

import com.chen1335.ultimateEnchantment.mixinsAPI.minecraft.IItemFishedEventMixin;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ItemFishedEvent.class)
public class ItemFishedEventMixin implements IItemFishedEventMixin {
    @Unique
    private List<ItemStack> ue$originalDrops = new ArrayList<>();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(List<ItemStack> stacks, int rodDamage, FishingHook hook, CallbackInfo ci) {
        ue$originalDrops = stacks;
    }

    public List<ItemStack> getUe$originalDrops() {
        return ue$originalDrops;
    }
}
