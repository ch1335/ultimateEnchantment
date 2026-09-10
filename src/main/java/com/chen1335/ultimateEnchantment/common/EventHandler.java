package com.chen1335.ultimateEnchantment.common;


import com.chen1335.ultimateEnchantment.API.AttachmentTypes;
import com.chen1335.ultimateEnchantment.API.UEDamageTypeTags;
import com.chen1335.ultimateEnchantment.AttachmentDatas.PlayerData;
import com.chen1335.ultimateEnchantment.AttachmentDatas.UEProjectileData;
import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.config.CommonConfig;
import com.chen1335.ultimateEnchantment.dataComponentType.UEDataComponentTypes;
import com.chen1335.ultimateEnchantment.enchantment.UEEnchantments;
import com.chen1335.ultimateEnchantment.enchantment.effectComponents.UltimateEnchantment.LegendComponent;
import com.chen1335.ultimateEnchantment.enchantment.enchantments.*;
import com.chen1335.ultimateEnchantment.loot.predicates.CreeperIsPoweredCondition;
import com.chen1335.ultimateEnchantment.mixinsAPI.minecraft.IItemStackMixin;
import com.chen1335.ultimateEnchantment.mixinsAPI.minecraft.IUEEntityExtension;
import com.chen1335.ultimateEnchantment.mobEffect.MobEffects;
import com.chen1335.ultimateEnchantment.netWork.BreakSpeedMultiplierPack;
import com.chen1335.ultimateEnchantment.tags.UEEnchantmentTags;
import com.chen1335.ultimateEnchantment.utils.UEEnchantmentHelper;
import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.network.SyncManaPacket;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import javax.script.SimpleBindings;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class EventHandler {
    @EventBusSubscriber()
    public static class Game {
        @SubscribeEvent
        public static void PlayerPreTick(PlayerTickEvent.Pre event) {
            if (!event.getEntity().level().isClientSide) {
                event.getEntity().getData(AttachmentTypes.PLAYER_DATA).tick(event.getEntity());
            }
        }

        @SubscribeEvent
        public static void QuickLatch(LivingEntityUseItemEvent.Tick event) {
            if (event.getEntity() instanceof Player player) {
                ItemStack itemStack = player.getUseItem();
                if (itemStack.getItem() instanceof ProjectileWeaponItem item) {
                    if (UEEnchantments.QUICK_LATCH.getEnchantmentLevel(itemStack, player.level()) > 0) {
                        if (BowItem.getPowerForTime(item.getUseDuration(itemStack, player) - player.getUseItemRemainingTicks()) >= 1) {
                            player.releaseUsingItem();
                        }
                    }
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
                int lvl = UEEnchantments.LAST_STAND.getEnchantmentLevel(itemStack, CommonHooks.resolveLookup(Registries.ENCHANTMENT));
                if (lvl > 0) {
                    SimpleBindings simpleBindings = LastStand.buildBindings(lvl);
                    if (itemStack.getOrDefault(UEDataComponentTypes.USER_HEALTH, 0).floatValue() <= itemStack.getOrDefault(UEDataComponentTypes.USER_MAX_HEALTH, 0).floatValue() * LastStand.HEALTH_THRESHOLD.calculate(simpleBindings)) {
                        AttributeModifier attributeModifier = new AttributeModifier(AttributeModifierId.LAST_STAND_ARMOR.withSuffix("/" + equipmentSlot.getSerializedName()), LastStand.ARMOR_BONUS.calculate(simpleBindings), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                        AttributeModifier attributeModifier1 = new AttributeModifier(AttributeModifierId.LAST_STAND_ARMOR_TOUGHNESS.withSuffix("/" + equipmentSlot.getSerializedName()), LastStand.ARMOR_BONUS.calculate(simpleBindings), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                        event.addModifier(Attributes.ARMOR, attributeModifier, EquipmentSlotGroup.ARMOR);
                        event.addModifier(Attributes.ARMOR_TOUGHNESS, attributeModifier1, EquipmentSlotGroup.ARMOR);
                    }
                }
            }
        }


        @SubscribeEvent(priority = EventPriority.LOW)
        public static void cutDown(LivingIncomingDamageEvent event) {
            if (event.getSource().getDirectEntity() instanceof LivingEntity attacker && event.getSource().is(UEDamageTypeTags.IS_ATTACK)) {
                int lvl = UEEnchantments.CUT_DOWN.getEnchantmentLevel(attacker.getWeaponItem(), attacker.level());
                if (lvl > 0) {
                    SimpleBindings simpleBindings = CutDown.buildBindings(lvl);
                    float attackerMaxHealth = attacker.getMaxHealth();
                    float percentage = ((event.getEntity().getHealth() - attackerMaxHealth) / attackerMaxHealth) * 100;
                    float damageMultiplier = Math.clamp(percentage * CutDown.DAMAGE_MUL.calculate(simpleBindings), 0, CutDown.MAX_DAMAGE_MUL.calculate(simpleBindings));
                    event.setAmount(event.getAmount() * (damageMultiplier + 1));
                }
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void playerBreakSpeed(PlayerEvent.BreakSpeed event) {
            float breakSpeedMultiplier = event.getEntity().getData(AttachmentTypes.PLAYER_DATA).breakSpeedMultiplier;
            event.setNewSpeed((1 + breakSpeedMultiplier) * event.getNewSpeed());
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void playerBreakBlock(BlockEvent.BreakEvent event) {
            if (!event.isCanceled()) {
                int lvl = UEEnchantments.KINETIC_ENERGY.getEnchantmentLevel(event.getPlayer().getMainHandItem(), event.getPlayer().level());
                if (lvl > 0) {
                    SimpleBindings bindings = LifeSteal.buildBindings(lvl);

                    PlayerData data = event.getPlayer().getData(AttachmentTypes.PLAYER_DATA);
                    data.breakSpeedMultiplier = Math.min(data.breakSpeedMultiplier + Math.round(KineticEnergy.INCREMENT.calculate(bindings)), Math.round(KineticEnergy.MAX_SPEED.calculate(bindings)));
                    if (!event.getPlayer().level().isClientSide()) {
                        PacketDistributor.sendToPlayer((ServerPlayer) event.getPlayer(), new BreakSpeedMultiplierPack(data.breakSpeedMultiplier));
                    }
                    data.breakSpeedMultiplierRemainingTime = KineticEnergy.KEEP_TIME.calculate(bindings);
                }
            }
        }

        @SubscribeEvent
        public static void lifeStealAndManaSteal(LivingDamageEvent.Pre event) {
            if (event.getSource().getEntity() instanceof LivingEntity attacker && event.getSource().is(UEDamageTypeTags.IS_ATTACK)) {
                ItemStack itemStack = attacker.getWeaponItem();
                float actualDamage = Math.min(event.getEntity().getHealth(), event.getNewDamage());
                {
                    int lvl = UEEnchantments.LIFE_STEAL.getEnchantmentLevel(itemStack, event.getEntity().level());
                    if (lvl > 0) {
                        SimpleBindings bindings = LifeSteal.buildBindings(lvl);
                        float healAmount = Math.min(actualDamage * LifeSteal.HEAL_PERCENT.calculate(bindings), attacker.getMaxHealth() * LifeSteal.MAX_PERCENT.calculate(bindings));
                        attacker.heal(healAmount);
                    }
                }

                {
                    int lvl = UEEnchantments.MANA_STEAL.getEnchantmentLevel(itemStack, event.getEntity().level());
                    if (lvl > 0 && UltimateEnchantment.isIronsSpellBooksLoaded() && attacker instanceof ServerPlayer player) {
                        SimpleBindings bindings = LifeSteal.buildBindings(lvl);
                        float manaRegainAmount = (float) Math.min(ManaSteal.MANA_PERCENT.calculate(bindings), player.getAttributeValue(AttributeRegistry.MAX_MANA) * ManaSteal.MAX_PERCENT.calculate(bindings));
                        MagicData.getPlayerMagicData(player).addMana(manaRegainAmount);
                        PacketDistributor.sendToPlayer(player, new SyncManaPacket(MagicData.getPlayerMagicData(player)));
                    }
                }
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void GetEnchantmentLevelEvent(GetEnchantmentLevelEvent event) {
            if (event.getTargetEnchant() != null && event.getTargetEnchant().is(UEEnchantments.ULTIMATE.getKey())) {
                return;
            }
            int lvl = UEEnchantments.ULTIMATE.getEnchantmentLevel(event.getStack(), event.getLookup());
            if (lvl > 0) {
                int addLevel = Math.round(Ultimate.LEVEL_ADD.calculate(Ultimate.buildBindings(lvl)));
                for (Holder<Enchantment> holder : event.getEnchantments().keySet()) {
                    if (event.getEnchantments().getLevel(holder) > 0) {
                        if (!holder.is(UEEnchantmentTags.IGNORE_ULTIMATE) && !holder.is(UEEnchantments.ULTIMATE.getKey()) && holder.value().getMaxLevel() > 1) {
                            int newLevel = event.getEnchantments().getLevel(holder) + addLevel;
                            if (holder.is(Enchantments.QUICK_CHARGE)) {
                                newLevel = Math.min(newLevel, 5);
                            }
                            event.getEnchantments().set(holder, newLevel);
                        }
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
            if (UEEnchantments.LEGEND.getEnchantmentLevel(form, livingEntity.level()) > 0) {
                livingEntity.getAttributes().supplier.instances.keySet().forEach((attributeHolder) -> {
                    Objects.requireNonNull(livingEntity.getAttributes().getInstance(attributeHolder)).removeModifier(LegendComponent.idForSlot(slot));
                });
            }

            int lvl = UEEnchantments.LEGEND.getEnchantmentLevel(to, livingEntity.level());
            if (lvl > 0) {
                SimpleBindings bindings = Legend.buildBindings(lvl);
                livingEntity.getAttributes().supplier.instances.keySet().forEach((attributeHolder) -> {
                    Attribute.Sentiment sentiment = attributeHolder.value().sentiment;
                    Objects.requireNonNull(livingEntity.getAttributes().getInstance(attributeHolder)).removeModifier(LegendComponent.idForSlot(slot));
                    if (sentiment == Attribute.Sentiment.POSITIVE && !CommonConfig.loadedLegendBlackList.contains(attributeHolder.value())) {
                        Objects.requireNonNull(livingEntity.getAttributes().getInstance(attributeHolder)).addTransientModifier(new AttributeModifier(LegendComponent.idForSlot(slot), Legend.ATTRIBUTE_BONUS.calculate(bindings), AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                    }
                });
            }
        }

        private static final RecipeManager.CachedCheck<SingleRecipeInput, SmeltingRecipe> SMELTING_RECIPE_CACHED_CHECK = RecipeManager.createCheck(RecipeType.SMELTING);


        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void BlockDropsEvent(BlockDropsEvent event) {
            if (event.getBreaker() instanceof LivingEntity) {

                if (UEEnchantments.SMELTING.getEnchantmentLevel(event.getTool(), event.getLevel()) > 0) {
                    for (ItemEntity drop : event.getDrops()) {
                        int count = drop.getItem().getCount();
                        RecipeHolder<SmeltingRecipe> recipeholder = SMELTING_RECIPE_CACHED_CHECK.getRecipeFor(new SingleRecipeInput(drop.getItem()), event.getLevel()).orElse(null);
                        if (recipeholder != null) {
                            ItemStack outputItemStack = recipeholder.value().assemble(new SingleRecipeInput(drop.getItem()), event.getLevel().registryAccess());
                            float exp = recipeholder.value().getExperience() * count;
                            int i = Mth.floor(exp);
                            float f = Mth.frac(exp);
                            if (f != 0.0F && Math.random() < (double) f) {
                                i++;
                            }
                            event.setDroppedExperience(event.getDroppedExperience() + i);
                            drop.setItem(outputItemStack.copyWithCount(outputItemStack.getCount() * count));
                        }
                    }
                }
            }
        }


        @SubscribeEvent
        public static void VanquisherEffect(LivingIncomingDamageEvent event) {
            if (event.getSource().getEntity() instanceof LivingEntity livingEntity && event.getSource().is(UEDamageTypeTags.IS_ATTACK)) {
                if (livingEntity instanceof Player player && player.getAttackStrengthScale(0) < 0.5) {
                    return;
                }

                int lvl = UEEnchantments.VANQUISHER.getEnchantmentLevel(livingEntity.getWeaponItem(), livingEntity.level());
                if (lvl > 0) {
                    SimpleBindings bindings = Vanquisher.buildBindings(lvl);
                    int buffDuration = Math.round(Vanquisher.BUFF_DURATION.calculate(bindings));
                    if (livingEntity.getEffect(MobEffects.ACTIVE_VANQUISHER) != null) {
                        event.getEntity().invulnerableTime = 0;
                        livingEntity.addEffect(new MobEffectInstance(MobEffects.ACTIVE_VANQUISHER, buffDuration, 0, false, false, true));
                        return;
                    }

                    MobEffectInstance instance = livingEntity.getEffect(MobEffects.UN_ACTIVE_VANQUISHER);
                    int amplifier = 0;
                    if (instance != null) {
                        amplifier = instance.getAmplifier() + 1;
                    }


                    livingEntity.addEffect(new MobEffectInstance(MobEffects.UN_ACTIVE_VANQUISHER, buffDuration, amplifier, false, false, true));
                }
            }
        }

        @SubscribeEvent
        public static void LivingGetProjectileEvent(LivingGetProjectileEvent event) {
            if (IItemStackMixin.class.cast(event.getProjectileWeaponItemStack()).ue$isLethalTempoShoot()) {
                event.setProjectileItemStack(event.getProjectileItemStack().copy());
            }
        }

        public static class IronsSpellBooksEvents {
            @SubscribeEvent(priority = EventPriority.LOWEST)
            public static void SpellOnCastEvent(SpellOnCastEvent event) {
                for (ItemStack itemStack : event.getEntity().getArmorSlots()) {
                    Equipable equipable = Equipable.get(itemStack);
                    if (!itemStack.isEmpty() && equipable != null) {
                        PlayerData playerData = event.getEntity().getData(AttachmentTypes.PLAYER_DATA);

                        int lvl = UEEnchantments.HARDENED_MANA.getEnchantmentLevel(itemStack, event.getEntity().level());
                        if (lvl > 0) {
                            SimpleBindings bindings = LifeSteal.buildBindings(lvl);
                            playerData.hardenedManaEffect.addArmor(event.getEntity(), equipable.getEquipmentSlot(), HardenedMana.MANA_PERCENT.calculate(bindings) * event.getManaCost(), HardenedMana.MAX_ARMOR.calculate(bindings));
                        }
                    }
                }
            }
        }

        @SubscribeEvent
        public static void LootTableLoadEvent(LootTableLoadEvent event) {
            ResourceLocation lootTableId = event.getName();
            HolderLookup.Provider registries = event.getRegistries();
            List<LootPool> pools = event.getTable().pools;
            if (lootTableId.equals(BuiltInLootTables.END_CITY_TREASURE.location()) && CommonConfig.willEndCityTreasureLootUltimateEnchant) {
                List<Holder<Enchantment>> holders = new ArrayList<>();
                UEEnchantmentHelper.getEnchantment(registries, UEEnchantments.LETHAL_TEMPO.getKey()).ifPresent(holders::add);
                UEEnchantmentHelper.getEnchantment(registries, UEEnchantments.VANQUISHER.getKey()).ifPresent(holders::add);
                UEEnchantmentHelper.getEnchantment(registries, UEEnchantments.TEAR.getKey()).ifPresent(holders::add);
                LootPool.Builder builder = LootPool.lootPool();
                for (Holder<Enchantment> holder : holders) {
                    LootPoolSingletonContainer.Builder<?> item = LootItem.lootTableItem(Items.ENCHANTED_BOOK);

                    if (holder.is(UEEnchantments.VANQUISHER.getKey())) {
                        item.apply(new SetEnchantmentsFunction.Builder().withEnchantment(holder, ConstantValue.exactly(1))).setWeight(3);
                    } else {
                        item.apply(new SetEnchantmentsFunction.Builder().withEnchantment(holder, UniformGenerator.between(2, 3))).setWeight(10);
                    }

                    builder.add(item).build();
                }
                if (!holders.isEmpty()) {
                    builder.add(EmptyLootItem.emptyItem().setWeight(30));
                    pools.add(builder.build());
                }
            } else if (lootTableId.equals(EntityType.ENDER_DRAGON.getDefaultLootTable().location())) {
                UEEnchantmentHelper.getEnchantment(registries, UEEnchantments.ULTIMATE.getKey()).ifPresent(holder -> {
                    LootPool.Builder builder = LootPool.lootPool();
                    LootPoolSingletonContainer.Builder<?> item = LootItem.lootTableItem(Items.ENCHANTED_BOOK);
                    item.apply(new SetEnchantmentsFunction.Builder().withEnchantment(holder, ConstantValue.exactly(2))).apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0, 0.2F)));
                    builder.add(item).build();
                    pools.add(builder.build());
                });
            } else if (lootTableId.equals(EntityType.WITHER.getDefaultLootTable().location())) {
                UEEnchantmentHelper.getEnchantment(registries, UEEnchantments.LEGEND.getKey()).ifPresent(holder -> {
                    LootPool.Builder builder = LootPool.lootPool();
                    LootPoolSingletonContainer.Builder<?> item = LootItem.lootTableItem(Items.ENCHANTED_BOOK);
                    item.apply(new SetEnchantmentsFunction.Builder().withEnchantment(holder, ConstantValue.exactly(1))).apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0, 0.2F)));
                    builder.add(item).build();
                    pools.add(builder.build());
                });
            } else if (lootTableId.equals(EntityType.WARDEN.getDefaultLootTable().location())) {
                UEEnchantmentHelper.getEnchantment(registries, UEEnchantments.LAST_STAND.getKey()).ifPresent(holder -> {
                    LootPool.Builder builder = LootPool.lootPool();
                    LootPoolSingletonContainer.Builder<?> item = LootItem.lootTableItem(Items.ENCHANTED_BOOK);
                    item.apply(new SetEnchantmentsFunction.Builder().withEnchantment(holder, ConstantValue.exactly(1))).apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0, 0.2F)));
                    builder.add(item).build();
                    pools.add(builder.build());
                });
            } else if (lootTableId.equals(EntityType.CREEPER.getDefaultLootTable().location())) {
                UEEnchantmentHelper.getEnchantment(registries, UEEnchantments.THUNDER_BOLT.getKey()).ifPresent(holder -> {
                    LootPool.Builder builder = LootPool.lootPool();
                    LootPoolSingletonContainer.Builder<?> item = LootItem.lootTableItem(Items.ENCHANTED_BOOK);
                    item.apply(new SetEnchantmentsFunction.Builder().withEnchantment(holder, ConstantValue.exactly(1))).when(CreeperIsPoweredCondition.creeperIsPowered());
                    builder.add(item).build();
                    pools.add(builder.build());
                });
            }
        }

        @SubscribeEvent
        public static void EntityJoinLevelEvent(EntityJoinLevelEvent event) {
            Entity entity = event.getEntity();
            if (entity instanceof Projectile projectile && projectile.getOwner() != null && ((IUEEntityExtension) projectile.getOwner()).ue$isLethalTempoShooting()) {
                if (projectile instanceof AbstractArrow arrow) {
                    arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                }
                UEProjectileData data = projectile.getData(AttachmentTypes.PROJECTILE_DATA);
                data.isLethalTempoAdditionArrow = true;
            }
        }
    }


    @EventBusSubscriber()
    public static class Mod {
        @SubscribeEvent
        public static void RegisterPayloadHandlersEvent(RegisterPayloadHandlersEvent event) {
            final PayloadRegistrar registrar = event.registrar("1");
            registrar.playToClient(BreakSpeedMultiplierPack.TYPE, BreakSpeedMultiplierPack.STREAM_CODEC, BreakSpeedMultiplierPack::handler);

        }
    }
}
