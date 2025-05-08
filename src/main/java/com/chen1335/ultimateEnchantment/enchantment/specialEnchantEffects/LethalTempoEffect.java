package com.chen1335.ultimateEnchantment.enchantment.specialEnchantEffects;

import com.chen1335.ultimateEnchantment.dataComponentType.UEDataComponentTypes;
import com.chen1335.ultimateEnchantment.enchantment.enchatments.UEEnchantments;
import com.chen1335.ultimateEnchantment.mixinsAPI.minecraft.IItemStackMixin;
import com.chen1335.ultimateEnchantment.mixinsAPI.minecraft.IProjectileMixin;
import com.chen1335.ultimateEnchantment.utils.SimpleSchedule;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.ArrowLooseEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class LethalTempoEffect {

    @SubscribeEvent
    public static void onShoot(LivingEntityUseItemEvent.Stop event) {

        ItemStack itemStack = event.getItem();
        int lethalTempoLevel = itemStack.getEnchantmentLevel(event.getEntity().level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(UEEnchantments.LETHAL_TEMPO));
        if (lethalTempoLevel <= 0) {
            return;
        }

        IItemStackMixin iItemStackMixin = (IItemStackMixin) (Object) itemStack;
        float additionShootChance = itemStack.getOrDefault(UEDataComponentTypes.ADDITION_SHOOT_CHANCE, 0).floatValue();

        int timeLeft = itemStack.getOrDefault(UEDataComponentTypes.LETHAL_TEMPO_TIME_LEFT, 0);
        if (timeLeft <= 0) {
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
                itemStack.releaseUsing(event.getEntity().level(), event.getEntity(), event.getDuration());
                iItemStackMixin.ue$setLethalTempoShoot(false);
            }, 2 * (i + 1)));
        }
    }

    @SubscribeEvent
    public static void onArrowHit(LivingIncomingDamageEvent event) {
        if (event.getSource().getDirectEntity() instanceof Projectile projectile && projectile.getOwner() instanceof LivingEntity living) {
            ItemStack weapon = living.getMainHandItem();
            int lethalTempoLevel = weapon.getEnchantmentLevel(living.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(UEEnchantments.LETHAL_TEMPO));
            if (lethalTempoLevel <= 0) {
                return;
            }

            IProjectileMixin projectileMixin = ((IProjectileMixin) projectile);

            if (!projectileMixin.ue$isLethalTempoAdditionArrow()) {
                float oldChance = weapon.getOrDefault(UEDataComponentTypes.ADDITION_SHOOT_CHANCE, 0).floatValue();
                weapon.set(UEDataComponentTypes.ADDITION_SHOOT_CHANCE, (float) Math.min(lethalTempoLevel * 0.4, oldChance + 0.2));
                weapon.set(UEDataComponentTypes.LETHAL_TEMPO_TIME_LEFT, 200);
            }


            if (projectileMixin.ue$isLethalTempoAdditionArrow()) {
                event.getEntity().invulnerableTime = 0;
                event.setAmount((float) (event.getAmount() * 0.2));
            }
        }
    }
}
