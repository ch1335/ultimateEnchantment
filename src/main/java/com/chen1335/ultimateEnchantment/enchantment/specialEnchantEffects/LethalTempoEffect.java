package com.chen1335.ultimateEnchantment.enchantment.specialEnchantEffects;

import com.chen.simpleRPGCore.utils.SimpleSchedule;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class LethalTempoEffect {

    public static void onShoot(ServerLevel level, LivingEntity shooter, ItemStack weapon, ItemStack ammo, boolean isCrit, IProjectileCreator creator, int lethalTempoLevel) {
        Projectile projectile = creator.createProjectile(level, shooter, weapon, ammo, isCrit);
        SimpleSchedule.addSchedule(level, new SimpleSchedule.Wait(LethalTempoEffect::shootAddition, 1));

    }

    private static void shootAddition() {

    }

    public interface IProjectileCreator {
        Projectile createProjectile(Level level, LivingEntity shooter, ItemStack weapon, ItemStack ammo, boolean isCrit);
    }
}
