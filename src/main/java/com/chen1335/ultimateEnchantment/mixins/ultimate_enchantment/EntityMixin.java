package com.chen1335.ultimateEnchantment.mixins.ultimate_enchantment;

import com.chen1335.ultimateEnchantment.common.EnchantmentLookup;
import com.chen1335.ultimateEnchantment.enchantment.UEEnchantments;
import com.chen1335.ultimateEnchantment.mixinsAPI.minecraft.IUEEntityExtension;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements IUEEntityExtension {
    @Unique
    public boolean ue$isLethalTempoShooting = false;

    @Override
    public boolean ue$isLethalTempoShooting() {
        return ue$isLethalTempoShooting;
    }

    @Override
    public void ue$setLethalTempoShooting(boolean flag) {
        ue$isLethalTempoShooting = flag;
    }

    @Shadow
    public abstract void setNoGravity(boolean pNoGravity);

    @Shadow
    public abstract void setDeltaMovement(double pX, double pY, double pZ);

    @Shadow
    public abstract void setGlowingTag(boolean hasGlowingTag);

    /**
     * 掉出世界底部的物品若带 {@code ETERNAL}，把它拉回世界并把发光打开。
     * <p>
     * 用 {@link EnchantmentLookup#getOrNull()} 而不是 {@link EnchantmentLookup#get()}：
     * 本方法客户端也会跑（{@code Entity#onBelowWorld} 两端都有），而客户端没有世界时
     * 附魔注册表确实取不到 —— 那时按「没有附魔」处理即可，不该抛异常。
     */
    @Inject(method = "onBelowWorld", at = @At("HEAD"), cancellable = true)
    private void onBelowWorld(CallbackInfo ci) {
        HolderLookup.RegistryLookup<Enchantment> lookup = EnchantmentLookup.getOrNull();
        if (lookup == null) {
            return;
        }
        lookup.get(UEEnchantments.ETERNAL.getKey()).ifPresent(holder -> {
            if ((Entity) (Object) this instanceof ItemEntity itemEntity && itemEntity.getItem().getEnchantmentLevel(holder) > 0) {
                this.setNoGravity(true);
                this.setDeltaMovement(0, 1.5, 0);
                this.setGlowingTag(true);
                ci.cancel();
            }
        });
    }
}
