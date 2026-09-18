package com.chen1335.ultimate_enchantment.mixins.ultimate_enchantment;

import com.chen1335.ultimate_enchantment.common.EnchantmentLookup;
import com.chen1335.ultimate_enchantment.dataComponentType.UEDataComponentTypes;
import com.chen1335.ultimate_enchantment.enchantment.UEEnchantments;
import com.chen1335.ultimate_enchantment.mixinsAPI.minecraft.IItemStackMixin;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import net.neoforged.neoforge.common.extensions.IItemStackExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;


@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements IItemStackExtension, MutableDataComponentHolder, IItemStackMixin {
    @Unique
    private boolean ue$isLethalTempoShoot = false;

    public boolean ue$isLethalTempoShoot() {
        return ue$isLethalTempoShoot;
    }

    public void ue$setLethalTempoShoot(boolean ue$isLethalTempoShoot) {
        this.ue$isLethalTempoShoot = ue$isLethalTempoShoot;
    }

    @Shadow
    public abstract boolean isEnchanted();

    @Shadow
    public abstract Item getItem();


    // 附魔书「特殊说明」的 tooltip 注入已于 26.1 移除。
    // 26.1 的 NeoForge 重构了组件 tooltip 机制：ItemStack#addToTooltip 已不在渲染路径上，
    // 注入它永远不会触发（编译通过、mixin 也报注入成功，但运行时不执行且无任何报错）。
    // 现改为官方扩展点，见 tooltip/EnchantmentDescriptionAppender。

    // 26.1：inventoryTick 的槽位参数由 (int inventorySlot, boolean isCurrentItem)
    // 变为 @Nullable EquipmentSlot。
    @Inject(method = "inventoryTick", at = @At("HEAD"))
    private void tick(Level pLevel, Entity pEntity, @org.jetbrains.annotations.Nullable net.minecraft.world.entity.EquipmentSlot pSlot, CallbackInfo ci) {
        if (pEntity instanceof LivingEntity livingEntity && this.isEnchanted()) {
            this.set(UEDataComponentTypes.USER_HEALTH, Math.max(livingEntity.getHealth(), 0));
            this.set(UEDataComponentTypes.USER_MAX_HEALTH, Math.max(livingEntity.getMaxHealth(), 0));
        }
    }

    /**
     * {@code ETERNAL} 是否生效。
     * <p>
     * 用 {@link EnchantmentLookup#getOrNull()} 而不是 {@link EnchantmentLookup#get()}：
     * 客户端没加载世界时（主菜单、断开连接后）附魔注册表确实取不到，而这时附魔系统本来就不该
     * 起作用，返回 {@code false} 即可。原先这里把可能为 null 的 lookup 直接交给了
     * {@code EnchantmentBasic#getEnchantmentLevel}，内部 {@code lookup.get(key)} 会 NPE。
     */
    @Unique
    private boolean ue$isEternal() {
        HolderLookup.RegistryLookup<Enchantment> lookup = EnchantmentLookup.getOrNull();
        if (lookup == null) {
            return false;
        }
        return UEEnchantments.ETERNAL.getEnchantmentLevel((ItemStack) (Object) this, lookup) > 0;
    }

    @Inject(method = "setDamageValue", at = @At("HEAD"), cancellable = true)
    private void setDamageValue(int damage, CallbackInfo ci) {
        if (ue$isEternal()) {
            this.getItem().setDamage(ItemStack.class.cast(this), 0);
            ci.cancel();
        }
    }

    @Inject(method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V", at = @At("HEAD"), cancellable = true)
    private void hurtAndBreak(int p_220158_, ServerLevel p_346256_, LivingEntity p_220160_, Consumer<Item> p_348596_, CallbackInfo ci) {
        if (ue$isEternal()) {
            this.getItem().setDamage(ItemStack.class.cast(this), 0);
            ci.cancel();
        }
        if (ue$isLethalTempoShoot) {
            ci.cancel();
        }
    }
}
