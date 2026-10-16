package com.chen1335.ultimateEnchantment.common;


import com.chen1335.arcanumlib.modules.attribute.ArcanumAttributes;
import com.chen1335.arcanumlib.modules.attribute.AttributeAdjustment.AttributeAdjustment;
import com.chen1335.arcanumlib.modules.attribute.AttributeAdjustment.contexts.DamageContext;
import com.chen1335.arcanumlib.modules.attribute.events.LivingGetAttributeAdjustmentEvent;
import com.chen1335.arcanumlib.modules.loot.bonusLoot.event.BonusLootCheckEvent;
import com.chen1335.arcanumlib.modules.loot.bonusLoot.event.ModifyBonusLootEvent;
import com.chen1335.ultimateEnchantment.API.AttachmentTypes;
import com.chen1335.ultimateEnchantment.API.UEDamageTypeTags;
import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.attachmentDatas.CommonEntityData;
import com.chen1335.ultimateEnchantment.attachmentDatas.PlayerData;
import com.chen1335.ultimateEnchantment.attachmentDatas.UEProjectileData;
import com.chen1335.ultimateEnchantment.config.CommonConfig;
import com.chen1335.ultimateEnchantment.config.ServerConfig;
import com.chen1335.ultimateEnchantment.enchantment.EnchantmentBasic;
import com.chen1335.ultimateEnchantment.enchantment.UEEnchantments;
import com.chen1335.ultimateEnchantment.enchantment.enchantments.*;
import com.chen1335.ultimateEnchantment.enchantment.specialEnchantEffects.ThunderBoltEffect;
import com.chen1335.ultimateEnchantment.mixinsAPI.minecraft.IItemStackMixin;
import com.chen1335.ultimateEnchantment.mixinsAPI.minecraft.IUEEntityExtension;
import com.chen1335.ultimateEnchantment.mobEffect.MobEffects;
import com.chen1335.ultimateEnchantment.netWork.BreakSpeedMultiplierPack;
import com.chen1335.ultimateEnchantment.netWork.TearParticlesPack;
import com.chen1335.ultimateEnchantment.tags.UEEnchantmentTags;
import com.chen1335.ultimateEnchantment.utils.UEEnchantmentHelper;
import com.chen1335.ultimateEnchantment.utils.Util;
import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.network.SyncManaPacket;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import javax.script.SimpleBindings;
import java.util.List;
import java.util.Objects;

@EventBusSubscriber(modid = UltimateEnchantment.MODID)
public class EventHandler {
    @SubscribeEvent
    public static void PlayerPreTick(PlayerTickEvent.Pre event) {
        if (!event.getEntity().level().isClientSide) {
            event.getEntity().getData(AttachmentTypes.PLAYER_DATA).tick(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void EntityTickEvent(EntityTickEvent.Post event) {
        if (!event.getEntity().level().isClientSide) {
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
        if (itemStack.getItem() instanceof Equipable equipable) {
            equipmentSlot = equipable.getEquipmentSlot();
        } else {
            equipmentSlot = itemStack.getEquipmentSlot();
        }

        HolderLookup.RegistryLookup<Enchantment> lookup = EnchantmentLookup.get();

        for (Object2IntMap.Entry<Holder<Enchantment>> holderEntry : itemStack.getAllEnchantments(lookup).entrySet()) {
            EnchantmentBasic enchantmentBasic = UEEnchantments.MAP.get(holderEntry.getKey().getKey());
            if (enchantmentBasic != null && holderEntry.getIntValue() > 0) {
                enchantmentBasic.addModifier(event, holderEntry.getIntValue(), equipmentSlot);
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
                data.breakSpeedMultiplier = Math.min(data.breakSpeedMultiplier + KineticEnergy.INCREMENT.calculate(bindings), KineticEnergy.MAX_SPEED.calculate(bindings));
                if (!event.getPlayer().level().isClientSide()) {
                    PacketDistributor.sendToPlayer((ServerPlayer) event.getPlayer(), new BreakSpeedMultiplierPack(data.breakSpeedMultiplier));
                }
                data.breakSpeedMultiplierRemainingTime = KineticEnergy.KEEP_TIME.calculate(bindings);
            }
        }
    }

    @SubscribeEvent
    public static void lifeStealAndManaSteal(LivingDamageEvent.Post event) {
        if (event.getSource().getEntity() instanceof LivingEntity attacker && event.getSource().is(UEDamageTypeTags.IS_ATTACK)) {
            ItemStack itemStack = attacker.getWeaponItem();
            float actualDamage = event.getNewDamage();
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
                    float manaRegainAmount = (float) Math.min(actualDamage * ManaSteal.MANA_PERCENT.calculate(bindings), player.getAttributeValue(AttributeRegistry.MAX_MANA) * ManaSteal.MAX_PERCENT.calculate(bindings));
                    MagicData.getPlayerMagicData(player).addMana(manaRegainAmount);
                    PacketDistributor.sendToPlayer(player, new SyncManaPacket(MagicData.getPlayerMagicData(player)));
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
        // 把终极附魔的等级摁回物品 NBT 上记录的那一档，等于无视其他模组（如神化的影激宝石）
        // 通过本事件给出的加成。关掉这个开关就是允许它们被正常提升等级。
        if (ServerConfig.ultimateEnchantmentCantBeUpgradedCache) {
            for (Object2IntMap.Entry<Holder<Enchantment>> entry : event.getStack().getTagEnchantments().entrySet()) {
                if (entry.getKey().is(UEEnchantmentTags.ULTIMATE_ENCHANTMENT)) {
                    event.getEnchantments().set(entry.getKey(), entry.getIntValue());
                }
            }
        }

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

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void LivingIncomingDamageEvent$Normal(LivingIncomingDamageEvent event) {
        DamageSource source = event.getSource();
        Entity entity = source.getEntity();
        if (entity instanceof LivingEntity attacker && source.is(UEDamageTypeTags.IS_ATTACK)) {
            Util.addAttackedCount(attacker);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void LivingIncomingDamageEvent$Lowest(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();
        if (source.getEntity() instanceof LivingEntity attacker && source.is(UEDamageTypeTags.IS_ATTACK)) {
            if (Util.getAttackedCount(attacker) % 3 == 0) {
                ThunderBoltEffect.onAttack(attacker, event.getAmount(), entity);
            }
        }
    }


    @SubscribeEvent
    public static void LivingGetAttributeAdjustmentEvent(LivingGetAttributeAdjustmentEvent event) {
        Holder<Attribute> attribute = event.getAttribute();
        LivingEntity owner = event.getOwner();
        if (event.getContext() instanceof DamageContext(LivingEntity target, DamageSource source,float damage)) {
            //owner = attacker
            if (source.getEntity() == owner) {
                if (attribute == ArcanumAttributes.DAMAGE_MULTIPLIER) {
                    if (UltimateSlayer.canApply(target)) {
                        float bonus = UltimateSlayer.sumPerSlot(owner, UltimateSlayer.DAMAGE_BONUS);
                        if (bonus > 0.0F) {
                            event.getAdjustment().add(AttributeAdjustment.Operation.ADD_VALUE, bonus);
                        }
                    }

                    if (source.is(UEDamageTypeTags.IS_ATTACK)) {
                        {
                            int lvl = UEEnchantments.CUT_DOWN.getEnchantmentLevel(owner.getWeaponItem(), owner.level());
                            if (lvl > 0) {
                                SimpleBindings simpleBindings = CutDown.buildBindings(lvl);
                                float attackerMaxHealth = owner.getHealth();
                                float percentage = ((target.getHealth() - attackerMaxHealth) / attackerMaxHealth) * 100;
                                float damageMultiplier = Math.clamp(percentage * CutDown.DAMAGE_MUL.calculate(simpleBindings), 0, CutDown.MAX_DAMAGE_MUL.calculate(simpleBindings));
                                event.getAdjustment().add(AttributeAdjustment.Operation.ADD_VALUE, damageMultiplier);
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void ModifyBonusLootEvent(ModifyBonusLootEvent event) {
        LootContext lootContext = event.getLootContext();
        Entity thisEntityOrNull = lootContext.getParamOrNull(LootContextParams.THIS_ENTITY);
        Entity attackerOrNull = lootContext.getParamOrNull(LootContextParams.ATTACKING_ENTITY);
        BlockState blockState = event.getLootContext().getParamOrNull(LootContextParams.BLOCK_STATE);
        LivingEntity thisEntity = thisEntityOrNull instanceof LivingEntity ? (LivingEntity) thisEntityOrNull : null;
        LivingEntity attacker = attackerOrNull instanceof LivingEntity ? (LivingEntity) attackerOrNull : null;

        if (thisEntity != null && attacker != null) {
            if (UltimateSlayer.canApply(thisEntity)) {
                event.setRatio(event.getRatio() + UltimateSlayer.getRatio(thisEntity, attacker));
            }
        }
        if (blockState != null) {
            event.setRatio(event.getRatio() + Harvest.getRatio(lootContext, blockState));
        }

    }

    @SubscribeEvent
    public static void BonusLootCheckEvent(BonusLootCheckEvent event) {
        BlockState blockState = event.getLootContext().getParamOrNull(LootContextParams.BLOCK_STATE);
        if (blockState != null && blockState.getBlock() instanceof CropBlock cropBlock && cropBlock.isMaxAge(blockState)) {
            event.setResult(true);
        }
    }


    @SubscribeEvent
    public static void RegisterPayloadHandlersEvent(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(BreakSpeedMultiplierPack.TYPE, BreakSpeedMultiplierPack.STREAM_CODEC, BreakSpeedMultiplierPack::handler);
        registrar.playToClient(TearParticlesPack.TYPE, TearParticlesPack.STREAM_CODEC, TearParticlesPack::handler);
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
}
