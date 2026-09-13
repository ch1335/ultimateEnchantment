package com.chen1335.ultimateEnchantment.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

/**
 * 撕裂特效的客户端部分。服务端只告诉"哪个实体刚裂了一道"，
 * 位置、方向、长度、粒子铺法全在这里现算。
 */
public class TearParticlesClient {
    public static void spawnTearParticles(int entityId) {
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        Entity entity = level.getEntity(entityId);
        if (!(entity instanceof LivingEntity target)) {
            return;
        }

        RandomSource random = level.random;
        // 口子长度取碰撞箱体对角线 * 1.5
        double bbWidth = target.getBbWidth();
        double bbHeight = target.getBbHeight();
        double length = Math.sqrt(bbWidth * bbWidth * 2.0 + bbHeight * bbHeight) * 1.5;
        double halfLength = length * 0.5;

        // 粒子按固定间距铺开，线拉长时数量跟着涨，免得中间拉稀。
        // 上限是防末影龙那种巨型碰撞箱（对角线 24 格）算出上百个粒子。
        int count = Math.min(24, Math.max(4, (int) Math.round(length / 0.22) + 1));

        // 每次随机一条短线方向，模拟新裂开的一道口子
        double angle = random.nextDouble() * Math.PI * 2.0;
        double dirX = Math.cos(angle);
        double dirZ = Math.sin(angle);
        double tilt = (random.nextDouble() - 0.5) * 0.8;
        double centerX = target.getX() + (random.nextDouble() - 0.5) * bbWidth;
        double centerZ = target.getZ() + (random.nextDouble() - 0.5) * bbWidth;
        double centerY = target.getY() + bbHeight * 0.55;

        for (int i = 0; i < count; i++) {
            double t = (i / (double) (count - 1) - 0.5) * 2.0 * halfLength;
            double x = centerX + dirX * t;
            double y = centerY + tilt * t + (random.nextDouble() - 0.5) * 0.15;
            double z = centerZ + dirZ * t;
            level.addParticle(ParticleTypes.CRIT, x, y, z, 0.0, 0.0, 0.0);
        }
    }
}
