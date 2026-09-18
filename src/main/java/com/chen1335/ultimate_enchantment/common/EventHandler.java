package com.chen1335.ultimate_enchantment.common;


import com.chen1335.ultimate_enchantment.API.AttachmentTypes;
import com.chen1335.ultimate_enchantment.API.UEDamageTypeTags;
import com.chen1335.ultimate_enchantment.UltimateEnchantment;
import com.chen1335.ultimate_enchantment.attachmentDatas.CommonEntityData;
import com.chen1335.ultimate_enchantment.attachmentDatas.PlayerData;
import com.chen1335.ultimate_enchantment.attachmentDatas.UEProjectileData;
import com.chen1335.ultimate_enchantment.config.CommonConfig;
import com.chen1335.ultimate_enchantment.enchantment.EnchantmentBasic;
import com.chen1335.ultimate_enchantment.enchantment.UEEnchantments;
import com.chen1335.ultimate_enchantment.enchantment.enchantments.*;
import com.chen1335.ultimate_enchantment.loot.BonusLoot;
import com.chen1335.ultimate_enchantment.loot.predicates.CreeperIsPoweredCondition;
import com.chen1335.ultimate_enchantment.mixinsAPI.minecraft.IItemStackMixin;
import com.chen1335.ultimate_enchantment.mixinsAPI.minecraft.IUEEntityExtension;
import com.chen1335.ultimate_enchantment.mobEffect.MobEffects;
import com.chen1335.ultimate_enchantment.netWork.BreakSpeedMultiplierPack;
import com.chen1335.ultimate_enchantment.netWork.TearParticlesPack;
import com.chen1335.ultimate_enchantment.tags.UEEnchantmentTags;
import com.chen1335.ultimate_enchantment.utils.UEEnchantmentHelper;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.equipment.Equippable;
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
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
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
            if (!event.getEntity().level().isClientSide()) {
                event.getEntity().getData(AttachmentTypes.PLAYER_DATA).tick(event.getEntity());
            }
        }

        @SubscribeEvent
        public static void EntityTickEvent(EntityTickEvent.Post event) {
            if (!event.getEntity().level().isClientSide()) {
                event.getEntity().getExistingData(AttachmentTypes.COMMON_ENTITY).ifPresent(CommonEntityData::tick);
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
            // 26.1：Equipable 接口已被移除，改为读取物品的 Equippable 数据组件；
            // 且 ItemStack#getEquipmentSlot 不再存在，物品无该组件时按主手处理。
            Equippable equippable = itemStack.get(DataComponents.EQUIPPABLE);
            equipmentSlot = equippable != null ? equippable.slot() : EquipmentSlot.MAINHAND;

            HolderLookup.RegistryLookup<Enchantment> lookup = EnchantmentLookup.get();

            for (Object2IntMap.Entry<Holder<Enchantment>> holderEntry : itemStack.getAllEnchantments(lookup).entrySet()) {
                EnchantmentBasic enchantmentBasic = UEEnchantments.MAP.get(holderEntry.getKey().getKey());
                if (enchantmentBasic != null && holderEntry.getIntValue() > 0) {
                    enchantmentBasic.addModifier(event, holderEntry.getIntValue(), equipmentSlot);
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
                    if (Float.isNaN(damageMultiplier)) {
                        return;
                    }
                    event.setAmount(event.getAmount() * (damageMultiplier + 1));
                }
            }
        }

        @SubscribeEvent(priority = EventPriority.LOW)
        public static void ultimateSlayer(LivingIncomingDamageEvent event) {
            if (!(event.getSource().getEntity() instanceof Player attacker)
                    || !event.getSource().is(UEDamageTypeTags.IS_ATTACK)) {
                return;
            }
            // Boss 判定比遍历装备槽便宜，而绝大多数挨打的目标都不是 Boss，先判。
            if (!UltimateSlayer.canApply(event.getEntity())) {
                return;
            }

            float bonus = UltimateSlayer.sumPerSlot(attacker, UltimateSlayer.DAMAGE_BONUS);
            if (bonus > 0.0F) {
                event.setAmount(event.getAmount() * (1 + bonus));
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void playerBreakSpeed(PlayerEvent.BreakSpeed event) {
            float breakSpeedMultiplier = event.getEntity().getData(AttachmentTypes.PLAYER_DATA).breakSpeedMultiplier;
            event.setNewSpeed((1 + breakSpeedMultiplier) * event.getNewSpeed());
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void playerBreakBlock(BreakBlockEvent event) {
            if (!event.isCanceled()) {
                int lvl = UEEnchantments.KINETIC_ENERGY.getEnchantmentLevel(event.getPlayer().getMainHandItem(), event.getPlayer().level());
                if (lvl > 0) {
                    SimpleBindings bindings = LifeSteal.buildBindings(lvl);

                    PlayerData data = event.getPlayer().getData(AttachmentTypes.PLAYER_DATA);
                    data.breakSpeedMultiplier = Math.min(data.breakSpeedMultiplier + KineticEnergy.INCREMENT.calculate(bindings), KineticEnergy.MAX_SPEED.calculate(bindings));
                    if (!event.getPlayer().level().isClientSide()) {
                        PacketDistributor.sendToPlayer((ServerPlayer) event.getPlayer(), new BreakSpeedMultiplierPack(data.breakSpeedMultiplier));
                    }
                    data.breakSpeedMultiplierRemainingTime = KineticEnergy.KEEP_TIME.calculate(bindings);
                }
            }
        }

        @SubscribeEvent
        public static void lifeSteal(LivingDamageEvent.Post event) {
            if (event.getSource().getEntity() instanceof LivingEntity attacker && event.getSource().is(UEDamageTypeTags.IS_ATTACK)) {
                ItemStack itemStack = attacker.getWeaponItem();
                float actualDamage = event.getInflictedDamage();
                {
                    int lvl = UEEnchantments.LIFE_STEAL.getEnchantmentLevel(itemStack, event.getEntity().level());
                    if (lvl > 0) {
                        SimpleBindings bindings = LifeSteal.buildBindings(lvl);
                        float healAmount = Math.min(actualDamage * LifeSteal.HEAL_PERCENT.calculate(bindings), attacker.getMaxHealth() * LifeSteal.MAX_PERCENT.calculate(bindings));
                        attacker.heal(healAmount);
                    }
                }
                {
                    if (attacker.hasEffect(MobEffects.ACTIVE_VANQUISHER)) {
                        attacker.heal(actualDamage * Vanquisher.LIFE_STEAL.calculate(new SimpleBindings()));
                    }
                }
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void GetEnchantmentLevelEvent(GetEnchantmentLevelEvent event) {
            for (Object2IntMap.Entry<Holder<Enchantment>> entry : event.getStack().getTagEnchantments().entrySet()) {
                if (entry.getKey().is(UEEnchantmentTags.ULTIMATE_ENCHANTMENT)) {
                    event.getEnchantments().set(entry.getKey(), entry.getIntValue());
                }
            }

            if (event.getTargetEnchant() != null && event.getTargetEnchant().is(UEEnchantments.ULTIMATE.getKey())) {
                return;
            }
            int lvl = UEEnchantments.ULTIMATE.getEnchantmentLevel((ItemStack) event.getStack(), event.getLookup());
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
                    Objects.requireNonNull(livingEntity.getAttributes().getInstance(attributeHolder)).removeModifier(Legend.idForSlot(slot));
                });
            }

            int lvl = UEEnchantments.LEGEND.getEnchantmentLevel(to, livingEntity.level());
            if ((slot.isArmor() || event.getSlot().equals(livingEntity.getEquipmentSlotForItem(to))) && lvl > 0) {
                SimpleBindings bindings = Legend.buildBindings(lvl);
                livingEntity.getAttributes().supplier.instances.keySet().forEach((attributeHolder) -> {
                    Attribute.Sentiment sentiment = attributeHolder.value().sentiment;
                    Objects.requireNonNull(livingEntity.getAttributes().getInstance(attributeHolder)).removeModifier(Legend.idForSlot(slot));
                    if (sentiment == Attribute.Sentiment.POSITIVE && !CommonConfig.loadedLegendBlackList.contains(attributeHolder.value())) {
                        Objects.requireNonNull(livingEntity.getAttributes().getInstance(attributeHolder)).addTransientModifier(new AttributeModifier(Legend.idForSlot(slot), Legend.ATTRIBUTE_BONUS.calculate(bindings), AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
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
                            ItemStack outputItemStack = recipeholder.value().assemble(new SingleRecipeInput(drop.getItem()));
                            float exp = recipeholder.value().experience() * count;
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
            if (event.getSource().getEntity() instanceof Player player && event.getSource().is(UEDamageTypeTags.IS_ATTACK)) {
                if (player.getData(AttachmentTypes.PLAYER_DATA).vanquisherCooldown > 0) {
                    return;
                }

                int lvl = UEEnchantments.VANQUISHER.getEnchantmentLevel(player.getWeaponItem(), player.level());
                if (lvl > 0) {
                    SimpleBindings bindings = Vanquisher.buildBindings(lvl);
                    player.getData(AttachmentTypes.PLAYER_DATA).vanquisherCooldown = Math.round(Vanquisher.COOL_DOWN.calculate(bindings));
                    int buffDuration = Math.round(Vanquisher.BUFF_DURATION.calculate(bindings));
                    if (player.getEffect(MobEffects.ACTIVE_VANQUISHER) != null) {
                        event.getEntity().invulnerableTime = 0;
                        player.addEffect(new MobEffectInstance(MobEffects.ACTIVE_VANQUISHER, buffDuration, 0, false, false, true));
                        return;
                    }

                    MobEffectInstance instance = player.getEffect(MobEffects.UN_ACTIVE_VANQUISHER);
                    int amplifier = 0;
                    if (instance != null) {
                        amplifier = instance.getAmplifier() + 1;
                    }


                    player.addEffect(new MobEffectInstance(MobEffects.UN_ACTIVE_VANQUISHER, buffDuration, amplifier, false, false, true));
                }
            }
        }

        @SubscribeEvent
        public static void LivingGetProjectileEvent(LivingGetProjectileEvent event) {
            if (IItemStackMixin.class.cast(event.getProjectileWeaponItemStack()).ue$isLethalTempoShoot()) {
                event.setProjectileItemStack(event.getProjectileItemStack().copy());
            }
        }

        @SubscribeEvent
        public static void LootTableLoadEvent(LootTableLoadEvent event) {
            Identifier lootTableId = event.getName();
            HolderLookup.Provider registries = event.getRegistries();
            List<LootPool> pools = event.getTable().pools;
            if (lootTableId.equals(BuiltInLootTables.END_CITY_TREASURE.identifier()) && CommonConfig.willEndCityTreasureLootUltimateEnchant.getAsBoolean()) {
                List<Holder<Enchantment>> holders = new ArrayList<>();
                UEEnchantmentHelper.getEnchantment(registries, UEEnchantments.LETHAL_TEMPO.getKey()).ifPresent(holders::add);
                UEEnchantmentHelper.getEnchantment(registries, UEEnchantments.VANQUISHER.getKey()).ifPresent(holders::add);
                UEEnchantmentHelper.getEnchantment(registries, UEEnchantments.TEAR.getKey()).ifPresent(holders::add);
                UEEnchantmentHelper.getEnchantment(registries, UEEnchantments.ULTIMATE_SLAYER.getKey()).ifPresent(holders::add);
                UEEnchantmentHelper.getEnchantment(registries, UEEnchantments.THE_FORTRESS.getKey()).ifPresent(holders::add);
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
            } else if (EntityType.ENDER_DRAGON.getDefaultLootTable().map(ResourceKey::identifier).filter(lootTableId::equals).isPresent()) {
                UEEnchantmentHelper.getEnchantment(registries, UEEnchantments.ULTIMATE.getKey()).ifPresent(holder -> {
                    LootPool.Builder builder = LootPool.lootPool();
                    LootPoolSingletonContainer.Builder<?> item = LootItem.lootTableItem(Items.ENCHANTED_BOOK);
                    item.apply(new SetEnchantmentsFunction.Builder().withEnchantment(holder, ConstantValue.exactly(2))).apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0, 0.2F)));
                    builder.add(item).build();
                    pools.add(builder.build());
                });
            } else if (EntityType.WITHER.getDefaultLootTable().map(ResourceKey::identifier).filter(lootTableId::equals).isPresent()) {
                UEEnchantmentHelper.getEnchantment(registries, UEEnchantments.LEGEND.getKey()).ifPresent(holder -> {
                    LootPool.Builder builder = LootPool.lootPool();
                    LootPoolSingletonContainer.Builder<?> item = LootItem.lootTableItem(Items.ENCHANTED_BOOK);
                    item.apply(new SetEnchantmentsFunction.Builder().withEnchantment(holder, ConstantValue.exactly(1))).apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0, 0.2F)));
                    builder.add(item).build();
                    pools.add(builder.build());
                });
            } else if (EntityType.WARDEN.getDefaultLootTable().map(ResourceKey::identifier).filter(lootTableId::equals).isPresent()) {
                UEEnchantmentHelper.getEnchantment(registries, UEEnchantments.LAST_STAND.getKey()).ifPresent(holder -> {
                    LootPool.Builder builder = LootPool.lootPool();
                    LootPoolSingletonContainer.Builder<?> item = LootItem.lootTableItem(Items.ENCHANTED_BOOK);
                    item.apply(new SetEnchantmentsFunction.Builder().withEnchantment(holder, ConstantValue.exactly(1))).apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0, 0.2F)));
                    builder.add(item).build();
                    pools.add(builder.build());
                });
            } else if (EntityType.CREEPER.getDefaultLootTable().map(ResourceKey::identifier).filter(lootTableId::equals).isPresent()) {
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
            registrar.playToClient(TearParticlesPack.TYPE, TearParticlesPack.STREAM_CODEC, TearParticlesPack::handler);

        }
    }
}
