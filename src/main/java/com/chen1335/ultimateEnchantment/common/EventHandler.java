package com.chen1335.ultimateEnchantment.common;


import com.chen.simpleRPGCore.API.objects.SRCAttributes;
import com.chen.simpleRPGCore.common.DamageSourceExtraData;
import com.chen.simpleRPGCore.common.capability.PlayerExtraData;
import com.chen.simpleRPGCore.common.capability.SRCCapabilities;
import com.chen.simpleRPGCore.event.events.ModifyDamageEvent;
import com.chen.simpleRPGCore.event.events.ModifyProjectileEvent;
import com.chen.simpleRPGCore.mixinsAPI.minecraft.IDamageSourceExtension;
import com.chen.simpleRPGCore.mixinsAPI.minecraft.IProjectileMixinExtension;
import com.chen.simpleRPGCore.tags.SRCDamageTags;
import com.chen.simpleRPGCore.utils.SimpleSchedule;
import com.chen1335.ultimateEnchantment.API.AttachmentTypes;
import com.chen1335.ultimateEnchantment.AttachmentDatas.PlayerData;
import com.chen1335.ultimateEnchantment.enchantment.EnchantmentConfigs;
import com.chen1335.ultimateEnchantment.enchantment.effectComponents.UEEnchantmentEffectComponents;
import com.chen1335.ultimateEnchantment.enchantment.effectComponents.UltimateEnchantment.LegendComponent;
import com.chen1335.ultimateEnchantment.enchantment.effectComponents.UltimateEnchantment.VanquisherComponent;
import com.chen1335.ultimateEnchantment.enchantment.effects.UltimateEnchantment.LastStandEffect;
import com.chen1335.ultimateEnchantment.enchantment.enchatments.ApothicEnchantingEnchantments;
import com.chen1335.ultimateEnchantment.enchantment.enchatments.IronsSpellBooksEnchantments;
import com.chen1335.ultimateEnchantment.enchantment.enchatments.UEEnchantments;
import com.chen1335.ultimateEnchantment.mobEffect.MobEffects;
import com.chen1335.ultimateEnchantment.utils.ItemEnchantmentHelper;
import com.mojang.datafixers.util.Pair;
import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.enchantment.*;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.Objects;


public class EventHandler {
    @EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
    public static class Game {
        @SubscribeEvent
        public static void PlayerPreTick(PlayerTickEvent.Pre event) {
            if (!event.getEntity().level().isClientSide) {
                event.getEntity().getData(AttachmentTypes.PLAYER_DATA).tick(event.getEntity());
            }
        }

        @SubscribeEvent
        public static void QuickLatch(LivingEntityUseItemEvent.Tick event) {
            Entity entity = event.getEntity();
            if (entity instanceof Player player) {
                ItemStack itemStack = player.getUseItem();
                if (itemStack.getItem() instanceof ProjectileWeaponItem item) {
                    EnchantmentHelper.runIterationOnItem(itemStack, (pEnchantment, pLevel) -> {
                        Unit quickLatch = pEnchantment.value().effects().get(UEEnchantmentEffectComponents.QUICK_LATCH.value());
                        if (quickLatch != null) {
                            int defaultTime = item.getDefaultProjectileRange();
                            if (item.getUseDuration(itemStack, player) - player.getUseItemRemainingTicks() >= defaultTime+1) {
                                player.releaseUsingItem();
                            }
                        }
                    });
                }
            }
        }

        @SubscribeEvent
        public static void ItemAttributeModifierEvent(ItemAttributeModifierEvent event) {
            ItemStack itemStack = event.getItemStack();
            EquipmentSlot equipmentSlot;
            if (itemStack.getItem() instanceof Equipable equipable) {
                equipmentSlot = equipable.getEquipmentSlot();
            } else {
                equipmentSlot = itemStack.getEquipmentSlot();
            }

            if (equipmentSlot != null) {
                EnchantmentHelper.runIterationOnItem(itemStack, (pEnchantment, pLevel) -> {
                    pEnchantment.value().getEffects(UEEnchantmentEffectComponents.LAST_STAND.value()).forEach(conditionaLEffect -> {
                        if (conditionaLEffect.effect() instanceof LastStandEffect lastStandEffect) {
                            AttributeModifier attributeModifier = lastStandEffect.getAttributeModifier(pLevel, itemStack, equipmentSlot);
                            if (attributeModifier != null) {
                                event.addModifier(lastStandEffect.attribute(), attributeModifier, EquipmentSlotGroup.ARMOR);
                            }
                        }
                    });
                });
            }
        }


        @SubscribeEvent(priority = EventPriority.LOW)
        public static void cutDown(LivingIncomingDamageEvent event) {
            DamageSourceExtraData extraData = ((IDamageSourceExtension) event.getSource()).src$getExtraData();
            if (event.getSource().getDirectEntity() instanceof LivingEntity attacker && extraData.isMeleeDamageToEntity(event.getEntity().getId())) {
                float attackerMaxHealth = attacker.getMaxHealth();
                float percentage = ((event.getEntity().getHealth() - attackerMaxHealth) / attackerMaxHealth) * 100;
                if (percentage > 0) {
                    Pair<LevelBasedValue, Integer> pair = EnchantmentHelper.getHighestLevel(attacker.getMainHandItem(), UEEnchantmentEffectComponents.CUT_DOWN.value());
                    if (pair != null) {
                        float damageMultiplier = (float) Math.min(pair.getFirst().calculate(pair.getSecond()) * percentage, 0.1 * pair.getSecond());
                        extraData.addFinalDamageAddition(damageMultiplier * event.getContainer().getNewDamage());
                    }
                }
            }
        }

        @SubscribeEvent
        public static void lifeStealAndManaSteal(LivingDamageEvent.Pre event) {
            if (event.getSource().getEntity() instanceof LivingEntity attacker && event.getSource().is(SRCDamageTags.CAN_LIFE_STEAL)) {
                DamageSourceExtraData extraData = ((IDamageSourceExtension) event.getSource()).src$getExtraData();
                if (extraData.isMeleeDamageToEntity(event.getEntity().getId())) {
                    float actualDamage = Math.min(event.getEntity().getHealth(), event.getNewDamage());

                    int lifeStealLevel = ItemEnchantmentHelper.getEnchantmentLevel(attacker.getMainHandItem(), UEEnchantments.LIFE_STEAL);
                    float healAmount = Math.min(actualDamage * lifeStealLevel * EnchantmentConfigs.LifeSteal.healPercentPerLevel, attacker.getMaxHealth() * EnchantmentConfigs.LifeSteal.maxHealPercentBaseMaxHealth * lifeStealLevel);
                    attacker.heal(healAmount);

                    if (attacker instanceof Player player) {
                        PlayerExtraData playerExtraData = player.getCapability(SRCCapabilities.SRC_PLAYER_DATA);

                        if (playerExtraData != null) {
                            int manaStealLevel = ItemEnchantmentHelper.getEnchantmentLevel(attacker.getMainHandItem(), UEEnchantments.MANA_STEAL);
                            float manaRegainAmount = (float) Math.min(actualDamage * manaStealLevel * EnchantmentConfigs.ManaSteal.ManaRegainPercentPerLevel, player.getAttributeValue(SRCAttributes.MAX_MANA) * EnchantmentConfigs.ManaSteal.maxManaRegainPercentBaseMaxMana * manaStealLevel);
                            playerExtraData.regainMana(manaRegainAmount);
                        }

                    }
                }

            }
        }

        @SubscribeEvent
        public static void GetEnchantmentLevelEvent(GetEnchantmentLevelEvent event) {
            int legendLevel = 0;
            for (Holder<Enchantment> holder : event.getEnchantments().keySet()) {
                if (holder.value().effects().has(UEEnchantmentEffectComponents.ULTIMATE.value())) {
                    int newLevel = event.getEnchantments().getLevel(holder);
                    legendLevel = Math.max(legendLevel, newLevel);
                }
            }
            for (Holder<Enchantment> holder : event.getEnchantments().keySet()) {
                if (event.getEnchantments().getLevel(holder) > 0) {
                    if (!holder.value().effects().has(UEEnchantmentEffectComponents.ULTIMATE.value()) && holder.value().getMaxLevel() > 1) {
                        int newLevel = event.getEnchantments().getLevel(holder) + legendLevel;
                        if (holder.is(Enchantments.QUICK_CHARGE)) {
                            newLevel = Math.min(newLevel, 5);
                        }
                        event.getEnchantments().set(holder, newLevel);
                    }
                }
            }
        }

        @SubscribeEvent
        public static void LivingEquipmentChangeEvent(LivingEquipmentChangeEvent event) {
            LivingEntity livingEntity = event.getEntity();
            EquipmentSlot slot = event.getSlot();
            ItemStack form = event.getFrom();
            ItemStack to = event.getTo();
            Pair<LegendComponent, Integer> pairFrom = EnchantmentHelper.getHighestLevel(form, UEEnchantmentEffectComponents.LEGEND.value());
            if (pairFrom != null) {
                livingEntity.getAttributes().supplier.instances.keySet().forEach((attributeHolder) -> {
                    Objects.requireNonNull(livingEntity.getAttributes().getInstance(attributeHolder)).removeModifier(LegendComponent.idForSlot(slot));
                });
            }
            Pair<LegendComponent, Integer> pairTo = EnchantmentHelper.getHighestLevel(to, UEEnchantmentEffectComponents.LEGEND.value());
            if (pairTo != null) {
                livingEntity.getAttributes().supplier.instances.keySet().forEach((attributeHolder) -> {
                    Attribute.Sentiment sentiment = attributeHolder.value().sentiment;
                    Objects.requireNonNull(livingEntity.getAttributes().getInstance(attributeHolder)).removeModifier(LegendComponent.idForSlot(slot));
                    if (sentiment == Attribute.Sentiment.POSITIVE) {
                        Objects.requireNonNull(livingEntity.getAttributes().getInstance(attributeHolder)).addTransientModifier(new AttributeModifier(LegendComponent.idForSlot(slot), pairTo.getFirst().attributeMultiplePerLevel() * pairTo.getSecond(), AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                    } else if (sentiment == Attribute.Sentiment.NEGATIVE) {
                        Objects.requireNonNull(livingEntity.getAttributes().getInstance(attributeHolder)).addTransientModifier(new AttributeModifier(LegendComponent.idForSlot(slot), -pairTo.getFirst().attributeMultiplePerLevel() * pairTo.getSecond(), AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                    }
                });
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void BlockDropsEvent(BlockDropsEvent event) {
            if (event.getBreaker() instanceof LivingEntity) {
                RecipeManager.CachedCheck<SingleRecipeInput, SmeltingRecipe> quickCheck = RecipeManager.createCheck(RecipeType.SMELTING);
                if (EnchantmentHelper.getHighestLevel(event.getTool(), UEEnchantmentEffectComponents.SMELTING.value()) != null) {
                    for (ItemEntity drop : event.getDrops()) {
                        int count = drop.getItem().getCount();
                        RecipeHolder<SmeltingRecipe> recipeholder = quickCheck.getRecipeFor(new SingleRecipeInput(drop.getItem()), event.getLevel()).orElse(null);
                        if (recipeholder != null) {
                            ItemStack outputItemStack = recipeholder.value().assemble(new SingleRecipeInput(drop.getItem()), event.getLevel().registryAccess());
                            float exp = recipeholder.value().getExperience() * count;
                            int i = Mth.floor(exp);
                            float f = Mth.frac(exp);
                            if (f != 0.0F && Math.random() < (double) f) {
                                i++;
                            }
                            event.setDroppedExperience(event.getDroppedExperience() + i);
                            drop.setItem(outputItemStack.copyWithCount(count));
                        }
                    }
                }
            }
        }

        @SubscribeEvent
        public static void extraShotCountEffect(LivingEntityUseItemEvent.Stop event) {
            MutableInt extraShotCount = new MutableInt(0);
            ItemStack itemStack = event.getItem();
            EnchantmentHelper.runIterationOnItem(itemStack, (holder, i) -> {
                holder.value().getEffects(UEEnchantmentEffectComponents.EXTRA_SHOOT_COUNT.value()).forEach(condition -> {
                    extraShotCount.setValue(condition.effect().process(i, event.getEntity().getRandom(), extraShotCount.intValue()));
                });
            });

            for (int i = 0; i < extraShotCount.intValue(); i++) {
                SimpleSchedule.addSchedule(event.getEntity().level(), new SimpleSchedule.Wait(() -> {
                    itemStack.releaseUsing(event.getEntity().level(), event.getEntity(), event.getDuration());
                }, 5));
            }
        }

        @SubscribeEvent
        public static void modifyProjectile(ModifyProjectileEvent event) {
            ItemEnchantments enchantments = event.itemStack.getAllEnchantments(event.serverLevel.registryAccess().lookupOrThrow(Registries.ENCHANTMENT));
            if (enchantments.keySet().stream().anyMatch(enchantmentHolder -> enchantmentHolder.getKey() == ApothicEnchantingEnchantments.TERMINATOR)) {
                ((IProjectileMixinExtension) event.projectile).src$setBypassesCooldownHit(true);
            }
        }

        @SubscribeEvent
        public static void VanquisherEffect(ModifyDamageEvent.AfterCritical event) {


            if (event.getDamageContainer().getSource().getDirectEntity() instanceof LivingEntity livingEntity && event.getExtraData().isMeleeDamageToEntity(event.getEntity().getId())) {
                if (livingEntity instanceof Player player && player.getAttackStrengthScale(0) < 0.5) {
                    return;
                }

                Pair<VanquisherComponent, Integer> pair = EnchantmentHelper.getHighestLevel(livingEntity.getMainHandItem(), UEEnchantmentEffectComponents.VANQUISHER.value());
                if (pair == null) {
                    return;
                }

                if (livingEntity.getEffect(MobEffects.ACTIVE_VANQUISHER) != null) {
                    event.getExtraData().addAdditionTags(DamageTypeTags.BYPASSES_COOLDOWN);
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.ACTIVE_VANQUISHER, pair.getFirst().buffDuration(), 0, false, false, true));
                    return;
                }

                MobEffectInstance instance = livingEntity.getEffect(MobEffects.UN_ACTIVE_VANQUISHER);
                int amplifier = 0;
                if (instance != null) {
                    amplifier = instance.getAmplifier() + 1;
                }


                livingEntity.addEffect(new MobEffectInstance(MobEffects.UN_ACTIVE_VANQUISHER, pair.getFirst().buffDuration(), amplifier, false, false, true));
            }
        }

        public static class IronsSpellBooksEvents {
            @SubscribeEvent(priority = EventPriority.LOWEST)
            public static void SpellOnCastEvent(SpellOnCastEvent event) {
                for (ItemStack itemStack : event.getEntity().getArmorSlots()) {
                    Equipable equipable = Equipable.get(itemStack);
                    if (!itemStack.isEmpty() && equipable != null) {
                        PlayerData playerData = event.getEntity().getData(AttachmentTypes.PLAYER_DATA);
                        playerData.hardenedManaEffect.addArmor(event.getEntity(), equipable.getEquipmentSlot(), event.getManaCost() * 0.01F, ItemEnchantmentHelper.getEnchantmentLevel(itemStack, IronsSpellBooksEnchantments.HARDENED_MANA));
                    }
                }
            }
        }
    }

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
    public static class Mod {
        @SubscribeEvent
        public static void RegisterCapabilitiesEvent(RegisterCapabilitiesEvent event) {

        }
    }
}
