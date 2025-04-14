package com.chen1335.ultimateEnchantment.mixins.minecraft;

import com.chen1335.ultimateEnchantment.dataComponentType.UEDataComponentTypes;
import com.chen1335.ultimateEnchantment.enchantment.enchatments.UEEnchantments;
import com.chen1335.ultimateEnchantment.utils.ItemEnchantmentHelper;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
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

import java.util.function.Consumer;


@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements IItemStackExtension, MutableDataComponentHolder {


    @Shadow
    public abstract boolean isEnchanted();

    @Shadow
    public abstract Item getItem();

    @Inject(method = "<init>(Lnet/minecraft/world/level/ItemLike;ILnet/minecraft/core/component/PatchedDataComponentMap;)V", at = @At("RETURN"))
    private void init(ItemLike pItem, int pCount, PatchedDataComponentMap pComponents, CallbackInfo ci) {

    }

    @Inject(method = "inventoryTick", at = @At("HEAD"))
    private void tick(Level pLevel, Entity pEntity, int pInventorySlot, boolean pIsCurrentItem, CallbackInfo ci) {
        if (pEntity instanceof LivingEntity livingEntity && this.isEnchanted()) {
            this.set(UEDataComponentTypes.USER_HEALTH, Math.max(livingEntity.getHealth(), 0));
            this.set(UEDataComponentTypes.USER_MAX_HEALTH, Math.max(livingEntity.getMaxHealth(), 0));
        }
    }

    @Inject(method = "setDamageValue", at = @At("HEAD"), cancellable = true)
    private void setDamageValue(int damage, CallbackInfo ci) {
        if (ItemEnchantmentHelper.getEnchantmentLevel((ItemStack) (Object) this, UEEnchantments.ETERNAL) > 0) {
            this.getItem().setDamage((ItemStack) (Object) this, 0);
            ci.cancel();
        }
    }

    @Inject(method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V", at = @At("HEAD"), cancellable = true)
    private void hurtAndBreak(int p_220158_, ServerLevel p_346256_, LivingEntity p_220160_, Consumer<Item> p_348596_, CallbackInfo ci) {
        if (ItemEnchantmentHelper.getEnchantmentLevel((ItemStack) (Object) this, UEEnchantments.ETERNAL) > 0) {
            this.getItem().setDamage((ItemStack) (Object) this, 0);
            ci.cancel();
        }
    }
}
