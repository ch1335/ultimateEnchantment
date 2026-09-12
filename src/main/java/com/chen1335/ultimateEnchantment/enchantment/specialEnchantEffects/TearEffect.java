package com.chen1335.ultimateEnchantment.enchantment.specialEnchantEffects;

import com.chen1335.ultimateEnchantment.API.AttachmentTypes;
import com.chen1335.ultimateEnchantment.API.UEDamageTypeTags;
import com.chen1335.ultimateEnchantment.attachmentDatas.CommonEntityData;
import com.chen1335.ultimateEnchantment.data.registries.UEDamageType;
import com.chen1335.ultimateEnchantment.enchantment.UEEnchantments;
import com.chen1335.ultimateEnchantment.enchantment.enchantments.Tear;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.damagesource.DamageContainer;

import javax.script.SimpleBindings;
import java.util.Stack;

public class TearEffect {
    public static void onAttack(DamageSource damageSource, Stack<DamageContainer> damageContainers, LivingEntity target) {
        if (damageSource.getEntity() instanceof Player attacker && damageSource.is(UEDamageTypeTags.IS_ATTACK) && !damageSource.is(UEDamageType.TEAR_DAMAGE)) {
            int level = UEEnchantments.TEAR.getEnchantmentLevel(attacker.getWeaponItem(), attacker.level());
            if (level > 0) {
                SimpleBindings bindings = UEEnchantments.buildBindings(level);
                int hitCount = Math.max(1, Math.round(Tear.HIT_COUNT.calculate(bindings)));
                float damageAdd = Tear.DAMAGE_ADD.calculate(bindings);
                float healthDamage = Tear.HEALTH_DAMAGE.calculate(bindings);
                float finalDamage = damageContainers.peek().getNewDamage();
                damageContainers.peek().setNewDamage(0);
                float damagePerHit = finalDamage / hitCount * (1 + damageAdd) + target.getHealth() * healthDamage;
                CommonEntityData data = target.getData(AttachmentTypes.COMMON_ENTITY);
                for (int i = 0; i < hitCount; i++) {
                    if (!data.ticker.tryAddDamage(damagePerHit, attacker)) {
                        for (int j = 0; j < hitCount - i; j++) {
                            Tear.Ticker.perHit(attacker, target, damagePerHit);
                        }
                    }
                }
            }
        }
    }


}
