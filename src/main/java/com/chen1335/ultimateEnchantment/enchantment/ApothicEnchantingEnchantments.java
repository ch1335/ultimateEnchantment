package com.chen1335.ultimateEnchantment.enchantment;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.enchantment.effectComponents.UltimateEnchantment.UEEnchantmentEffectComponents;
import com.chen1335.ultimateEnchantment.tags.UEEnchantmentTags;
import dev.shadowsoffire.apothic_attributes.api.ALObjects;
import dev.shadowsoffire.apothic_enchanting.ApothicEnchanting;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.AddValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApothicEnchantingEnchantments {
    public static final Map<ResourceKey<?>, List<ICondition>> conditions = new HashMap<>();

    public static final ResourceKey<Enchantment> QUICK_SHOOTING = key("quick_shooting");

    public static final ResourceKey<Enchantment> SCABBING = key("scabbing");

    public static final ResourceKey<Enchantment> TERMINATOR = key("terminator");

    public static void bootstrap(BootstrapContext<Enchantment> pContext) {
        HolderGetter<Enchantment> enchantmentHolderGetter = pContext.lookup(Registries.ENCHANTMENT);
        HolderGetter<Item> itemHolderGetter = pContext.lookup(Registries.ITEM);
        register(
                pContext,
                TERMINATOR,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        itemHolderGetter.getOrThrow(ItemTags.BOW_ENCHANTABLE),
                                        1,
                                        1,
                                        Enchantment.constantCost(80),
                                        Enchantment.constantCost(150),
                                        1,
                                        EquipmentSlotGroup.HAND
                                )
                        )
                        .exclusiveWith(enchantmentHolderGetter.getOrThrow(UEEnchantmentTags.ULTIMATE_ENCHANTMENT))
                        .withEffect(EnchantmentEffectComponents.PROJECTILE_COUNT, new AddValue(LevelBasedValue.perLevel(2.0F)))
                        .withEffect(EnchantmentEffectComponents.PROJECTILE_SPREAD, new AddValue(LevelBasedValue.perLevel(10.0F)))
                        .withEffect(
                                EnchantmentEffectComponents.ATTRIBUTES,
                                new EnchantmentAttributeEffect(
                                        ResourceLocation.fromNamespaceAndPath(UltimateEnchantment.MODID, "enchantment.terminator"),
                                        ALObjects.Attributes.DRAW_SPEED,
                                        new LevelBasedValue.Constant(0.5F),
                                        AttributeModifier.Operation.ADD_VALUE
                                )
                        )
                        .withEffect(
                                EnchantmentEffectComponents.ATTRIBUTES,
                                new EnchantmentAttributeEffect(
                                        ResourceLocation.fromNamespaceAndPath(UltimateEnchantment.MODID, "enchantment.terminator"),
                                        ALObjects.Attributes.CRIT_CHANCE,
                                        new LevelBasedValue.Constant(-0.75F),
                                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                                )
                        )
                        .withEffect(
                                EnchantmentEffectComponents.ATTRIBUTES,
                                new EnchantmentAttributeEffect(
                                        ResourceLocation.fromNamespaceAndPath(UltimateEnchantment.MODID, "enchantment.terminator"),
                                        ALObjects.Attributes.CRIT_DAMAGE,
                                        new LevelBasedValue.Constant(-0.5F),
                                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                                )
                        )
                        .withEffect(UEEnchantmentEffectComponents.EXTRA_SHOOT_COUNT.value(),
                                new AddValue(LevelBasedValue.perLevel(1.0F))
                        )
        );


        register(
                pContext,
                SCABBING,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        itemHolderGetter.getOrThrow(ItemTags.WEAPON_ENCHANTABLE),
                                        2,
                                        5,
                                        Enchantment.dynamicCost(50, 10),
                                        Enchantment.dynamicCost(90, 10),
                                        1,
                                        EquipmentSlotGroup.HAND
                                )
                        )
                        .exclusiveWith(HolderSet.direct(enchantmentHolderGetter.getOrThrow(UEEnchantments.PIERCE_THROUGH)))
                        .withEffect(
                                EnchantmentEffectComponents.ATTRIBUTES,
                                new EnchantmentAttributeEffect(
                                        ResourceLocation.fromNamespaceAndPath(UltimateEnchantment.MODID, "enchantment.quick_shooting"),
                                        ALObjects.Attributes.ARMOR_SHRED,
                                        LevelBasedValue.perLevel(0.05F),
                                        AttributeModifier.Operation.ADD_VALUE
                                )
                        )
        );

        register(
                pContext,
                QUICK_SHOOTING,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        itemHolderGetter.getOrThrow(ItemTags.BOW_ENCHANTABLE),
                                        2,
                                        5,
                                        Enchantment.dynamicCost(50, 10),
                                        Enchantment.dynamicCost(90, 10),
                                        1,
                                        EquipmentSlotGroup.HAND
                                )
                        )
                        .withEffect(
                                EnchantmentEffectComponents.ATTRIBUTES,
                                new EnchantmentAttributeEffect(
                                        ResourceLocation.fromNamespaceAndPath(UltimateEnchantment.MODID, "enchantment.quick_shooting"),
                                        ALObjects.Attributes.DRAW_SPEED,
                                        LevelBasedValue.perLevel(0.1F),
                                        AttributeModifier.Operation.ADD_VALUE
                                )
                        )
        );
    }

    private static void register(BootstrapContext<Enchantment> pContext, ResourceKey<Enchantment> pKey, Enchantment.Builder pBuilder) {
        conditions.put(pKey, List.of(new ModLoadedCondition(ApothicEnchanting.MODID)));
        pContext.register(pKey, pBuilder.build(pKey.location()));
    }

    private static ResourceKey<Enchantment> key(String pName) {
        return ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(UltimateEnchantment.MODID, pName));
    }
}
