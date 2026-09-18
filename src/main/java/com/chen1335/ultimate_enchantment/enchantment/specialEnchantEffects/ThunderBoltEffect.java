package com.chen1335.ultimate_enchantment.enchantment.specialEnchantEffects;

import com.chen1335.ultimate_enchantment.API.UEDamageTypeTags;
import com.chen1335.ultimate_enchantment.enchantment.UEEnchantments;
import com.chen1335.ultimate_enchantment.enchantment.enchantments.ThunderBolt;
import com.chen1335.ultimate_enchantment.utils.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.damagesource.DamageContainer;

import javax.script.SimpleBindings;
import java.util.Stack;

public class ThunderBoltEffect {
    public static void onAttack(DamageSource damageSource, Stack<DamageContainer> damageContainers, LivingEntity target) {
        if (damageSource.getEntity() instanceof LivingEntity attacker
                && damageSource.is(UEDamageTypeTags.IS_ATTACK)
                && Util.getAttackedCount(attacker) % 3 == 0
        ) {
            int level = UEEnchantments.THUNDER_BOLT.getEnchantmentLevel(attacker.getWeaponItem(), attacker.level());
            if (level > 0) {
                SimpleBindings bindings = UEEnchantments.buildBindings(level);
                float range = ThunderBolt.RANGE.calculate(bindings);
                float mainDamage = ThunderBolt.MAIN_DAMAGE.calculate(bindings);
                float otherDamage = ThunderBolt.OTHER_DAMAGE.calculate(bindings);
                LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(target.level(), EntitySpawnReason.TRIGGERED);
                if (lightningBolt != null) {
                    lightningBolt.setPos(target.position());
                    lightningBolt.setVisualOnly(true);
                    target.level().addFreshEntity(lightningBolt);
                }
                for (Entity entity : target.level().getEntities(target, AABB.ofSize(target.position(), range, 4, range), entity -> {
                    return entity instanceof LivingEntity livingEntity && livingEntity.attackable() && livingEntity != attacker;
                })) {
                    entity.invulnerableTime = 0;
                    entity.hurt(new DamageSource(attacker.level().registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).get(DamageTypes.LIGHTNING_BOLT).orElseThrow(), attacker), damageContainers.peek().getNewDamage() * otherDamage);
                }
                target.invulnerableTime = 0;
                target.hurt(new DamageSource(attacker.level().registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).get(DamageTypes.LIGHTNING_BOLT).orElseThrow(), attacker), damageContainers.peek().getNewDamage() * mainDamage);

            }
        }
    }
}
