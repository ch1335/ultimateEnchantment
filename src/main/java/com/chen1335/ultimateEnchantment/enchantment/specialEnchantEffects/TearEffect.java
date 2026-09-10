package com.chen1335.ultimateEnchantment.enchantment.specialEnchantEffects;

import com.chen1335.ultimateEnchantment.API.UEDamageTypeTags;
import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.data.registries.UEDamageType;
import com.chen1335.ultimateEnchantment.enchantment.UEEnchantments;
import com.chen1335.ultimateEnchantment.enchantment.enchantments.Tear;
import com.chen1335.ultimateEnchantment.mixinsAPI.minecraft.ILivingEntityMixin;
import com.chen1335.ultimateEnchantment.utils.SimpleSchedule;

import javax.script.SimpleBindings;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import twilightforest.entity.boss.Hydra;

import java.util.Stack;

public class TearEffect {
    public static void onAttack(DamageSource damageSource, Stack<DamageContainer> damageContainers, LivingEntity target) {
        if (damageSource.getEntity() instanceof Player attacker && damageSource.is(UEDamageTypeTags.IS_ATTACK) && !damageSource.is(UEDamageType.TEAR_DAMAGE)) {
            int level = UEEnchantments.TEAR.getEnchantmentLevel(attacker.getWeaponItem(), attacker.level());
            if (level > 0) {
                SimpleBindings bindings = UEEnchantments.buildBindings(level);
                int hitCount = Math.max(1, Math.round(Tear.HIT_COUNT.calculate(bindings)));
                float duration = Math.max(0, Tear.DURATION.calculate( bindings));
                float damageAdd = Tear.DAMAGE_ADD.calculate( bindings);
                float healthDamage = Tear.HEALTH_DAMAGE.calculate( bindings);
                float finalDamage = damageContainers.peek().getNewDamage();
                damageContainers.peek().setNewDamage(0);
                float damagePerHit = finalDamage / hitCount * (1 + damageAdd) + target.getHealth() * healthDamage;
                float tickPerHit = hitCount <= 1 ? 0 : duration / (hitCount - 1);
                for (int i = 0; i < hitCount; i++) {
                    SimpleSchedule.addSchedule(attacker.level(), new SimpleSchedule.Wait(() -> {
                        perHit(attacker, damageSource, damageContainers, target, damagePerHit);
                    }, Math.round(tickPerHit * i)));
                }
            }
        }
    }

    private static void perHit(LivingEntity attacker, DamageSource damageSource, Stack<DamageContainer> damageContainers, LivingEntity target, float damagePerHit) {
        if (target.isDeadOrDying() || target.isRemoved()) {
            return;
        }
        DamageSource damageSource1 = attacker.level().damageSources().source(UEDamageType.TEAR_DAMAGE, attacker);
        target.playSound(SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, 0.3F, 1.7F);
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
