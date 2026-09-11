package com.chen1335.ultimateEnchantment.mixins.apotheosis;

import com.chen1335.ultimateEnchantment.apotheosis.ApothBossEquipmentLoot;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

/**
 * 神化 Boss 额外掉落 —— 装备部分。
 * <p>
 * 神化 Boss 的装备是 {@code Invader#initBoss} 用 {@code Mob#setItemSlot} 直接穿上去的，
 * 死亡时在 {@code Mob#dropCustomDeathLoot} 里掉出，全程不经过 {@code LootTable}，
 * 因此 {@code LootTableMixin} 覆盖不到这条路径。
 * <p>
 * <b>放在 apotheosis 子包下是有意的</b>：{@code MainMixinPlugin#shouldApplyMixin} 会把
 * 类名里 {@code mixins} 之后的那一段当作 modid 去查加载列表，于是本 mixin 只在神化
 * 加载时才会被应用。它引用的 {@link ApothBossEquipmentLoot} 直接依赖神化 API，
 * 正是靠这个条件才敢放心引用。
 * <p>
 * <b>为什么要两个注入点</b>：装备掉落是 {@code Mob#dropCustomDeathLoot} 内部按掉率
 * 逐个判定后就地清空槽位的，等能干预时已经拿不到「这次掉了哪几件」。所以在
 * {@code dropAllDeathLoot} 的 HEAD 处快照装备槽，再在 {@code dropEquipment} 的 HEAD
 * 处比对槽位是否被清空 —— 没被清空说明这次掉率没过，不能算进额外掉落的基数。
 * <p>
 * {@code dropEquipment} 这个位置同样关键：它仍在 {@code dropAllDeathLoot} 的
 * {@code captureDrops} 窗口内、且在 {@code CommonHooks#onLivingDrops} 之前，所以额外
 * 掉出来的装备会正常进入 {@code LivingDropsEvent}，其它模组的掉落事件处理器能看到它，
 * 事件被取消时也不会凭空掉出来。
 */
@Mixin(LivingEntity.class)
public abstract class BossEquipmentDropMixin {

    /** 死亡掉落开始前的装备快照，只存活在一次 {@code dropAllDeathLoot} 之内。 */
    @Unique
    @Nullable
    private Map<EquipmentSlot, ItemStack> ue$equipmentBeforeDeath;

    /**
     * 击杀者。<b>不能从实体上读</b> —— {@code LivingEntity#lastHurtByPlayer} 是
     * protected 的，跨包取不到，而 {@code DamageSource} 正好在这个注入点能拿到。
     */
    @Unique
    @Nullable
    private Player ue$killer;

    @Inject(method = "dropAllDeathLoot", at = @At("HEAD"))
    private void ue$snapshotEquipment(ServerLevel level, DamageSource damageSource, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        Map<EquipmentSlot, ItemStack> snapshot = new EnumMap<>(EquipmentSlot.class);
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = self.getItemBySlot(slot);
            if (!stack.isEmpty()) {
                // 存副本：原 stack 掉出后会被整个交给 ItemEntity，不能再引用。
                snapshot.put(slot, stack.copy());
            }
        }
        this.ue$equipmentBeforeDeath = snapshot;
        this.ue$killer = damageSource.getEntity() instanceof Player player ? player : null;
    }

    @Inject(method = "dropEquipment", at = @At("HEAD"))
    private void ue$appendBossEquipment(CallbackInfo ci) {
        Map<EquipmentSlot, ItemStack> snapshot = this.ue$equipmentBeforeDeath;
        Player killer = this.ue$killer;
        this.ue$equipmentBeforeDeath = null;
        this.ue$killer = null;
        if (snapshot == null || snapshot.isEmpty()) {
            return;
        }

        LivingEntity self = (LivingEntity) (Object) this;
        ObjectArrayList<ItemStack> dropped = new ObjectArrayList<>();
        for (Map.Entry<EquipmentSlot, ItemStack> entry : snapshot.entrySet()) {
            if (self.getItemBySlot(entry.getKey()).isEmpty()) {
                dropped.add(entry.getValue());
            }
        }
        ApothBossEquipmentLoot.appendEquipment(self, dropped, killer);
    }
}
