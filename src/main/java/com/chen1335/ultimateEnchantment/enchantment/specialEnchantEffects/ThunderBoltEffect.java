package com.chen1335.ultimateEnchantment.enchantment.specialEnchantEffects;

import com.chen1335.ultimateEnchantment.data.registries.UEDamageType;
import com.chen1335.ultimateEnchantment.enchantment.UEEnchantments;
import com.chen1335.ultimateEnchantment.enchantment.enchantments.ThunderBolt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.damagesource.DamageContainer;

import javax.script.SimpleBindings;
import java.util.Stack;

public class ThunderBoltEffect {

    public static void onAttack(LivingEntity attacker, float damage, LivingEntity target) {
        int level = UEEnchantments.THUNDER_BOLT.getEnchantmentLevel(attacker.getWeaponItem(), attacker.level());
        if (level > 0) {
            SimpleBindings bindings = UEEnchantments.buildBindings(level);
            float range = ThunderBolt.RANGE.calculate(bindings);
            float mainDamage = ThunderBolt.MAIN_DAMAGE.calculate(bindings);
            float otherDamage = ThunderBolt.OTHER_DAMAGE.calculate(bindings);
            LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(target.level());
            if (lightningBolt != null) {
                lightningBolt.setPos(target.position());
                lightningBolt.setVisualOnly(true);
                target.level().addFreshEntity(lightningBolt);
            }
            for (Entity entity : target.level().getEntities(target, AABB.ofSize(target.position(), range, 4, range), entity -> {
                return entity instanceof LivingEntity livingEntity && livingEntity.attackable() && livingEntity != attacker;
            })) {
                entity.hurt(attacker.level().damageSources().source(UEDamageType.THUNDER_BOLT_DAMAGE, attacker), damage * otherDamage);
            }
            target.hurt(attacker.level().damageSources().source(UEDamageType.THUNDER_BOLT_DAMAGE, attacker), damage * mainDamage);
        }
    }
}
