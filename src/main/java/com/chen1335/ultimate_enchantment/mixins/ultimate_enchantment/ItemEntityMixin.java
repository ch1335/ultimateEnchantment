package com.chen1335.ultimate_enchantment.mixins.ultimate_enchantment;

import com.chen1335.ultimate_enchantment.enchantment.UEEnchantments;
import com.chen1335.ultimate_enchantment.utils.UEEnchantmentHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    public ItemEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract ItemStack getItem();

    @Shadow
    public int lifespan;

    @Shadow
    private int health;

    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;DDD)V", at = @At("RETURN"))
    private void init(Level pLevel, double pPosX, double pPosY, double pPosZ, ItemStack pItemStack, double pDeltaX, double pDeltaY, double pDeltaZ, CallbackInfo ci) {
        UEEnchantmentHelper.getEnchantment(UEEnchantments.ETERNAL.getKey()).ifPresent(holder -> {
            if (pItemStack.getEnchantmentLevel(holder) > 0) {
                this.lifespan = Integer.MAX_VALUE;
                this.health = Integer.MAX_VALUE;
            }
        });
    }

    // 26.1：ItemEntity#hurt 已移除，改为 hurtServer(ServerLevel, DamageSource, float)。
    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource pSource, float pAmount, CallbackInfoReturnable<Boolean> cir) {
        UEEnchantmentHelper.getEnchantment(UEEnchantments.ETERNAL.getKey()).ifPresent(holder -> {
            if (getItem().getEnchantmentLevel(holder) > 0) {
                cir.setReturnValue(false);
            }
        });
    }
}
