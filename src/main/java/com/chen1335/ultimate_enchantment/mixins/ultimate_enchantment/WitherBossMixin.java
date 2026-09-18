package com.chen1335.ultimate_enchantment.mixins.ultimate_enchantment;

import com.chen1335.ultimate_enchantment.enchantment.enchantments.UltimateSlayer;
import com.chen1335.ultimate_enchantment.loot.BonusLoot;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 凋零的下界之星额外掉落。
 * <p>
 * 凋零之星是硬编码在 {@code WitherBoss#dropCustomDeathLoot} 里掉的，不走战利品表，
 * 所以挂在 {@code LootTable#getRandomItems} 上的 {@link BonusLoot#append} 完全看不到它。
 * 这里补上同一口径的额外掉落。
 * <p>
 * <b>为什么注入 TAIL</b>：原版那颗星得先掉出来，我们才好在它之外追加。TAIL 的位置也仍在
 * {@code LivingEntity#dropAllDeathLoot} 的 {@code captureDrops} 窗口内，额外掉出来的星会正常
 * 进入 {@code LivingDropsEvent}，其它模组的掉落处理器能看到它，事件被取消时也不会凭空掉出来。
 * <p>
 * {@code mobLoot} 游戏规则与 {@code shouldDropLoot()} 不必自己查：{@code dropAllDeathLoot}
 * 是这两项都通过才会调到 {@code dropCustomDeathLoot} 的，本注入点天然继承那层判断。
 * <p>
 * 本 mixin 只管下界之星。凋零的装备槽掉落走的是 {@code Mob#dropCustomDeathLoot}，那条路径上
 * 只有神化 Boss 会被 {@code ApothBossEquipmentLoot} 接管（它认的是 {@code apoth.boss} 标记），
 * 凋零不在其中 —— 也就是说凋零身上的装备目前不参与额外掉落。
 */
@Mixin(WitherBoss.class)
public abstract class WitherBossMixin {

    @Inject(method = "dropCustomDeathLoot", at = @At("TAIL"))
    private void ue$appendBonusNetherStar(
            ServerLevel level,
            DamageSource damageSource,
            boolean recentlyHit,
            CallbackInfo ci
    ) {
        // 与另外两条掉落路径同口径：没有击杀者的死亡不参与。
        if (!(damageSource.getEntity() instanceof Player killer)) {
            return;
        }

        WitherBoss self = (WitherBoss) (Object) this;
        RandomSource random = level.getRandom();
        // 原版固定掉 1 颗，基数就是 1。
        int count = BonusLoot.rollCount(1, UltimateSlayer.ratioFor(killer), random);
        for (int i = 0; i < count; i++) {
            ItemEntity extra = self.spawnAtLocation((ServerLevel) self.level(), Items.NETHER_STAR);
            if (extra != null) {
                // 跟原版那颗保持一致：凋零可能死在很远的地方或虚空边缘，
                // 不延长存在时间的话额外掉的这几颗转眼就消失了。
                extra.setExtendedLifetime();
            }
        }
    }
}
