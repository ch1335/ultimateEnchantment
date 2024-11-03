package com.chen1335.ultimateEnchantment.enchantment;

import com.chen.simpleRPGCore.attribute.SRCAttributes;
import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.enchantment.effectComponents.UltimateEnchantment.LegendComponent;
import com.chen1335.ultimateEnchantment.enchantment.effectComponents.UltimateEnchantment.SyphonComponent;
import com.chen1335.ultimateEnchantment.enchantment.effectComponents.UltimateEnchantment.UEEnchantmentEffectComponents;
import com.chen1335.ultimateEnchantment.enchantment.effects.UltimateEnchantment.LastStandEffect;
import com.chen1335.ultimateEnchantment.tags.UEEnchantmentTags;
import com.chen1335.ultimateEnchantment.tags.UEItemTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UEEnchantments {
    public static final Map<ResourceKey<?>, List<ICondition>> conditions = new HashMap<>();
    public static final ResourceKey<Enchantment> LAST_STAND = key("last_stand");
    public static final ResourceKey<Enchantment> OVER_GROW = key("over_grow");
    public static final ResourceKey<Enchantment> QUICK_LATCH = key("quick_latch");
    public static final ResourceKey<Enchantment> SYPHON = key("syphon");
    public static final ResourceKey<Enchantment> LIFE_STEAL = key("life_steal");
    public static final ResourceKey<Enchantment> ULTIMATE = key("ultimate");
    public static final ResourceKey<Enchantment> LEGEND = key("legend");
    public static final ResourceKey<Enchantment> SMELTING = key("smelting");
    public static final ResourceKey<Enchantment> CRITICAL_CHANCE = key("critical_chance");
    public static final ResourceKey<Enchantment> CRITICAL_DAMAGE = key("critical_damage");
    public static final ResourceKey<Enchantment> PIERCE_THROUGH = key("pierce_through");
    public static final ResourceKey<Enchantment> CUT_DOWN = key("cut_down");


    public static void bootstrap(BootstrapContext<Enchantment> pContext) {
        HolderGetter<Enchantment> enchantmentHolderGetter = pContext.lookup(Registries.ENCHANTMENT);
        HolderGetter<Item> itemHolderGetter = pContext.lookup(Registries.ITEM);

        register(
                pContext,
                CUT_DOWN,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        itemHolderGetter.getOrThrow(ItemTags.WEAPON_ENCHANTABLE),
                                        3,
                                        5,
                                        Enchantment.dynamicCost(30, 10),
                                        Enchantment.dynamicCost(80, 10),
                                        1,
                                        EquipmentSlotGroup.HAND
                                )
                        )
                        .withSpecialEffect(
                                UEEnchantmentEffectComponents.CUT_DOWN.value(),
                                LevelBasedValue.perLevel(0.0001F)
                        )
        );
        register(
                pContext,
                PIERCE_THROUGH,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        itemHolderGetter.getOrThrow(UEItemTags.WEAPON_TOOLS),
                                        3,
                                        5,
                                        Enchantment.dynamicCost(30, 10),
                                        Enchantment.dynamicCost(80, 10),
                                        1,
                                        EquipmentSlotGroup.HAND
                                )
                        )
                        .withEffect(
                                EnchantmentEffectComponents.ATTRIBUTES,
                                new EnchantmentAttributeEffect(
                                        ResourceLocation.fromNamespaceAndPath(UltimateEnchantment.MODID, "enchantment" + CRITICAL_DAMAGE.location().getPath()),
                                        SRCAttributes.ARMOR_PENETRATION,
                                        LevelBasedValue.perLevel(2F),
                                        AttributeModifier.Operation.ADD_VALUE
                                )
                        )
        );
        register(
                pContext,
                CRITICAL_DAMAGE,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        itemHolderGetter.getOrThrow(UEItemTags.WEAPON_TOOLS),
                                        4,
                                        5,
                                        Enchantment.dynamicCost(15, 10),
                                        Enchantment.dynamicCost(80, 10),
                                        1,
                                        EquipmentSlotGroup.HAND
                                )
                        )
                        .withEffect(
                                EnchantmentEffectComponents.ATTRIBUTES,
                                new EnchantmentAttributeEffect(
                                        ResourceLocation.fromNamespaceAndPath(UltimateEnchantment.MODID, "enchantment" + CRITICAL_DAMAGE.location().getPath()),
                                        SRCAttributes.CRITICAL_DAMAGE,
                                        LevelBasedValue.perLevel(0.1F),
                                        AttributeModifier.Operation.ADD_VALUE
                                )
                        )
        );
        register(
                pContext,
                CRITICAL_CHANCE,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        itemHolderGetter.getOrThrow(UEItemTags.WEAPON_TOOLS),
                                        4,
                                        4,
                                        Enchantment.dynamicCost(20, 10),
                                        Enchantment.dynamicCost(80, 10),
                                        1,
                                        EquipmentSlotGroup.HAND
                                )
                        )
                        .withEffect(
                                EnchantmentEffectComponents.ATTRIBUTES,
                                new EnchantmentAttributeEffect(
                                        ResourceLocation.fromNamespaceAndPath(UltimateEnchantment.MODID, "enchantment" + CRITICAL_CHANCE.location().getPath()),
                                        SRCAttributes.CRITICAL_CHANCE,
                                        LevelBasedValue.perLevel(0.05F),
                                        AttributeModifier.Operation.ADD_VALUE
                                )
                        )
        );
        register(
                pContext,
                SMELTING,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        itemHolderGetter.getOrThrow(ItemTags.MINING_ENCHANTABLE),
                                        3,
                                        1,
                                        Enchantment.constantCost(15),
                                        Enchantment.constantCost(80),
                                        1,
                                        EquipmentSlotGroup.ANY
                                )
                        )
                        .withSpecialEffect(UEEnchantmentEffectComponents.SMELTING.value(),
                                Unit.INSTANCE
                        )

        );
        register(
                pContext,
                LEGEND,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        itemHolderGetter.getOrThrow(Tags.Items.ENCHANTABLES),
                                        1,
                                        5,
                                        Enchantment.constantCost(999),
                                        Enchantment.constantCost(999),
                                        1,
                                        EquipmentSlotGroup.ANY
                                )
                        )
                        .exclusiveWith(enchantmentHolderGetter.getOrThrow(UEEnchantmentTags.ULTIMATE_ENCHANTMENT))
                        .withSpecialEffect(UEEnchantmentEffectComponents.LEGEND.value(),
                                new LegendComponent(0.02F)
                        )

        );
        register(
                pContext,
                LAST_STAND,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        itemHolderGetter.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                                        1,
                                        5,
                                        Enchantment.constantCost(999),
                                        Enchantment.constantCost(999),
                                        1,
                                        EquipmentSlotGroup.ARMOR
                                )
                        )
                        .exclusiveWith(enchantmentHolderGetter.getOrThrow(UEEnchantmentTags.ULTIMATE_ENCHANTMENT))
                        .withEffect(UEEnchantmentEffectComponents.LAST_STAND.value(),
                                new LastStandEffect(
                                        Attributes.ARMOR,
                                        LevelBasedValue.constant(0.334F),
                                        LevelBasedValue.perLevel(0.05F)
                                )
                        )
                        .withEffect(UEEnchantmentEffectComponents.LAST_STAND.value(),
                                new LastStandEffect(
                                        Attributes.ARMOR_TOUGHNESS,
                                        LevelBasedValue.constant(0.334F),
                                        LevelBasedValue.perLevel(0.05F)
                                )
                        )

        );
        register(
                pContext,
                OVER_GROW,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        itemHolderGetter.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                                        2,
                                        5,
                                        Enchantment.dynamicCost(30, 5),
                                        Enchantment.dynamicCost(80, 10),
                                        1,
                                        EquipmentSlotGroup.ARMOR
                                )
                        )
                        .withEffect(
                                EnchantmentEffectComponents.ATTRIBUTES,
                                new EnchantmentAttributeEffect(
                                        ResourceLocation.fromNamespaceAndPath(UltimateEnchantment.MODID, "enchantment" + OVER_GROW.location().getPath()),
                                        Attributes.MAX_HEALTH,
                                        LevelBasedValue.perLevel(0.02F),
                                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                                )
                        )

        );
        register(
                pContext,
                QUICK_LATCH,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        itemHolderGetter.getOrThrow(ItemTags.BOW_ENCHANTABLE),
                                        3,
                                        1,
                                        Enchantment.constantCost(20),
                                        Enchantment.constantCost(60),
                                        1,
                                        EquipmentSlotGroup.HAND
                                )
                        )
                        .withSpecialEffect(UEEnchantmentEffectComponents.QUICK_LATCH.value(), Unit.INSTANCE)
        );
        register(
                pContext,
                SYPHON,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        itemHolderGetter.getOrThrow(ItemTags.WEAPON_ENCHANTABLE),
                                        itemHolderGetter.getOrThrow(ItemTags.SWORD_ENCHANTABLE),
                                        1,
                                        5,
                                        Enchantment.dynamicCost(20, 9),
                                        Enchantment.dynamicCost(65, 9),
                                        1,
                                        EquipmentSlotGroup.HAND
                                )
                        )
                        .exclusiveWith(enchantmentHolderGetter.getOrThrow(UEEnchantmentTags.LIFE_STEAL_ENCHANTMENT))
                        .withSpecialEffect(UEEnchantmentEffectComponents.SYPHON.value(), new SyphonComponent(10, 0.001F))
        );
        register(
                pContext,
                LIFE_STEAL,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        itemHolderGetter.getOrThrow(ItemTags.WEAPON_ENCHANTABLE),
                                        itemHolderGetter.getOrThrow(ItemTags.SWORD_ENCHANTABLE),
                                        4,
                                        4,
                                        Enchantment.dynamicCost(15, 9),
                                        Enchantment.dynamicCost(65, 9),
                                        1,
                                        EquipmentSlotGroup.HAND
                                )
                        )
                        .exclusiveWith(enchantmentHolderGetter.getOrThrow(UEEnchantmentTags.LIFE_STEAL_ENCHANTMENT))
                        .withEffect(
                                EnchantmentEffectComponents.ATTRIBUTES,
                                new EnchantmentAttributeEffect(
                                        ResourceLocation.fromNamespaceAndPath(UltimateEnchantment.MODID, "enchantment" + LIFE_STEAL.location().getPath()),
                                        SRCAttributes.LIFE_STEAL,
                                        LevelBasedValue.perLevel(0.025F),
                                        AttributeModifier.Operation.ADD_VALUE
                                )
                        )
        );

        register(
                pContext,
                ULTIMATE,
                Enchantment.enchantment(
                                Enchantment.definition(
                                        itemHolderGetter.getOrThrow(Tags.Items.ENCHANTABLES),
                                        itemHolderGetter.getOrThrow(Tags.Items.ENCHANTABLES),
                                        2,
                                        5,
                                        Enchantment.constantCost(999),
                                        Enchantment.constantCost(999),
                                        1,
                                        EquipmentSlotGroup.ANY
                                )
                        )
                        .exclusiveWith(enchantmentHolderGetter.getOrThrow(UEEnchantmentTags.ULTIMATE_ENCHANTMENT))
                        .withSpecialEffect(UEEnchantmentEffectComponents.ULTIMATE.value(), Unit.INSTANCE)
        );
    }

    private static void register(BootstrapContext<Enchantment> pContext, ResourceKey<Enchantment> pKey, Enchantment.Builder pBuilder) {
        conditions.put(pKey, List.of(new ModLoadedCondition(UltimateEnchantment.MODID)));
        pContext.register(pKey, pBuilder.build(pKey.location()));
    }

    private static ResourceKey<Enchantment> key(String pName) {
        return ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(UltimateEnchantment.MODID, pName));
    }
}
