package com.chen1335.ultimateEnchantment.enchantment.specialEnchantEffects;

import com.chen1335.ultimateEnchantment.API.UEDamageTypeTags;
import com.chen1335.ultimateEnchantment.enchantment.enchatments.UEEnchantments;
import com.chen1335.ultimateEnchantment.mixinsAPI.minecraft.IDamageSourceMixin;
import com.chen1335.ultimateEnchantment.utils.ItemEnchantmentHelper;
import com.chen1335.ultimateEnchantment.utils.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.damagesource.DamageContainer;

import java.util.Stack;

public class ThunderBolt {
    public static void onAttack(DamageSource damageSource, Stack<DamageContainer> damageContainers, LivingEntity target) {
        if (damageSource.getEntity() instanceof LivingEntity attacker && damageSource.is(UEDamageTypeTags.IS_ATTACK)) {
            int level = ItemEnchantmentHelper.getEnchantmentLevel(attacker.getMainHandItem(), UEEnchantments.THUNDER_BOLT);
            if (level > 0 && ((IDamageSourceMixin) damageSource).isDirectAttackedEntity(target) && Util.getAttackedCount(attacker) % 3 == 0) {

                LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(target.level());
                if (lightningBolt != null) {
                    lightningBolt.setPos(target.position());
                    lightningBolt.setVisualOnly(true);
                    target.level().addFreshEntity(lightningBolt);
                }

                for (Entity entity : target.level().getEntities(target, AABB.ofSize(target.position(), 2, 4, 2), entity -> {
                    return entity instanceof LivingEntity livingEntity && livingEntity.attackable() && livingEntity != attacker;
                })) {
                    entity.invulnerableTime = 0;
                    entity.hurt(new DamageSource(attacker.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.LIGHTNING_BOLT), attacker), (float) (damageContainers.peek().getNewDamage() * 0.1 * level));
                }
                target.invulnerableTime = 0;
                target.hurt(new DamageSource(attacker.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.LIGHTNING_BOLT), attacker), (float) (damageContainers.peek().getNewDamage() * 0.05 * level));

            }
        }
    }
}
