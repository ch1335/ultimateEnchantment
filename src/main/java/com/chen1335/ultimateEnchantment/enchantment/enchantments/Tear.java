package com.chen1335.ultimateEnchantment.enchantment.enchantments;

import com.chen1335.ultimateEnchantment.API.objects.UESounds;
import com.chen1335.ultimateEnchantment.common.EnchantmentLookup;
import com.chen1335.ultimateEnchantment.common.Formula;
import com.chen1335.ultimateEnchantment.data.registries.UEDamageType;
import com.chen1335.ultimateEnchantment.enchantment.EnchantmentBasic;
import com.chen1335.ultimateEnchantment.enchantment.UEEnchantments;
import com.chen1335.ultimateEnchantment.mixinsAPI.minecraft.ILivingEntityMixin;
import com.chen1335.ultimateEnchantment.tags.UEEnchantmentTags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class Tear extends EnchantmentBasic {
    public static final Formula DAMAGE_ADD = new Formula("0.2");
    public static final Formula HEALTH_DAMAGE = new Formula("0.01");
    public static final Formula HIT_COUNT = new Formula("lvl+1");
    public static final Formula DURATION = new Formula("60");

    public Tear() {
        super("tear");
        supported_items = new Type.TagType<>(ItemTags.SHARP_WEAPON_ENCHANTABLE);
        exclusive_set = new Type.TagType<>(UEEnchantmentTags.ULTIMATE_ENCHANTMENT_EXCLUSIVE);
        max_cost = new Enchantment.Cost(200, 0);
        min_cost = new Enchantment.Cost(200, 0);
        slots = List.of();
        max_level = 5;
        weight = 3;
    }

    @Override
    protected void registerFormula(Map<String, Formula> formulas) {
        formulas.put("damage_add", DAMAGE_ADD);
        formulas.put("health_damage", HEALTH_DAMAGE);
        formulas.put("hit_count", HIT_COUNT);
        formulas.put("duration", DURATION);
    }

    @Override
    public List<MutableComponent> getDesc(int level) {
        return List.of(Component.translatable(getDescId(), level + 1,
                DAMAGE_ADD.toComponent(buildBindings(level), 100),
                HEALTH_DAMAGE.toComponent(buildBindings(level), 100),
                DURATION.toComponent(buildBindings(level), 0.05F)).withStyle(ChatFormatting.LIGHT_PURPLE));
    }

    public static class Ticker {
        private int interval = 10;
        private int ticker = 0;
        private Queue<DamageHolder> damageHolders = new ArrayDeque<>(60);

        public void tick(LivingEntity owner) {
            if (damageHolders.isEmpty()) {
                return;
            }
            ticker--;
            if (ticker <= 0) {
                ticker = interval;
                DamageHolder peek = damageHolders.poll();
                if (owner.isAttackable()) {
                    perHit(peek.source, owner, peek.amount);
                }
            }
            if (damageHolders.isEmpty()) {
                ticker = 0;
            }
        }

        public static void perHit(LivingEntity attacker, LivingEntity target, float damagePerHit) {
            if (target.isDeadOrDying() || target.isRemoved()) {
                return;
            }
            DamageSource damageSource1 = attacker.level().damageSources().source(UEDamageType.TEAR_DAMAGE, attacker);
            target.playSound(UESounds.TEAR.value(), 1.5F, 1.1F);
            target.playSound(SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, 0.3F, 1.7F);

            ((ILivingEntityMixin) target).ue$setDisableHurtSound(true);
            float lastHurt = target.lastHurt;
            int invulnerableTime = target.invulnerableTime;

            target.hurt(damageSource1, damagePerHit);

            target.lastHurt = lastHurt;
            target.invulnerableTime = invulnerableTime;
            ((ILivingEntityMixin) target).ue$setDisableHurtSound(false);
        }

        public boolean tryAddDamage(float amount, LivingEntity source) {
            ItemStack weaponItem = source.getWeaponItem();
            int lvl = UEEnchantments.TEAR.getEnchantmentLevel(weaponItem, EnchantmentLookup.get());

            int duration = Math.round(DURATION.calculate(buildBindings(lvl)));
            if (damageHolders.size() < duration) {
                damageHolders.add(new DamageHolder(amount, source));
                interval = duration / damageHolders.size();
                return true;
            }
            return false;
        }

        private record DamageHolder(float amount, LivingEntity source) {
        }
    }
}
