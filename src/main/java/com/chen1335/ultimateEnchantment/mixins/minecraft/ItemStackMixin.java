package com.chen1335.ultimateEnchantment.mixins.minecraft;

import com.chen1335.ultimateEnchantment.dataComponentType.UEDataComponentTypes;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import net.neoforged.neoforge.common.extensions.IItemStackExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements IItemStackExtension , MutableDataComponentHolder {


    @Shadow public abstract boolean isEnchanted();

    @Inject(method = "<init>(Lnet/minecraft/world/level/ItemLike;ILnet/minecraft/core/component/PatchedDataComponentMap;)V",at = @At("RETURN"))
    private void init(ItemLike pItem, int pCount, PatchedDataComponentMap pComponents, CallbackInfo ci){

    }

    @Inject(method = "inventoryTick",at = @At("HEAD"))
    private void tick(Level pLevel, Entity pEntity, int pInventorySlot, boolean pIsCurrentItem, CallbackInfo ci){
        if (pEntity instanceof LivingEntity livingEntity && this.isEnchanted()) {
            this.set(UEDataComponentTypes.USER_HEALTH, Math.max(livingEntity.getHealth(),0));
            this.set(UEDataComponentTypes.USER_MAX_HEALTH,Math.max(livingEntity.getMaxHealth(),0));
        }
    }
}
