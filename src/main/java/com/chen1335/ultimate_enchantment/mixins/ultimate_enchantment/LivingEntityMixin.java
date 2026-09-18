package com.chen1335.ultimate_enchantment.mixins.ultimate_enchantment;

import com.chen1335.ultimate_enchantment.API.UEDamageTypeTags;
import com.chen1335.ultimate_enchantment.dataComponentType.UEDataComponentTypes;
import com.chen1335.ultimate_enchantment.enchantment.enchantments.Vanquisher;
import com.chen1335.ultimate_enchantment.enchantment.specialEnchantEffects.TearEffect;
import com.chen1335.ultimate_enchantment.enchantment.specialEnchantEffects.ThunderBoltEffect;
import com.chen1335.ultimate_enchantment.mixinsAPI.minecraft.ILivingEntityMixin;
import com.chen1335.ultimate_enchantment.mobEffect.MobEffects;
import com.chen1335.ultimate_enchantment.utils.Util;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import javax.script.SimpleBindings;
import java.util.Stack;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements ILivingEntityMixin {
    @Unique
    private boolean ue$disableHurtSound = false;

    @Shadow
    @Nullable
    public abstract MobEffectInstance getEffect(Holder<MobEffect> effect);

    @Shadow
    public abstract boolean removeEffect(Holder<MobEffect> effect);

    @Shadow
    public abstract boolean addEffect(MobEffectInstance effectInstance);

    @Shadow
    protected abstract void onEffectUpdated(MobEffectInstance effectInstance, boolean forced, @org.jetbrains.annotations.Nullable Entity entity);

    @Shadow
    @Nullable
    protected Stack<DamageContainer> damageContainers;

    @Inject(method = "playHurtSound", at = @At("HEAD"), cancellable = true)
    private void playHurtSound(DamageSource source, CallbackInfo ci) {
        if (ue$disableHurtSound) {
            ci.cancel();
        }
    }

    // 26.1：actuallyHurt 的首参新增了 ServerLevel
    // （签名变为 actuallyHurt(ServerLevel, DamageSource, float)），
    // 注入方法的参数列表必须同步，否则 Mixin 应用阶段会因描述符不匹配而致命失败（启动崩溃）。
    @Inject(method = "actuallyHurt", at = @At("HEAD"))
    private void actuallyHurt(net.minecraft.server.level.ServerLevel level, DamageSource damageSource, float damageAmount, CallbackInfo ci) {
        LivingEntity living = (LivingEntity) (Object) this;
        if (damageSource.getEntity() instanceof LivingEntity attacker && damageSource.is(UEDamageTypeTags.IS_ATTACK)) {
            Util.addAttackedCount(attacker);
        }
        TearEffect.onAttack(damageSource, this.damageContainers, living);
        ThunderBoltEffect.onAttack(damageSource, this.damageContainers, living);

    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        LivingEntity living = (LivingEntity) (Object) this;
        if (!(living instanceof Player)) {
            // 26.1：LivingEntity#getArmorSlots / getHandSlots 已移除，改为遍历装备槽位。
            // 原实现覆盖护甲槽与双持槽，这里用 VALUES 全量遍历（含 body 槽），语义为其超集。
            for (EquipmentSlot slot : EquipmentSlot.VALUES) {
                ItemStack itemStack = living.getItemBySlot(slot);
                if (!itemStack.isEmpty() && itemStack.isEnchanted()) {
                    itemStack.set(UEDataComponentTypes.USER_HEALTH, Math.max(living.getHealth(), 0));
                    itemStack.set(UEDataComponentTypes.USER_MAX_HEALTH, Math.max(living.getMaxHealth(), 0));
                }
            }
        }
    }

    @Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At("RETURN"))
    private void onAddEffect(MobEffectInstance effectInstance, Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            MobEffectInstance unActiveVanquisher = this.getEffect(MobEffects.UN_ACTIVE_VANQUISHER);
            MobEffectInstance activeVanquisher = this.getEffect(MobEffects.ACTIVE_VANQUISHER);

            if (!(effectInstance.getEffect().value() == MobEffects.UN_ACTIVE_VANQUISHER.value() || effectInstance.getEffect().value() == MobEffects.ACTIVE_VANQUISHER.value())) {
                return;
            }

            SimpleBindings bindings = new SimpleBindings();
            int duration = Math.round(Vanquisher.BUFF_DURATION.calculate(bindings));
            if (activeVanquisher != null) {
                this.removeEffect(MobEffects.UN_ACTIVE_VANQUISHER);
                this.addEffect(new MobEffectInstance(MobEffects.ACTIVE_VANQUISHER, duration, 0, false, false, true));
                return;
            }

            if (unActiveVanquisher == null) {
                return;
            }

            if (unActiveVanquisher.getAmplifier() + 1 < Vanquisher.MAX_STACKS.calculate(bindings)) {
                unActiveVanquisher.update(new MobEffectInstance(MobEffects.UN_ACTIVE_VANQUISHER, duration, unActiveVanquisher.getAmplifier(), false, false, true));
                this.onEffectUpdated(unActiveVanquisher, true, entity);
            } else {
                this.removeEffect(MobEffects.UN_ACTIVE_VANQUISHER);
                this.addEffect(new MobEffectInstance(MobEffects.ACTIVE_VANQUISHER, duration, 0, false, false, true));
            }
        }
    }

    public boolean ue$isDisableHurtSound() {
        return ue$disableHurtSound;
    }

    public void ue$setDisableHurtSound(boolean disableHurtSound) {
        this.ue$disableHurtSound = disableHurtSound;
    }
}
