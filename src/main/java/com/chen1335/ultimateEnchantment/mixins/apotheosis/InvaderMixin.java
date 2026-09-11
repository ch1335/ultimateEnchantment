package com.chen1335.ultimateEnchantment.mixins.apotheosis;

import com.chen1335.ultimateEnchantment.apotheosis.ApothBossEquipmentLoot;
import dev.shadowsoffire.apotheosis.loot.LootRarity;
import dev.shadowsoffire.apotheosis.mobs.types.Invader;
import dev.shadowsoffire.apotheosis.tiers.GenContext;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * 记住神化 Boss 生成时用的 {@link GenContext}，以及它能出的稀有度范围。
 * <p>
 * Boss 的装备是在 {@code Invader#initBoss} 里按这个上下文造出来的（世界层级决定权重，
 * 幸运值影响词缀数值），但神化只把最终摇出的那一个稀有度写进了 Boss 的持久 NBT，
 * 上下文和候选范围本身用完就丢。死亡时要额外掉一件「同档次」的装备就得复刻它们，
 * 所以在 {@code initBoss} 收尾处抄一份到 Boss 身上，见
 * {@link ApothBossEquipmentLoot#rememberGenContext}。
 * <p>
 * 稀有度范围来自 {@code Invader#stats} 的 key 集合 —— 那是这个 Boss 在数据包里配了
 * {@code stats} 的那几档，也正是 {@code initBoss} 里
 * {@code LootRarity.random(ctx, this.stats.keySet())} 摇 Boss 本体稀有度用的池子。
 * <p>
 * 放在 apotheosis 子包下，由 {@code MainMixinPlugin} 的 modid 检查保证只在神化加载时应用。
 */
@Mixin(Invader.class)
public class InvaderMixin {

    @Inject(method = "initBoss", at = @At("TAIL"))
    private void ue$rememberGenContext(Mob mob, GenContext ctx, @Nullable LootRarity rarity, CallbackInfo ci) {
        // record 的 accessor 是 public 的，直接取比 @Shadow 字段稳。
        Set<LootRarity> rarities = ((Invader) (Object) this).stats().keySet();
        ApothBossEquipmentLoot.rememberGenContext(mob, ctx, rarities);
    }
}
