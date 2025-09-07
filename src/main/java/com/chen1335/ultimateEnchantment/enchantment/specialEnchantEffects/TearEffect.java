package com.chen1335.ultimateEnchantment.enchantment.specialEnchantEffects;

import com.chen1335.ultimateEnchantment.API.UEDamageTypeTags;
import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.data.registries.UEDamageType;
import com.chen1335.ultimateEnchantment.enchantment.effectComponents.UEEnchantmentEffectComponents;
import com.chen1335.ultimateEnchantment.enchantment.effectComponents.UltimateEnchantment.TearComponent;
import com.chen1335.ultimateEnchantment.enchantment.enchatments.UEEnchantments;
import com.chen1335.ultimateEnchantment.mixinsAPI.minecraft.IDamageSourceMixin;
import com.chen1335.ultimateEnchantment.mixinsAPI.minecraft.ILivingEntityMixin;
import com.chen1335.ultimateEnchantment.utils.SimpleSchedule;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import twilightforest.entity.boss.Hydra;

import java.util.Objects;
import java.util.Stack;

public class TearEffect {
    public static void onAttack(DamageSource damageSource, Stack<DamageContainer> damageContainers, LivingEntity target) {
        if (damageSource.getEntity() instanceof Player attacker && damageSource.is(UEDamageTypeTags.IS_ATTACK) && !damageSource.is(UEDamageType.TEAR_DAMAGE)) {
            Holder.Reference<Enchantment> holder = attacker.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(UEEnchantments.TEAR);
            int level = attacker.getWeaponItem().getEnchantmentLevel(holder);
            if (level <= 0) {
                return;
            }
            TearComponent tearComponent = Objects.requireNonNull(holder.value().effects().get(UEEnchantmentEffectComponents.TEAR.get()));

            float finalDamage = damageContainers.peek().getNewDamage();
            damageContainers.peek().setNewDamage(0);
            float damagePerHit = finalDamage / (level + 1) * (1 + tearComponent.damageAdd()) + target.getHealth() * tearComponent.totalHealthDamage();

            float tickPerHit = 60F / (level + 1);

            for (int i = 0; i < level + 1; i++) {
                SimpleSchedule.addSchedule(attacker.level(), new SimpleSchedule.Wait(() -> {
                    perHit(attacker, damageSource, damageContainers, target, damagePerHit);
                }, (int) (tickPerHit * i)));
            }

        }
    }

    private static void perHit(LivingEntity attacker, DamageSource damageSource, Stack<DamageContainer> damageContainers, LivingEntity target, float damagePerHit) {
        if (target.isDeadOrDying() || target.isRemoved()) {
            return;
        }
        DamageSource damageSource1 = attacker.level().damageSources().source(UEDamageType.TEAR_DAMAGE, attacker);
        target.playSound(SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, 0.3F, 1.7F);
        ((IDamageSourceMixin) damageSource1).ue$setDirectAttackedEntity(target);
        ((ILivingEntityMixin) target).ue$setDisableHurtSound(true);
        float lastHurt = target.lastHurt;
        int invulnerableTime = target.invulnerableTime;

        if (UltimateEnchantment.isTwilightForestLoaded()) {
            if (target instanceof Hydra hydra) {
                // I hate this :(
                hydra.body.hurt(damageSource1, damagePerHit);
            } else {
                target.hurt(damageSource1, damagePerHit);
            }
        } else {
            target.hurt(damageSource1, damagePerHit);
        }

        target.lastHurt = lastHurt;
        target.invulnerableTime = invulnerableTime;
        ((ILivingEntityMixin) target).ue$setDisableHurtSound(false);
    }
}
