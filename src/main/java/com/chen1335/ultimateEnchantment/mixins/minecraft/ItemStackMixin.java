package com.chen1335.ultimateEnchantment.mixins.minecraft;

import com.chen1335.ultimateEnchantment.client.EnchantmentSpecialDesc;
import com.chen1335.ultimateEnchantment.dataComponentType.UEDataComponentTypes;
import com.chen1335.ultimateEnchantment.enchantment.enchatments.UEEnchantments;
import com.chen1335.ultimateEnchantment.mixinsAPI.minecraft.IItemStackMixin;
import com.chen1335.ultimateEnchantment.utils.ItemEnchantmentHelper;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import net.neoforged.neoforge.common.extensions.IItemStackExtension;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.function.Consumer;


@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements IItemStackExtension, MutableDataComponentHolder, IItemStackMixin {
    @Unique
    private boolean ue$isLethalTempoShoot = false;

    public boolean ue$isLethalTempoShoot() {
        return ue$isLethalTempoShoot;
    }

    public void ue$setLethalTempoShoot(boolean ue$isLethalTempoShoot) {
        this.ue$isLethalTempoShoot = ue$isLethalTempoShoot;
    }

    @Shadow
    public abstract boolean isEnchanted();

    @Shadow
    public abstract Item getItem();

    @Shadow
    @Nullable
    public abstract <T> T set(@NotNull DataComponentType<? super T> component, @org.jetbrains.annotations.Nullable T value);

    @Inject(method = "addToTooltip", at = @At("RETURN"))
    private void enchantmentToolTip(DataComponentType<ItemEnchantments> component, Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag, CallbackInfo ci) {

        if (component == DataComponents.STORED_ENCHANTMENTS && getItem() == Items.ENCHANTED_BOOK) {
            ItemEnchantments itemEnchantments = this.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
            if (itemEnchantments.size() == 1) {
                for (Object2IntMap.Entry<Holder<Enchantment>> holderEntry : itemEnchantments.entrySet()) {
                    MutableComponent newDescription = EnchantmentSpecialDesc.getNewDescription(holderEntry.getKey(), holderEntry.getIntValue());
                    if (newDescription != null) {
                        tooltipAdder.accept(newDescription);
                    }
                }
            }
        }
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
            this.getItem().setDamage(ItemStack.class.cast(this), 0);
            ci.cancel();
        }
    }

    @Inject(method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V", at = @At("HEAD"), cancellable = true)
    private void hurtAndBreak(int p_220158_, ServerLevel p_346256_, LivingEntity p_220160_, Consumer<Item> p_348596_, CallbackInfo ci) {
        if (ItemEnchantmentHelper.getEnchantmentLevel((ItemStack) (Object) this, UEEnchantments.ETERNAL) > 0) {
            this.getItem().setDamage(ItemStack.class.cast(this), 0);
            ci.cancel();
        }
        if (ue$isLethalTempoShoot) {
            ci.cancel();
        }
    }
}
