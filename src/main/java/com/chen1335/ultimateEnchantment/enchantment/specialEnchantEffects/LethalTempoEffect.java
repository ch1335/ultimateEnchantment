package com.chen1335.ultimateEnchantment.enchantment.specialEnchantEffects;

import com.chen1335.ultimateEnchantment.dataComponentType.UEDataComponentTypes;
import com.chen1335.ultimateEnchantment.enchantment.effectComponents.UEEnchantmentEffectComponents;
import com.chen1335.ultimateEnchantment.enchantment.effectComponents.UltimateEnchantment.LethalTempoComponent;
import com.chen1335.ultimateEnchantment.enchantment.enchatments.UEEnchantments;
import com.chen1335.ultimateEnchantment.mixinsAPI.minecraft.IEntityMixin;
import com.chen1335.ultimateEnchantment.mixinsAPI.minecraft.IItemStackMixin;
import com.chen1335.ultimateEnchantment.mixinsAPI.minecraft.IProjectileMixin;
import com.chen1335.ultimateEnchantment.utils.ItemEnchantmentHelper;
import com.chen1335.ultimateEnchantment.utils.SimpleSchedule;
import com.chen1335.ultimateEnchantment.utils.UEEnchantmentHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber()
public class LethalTempoEffect {

    @SubscribeEvent
    public static void onShoot(LivingEntityUseItemEvent.Stop event) {

        ItemStack itemStack = event.getItem();
        UEEnchantmentHelper.getEnchantment(UEEnchantments.LETHAL_TEMPO).ifPresent(holder -> {
            ItemEnchantmentHelper.runIfItemStackHaveEnchant(itemStack, holder, level -> {
                LethalTempoComponent lethalTempoComponent = holder.value().effects().get(UEEnchantmentEffectComponents.LETHAL_TEMPO.get());
                if (lethalTempoComponent == null) {
                    return;
                }
                IItemStackMixin iItemStackMixin = (IItemStackMixin) (Object) itemStack;
                IEntityMixin iEntityMixin = (IEntityMixin) event.getEntity();
                float additionShootChance = itemStack.getOrDefault(UEDataComponentTypes.ADDITION_SHOOT_CHANCE, 0).floatValue();

                long timeRecord = itemStack.getOrDefault(UEDataComponentTypes.LETHAL_TEMPO_TIME_RECORD, 0L);
                if (event.getEntity().level().getGameTime() - timeRecord > lethalTempoComponent.keepTime()) {
                    itemStack.set(UEDataComponentTypes.ADDITION_SHOOT_CHANCE, 0F);
                    return;
                }

                int additionShootCount = (int) Math.floor(additionShootChance);
                if (event.getEntity().getRandom().nextFloat() < (additionShootChance - additionShootCount)) {
                    additionShootCount++;
                }

                for (int i = 0; i < additionShootCount; i++) {
                    SimpleSchedule.addSchedule(event.getEntity().level(), new SimpleSchedule.Wait(() -> {
                        iItemStackMixin.ue$setLethalTempoShoot(true);
                        iEntityMixin.ue$setLethalTempoShooting(true);
                        itemStack.releaseUsing(event.getEntity().level(), event.getEntity(), event.getDuration());
                        iItemStackMixin.ue$setLethalTempoShoot(false);
                        iEntityMixin.ue$setLethalTempoShooting(false);
                    }, 2 * (i + 1)));
                }
            });
        });
    }

    @SubscribeEvent
    public static void onArrowHit(LivingIncomingDamageEvent event) {
        if (event.getSource().getDirectEntity() instanceof Projectile projectile && projectile.getOwner() instanceof LivingEntity living) {
            ItemStack weapon = living.getWeaponItem();
            UEEnchantmentHelper.runIfEnchantmentExist(UEEnchantments.LETHAL_TEMPO, holder -> {
                ItemEnchantmentHelper.runIfItemStackHaveEnchant(weapon, holder, level -> {
                    UEEnchantmentHelper.runIfComponentExist(holder, UEEnchantmentEffectComponents.LETHAL_TEMPO, lethalTempoComponent -> {
                        IProjectileMixin projectileMixin = ((IProjectileMixin) projectile);

                        if (!projectileMixin.ue$isLethalTempoAdditionArrow()) {
                            float oldChance = weapon.getOrDefault(UEDataComponentTypes.ADDITION_SHOOT_CHANCE, 0).floatValue();
                            weapon.set(UEDataComponentTypes.ADDITION_SHOOT_CHANCE, Math.min(lethalTempoComponent.maxChancePerLevel() * level, oldChance + lethalTempoComponent.addChanceOnHit()));
                            weapon.set(UEDataComponentTypes.LETHAL_TEMPO_TIME_RECORD, event.getEntity().level().getGameTime());
                        }


                        if (projectileMixin.ue$isLethalTempoAdditionArrow()) {
                            event.getEntity().invulnerableTime = 0;
                            event.setAmount(event.getAmount() * lethalTempoComponent.additionHitDamage());
                        }
                    });
                });
            });
        }
    }
}
