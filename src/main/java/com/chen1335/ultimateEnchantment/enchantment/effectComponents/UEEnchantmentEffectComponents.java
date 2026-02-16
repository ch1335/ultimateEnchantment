package com.chen1335.ultimateEnchantment.enchantment.effectComponents;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.enchantment.effectComponents.UltimateEnchantment.*;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Unit;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class UEEnchantmentEffectComponents {
    public static DeferredRegister<DataComponentType<?>> TYPES = DeferredRegister.create(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, UltimateEnchantment.MODID);

    public static DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentEntityEffect>>>> LAST_STAND = TYPES.register("last_stand", () -> DataComponentType.<List<ConditionalEffect<EnchantmentEntityEffect>>>builder().persistent(ConditionalEffect.codec(EnchantmentEntityEffect.CODEC, LootContextParamSets.ENCHANTED_ITEM).listOf()).build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<VanquisherComponent>> VANQUISHER = TYPES.register("vanquisher", () -> DataComponentType.<VanquisherComponent>builder().persistent(VanquisherComponent.CODEC).build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<Float>> SIMPLE_PER_LEVEL = TYPES.register("simple_per_level", () -> DataComponentType.<Float>builder().persistent(Codec.FLOAT).build());


    public static DeferredHolder<DataComponentType<?>, DataComponentType<EnchantmentValueEffect>> SIMPLE_VALUE = TYPES.register("simple_value", () -> DataComponentType.<EnchantmentValueEffect>builder().persistent(EnchantmentValueEffect.CODEC).build());


    public static DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> QUICK_LATCH = TYPES.register("quick_latch", () -> DataComponentType.<Unit>builder().persistent(Unit.CODEC).build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<LevelBasedValue>> ULTIMATE = TYPES.register("ultimate", () -> DataComponentType.<LevelBasedValue>builder().persistent(LevelBasedValue.CODEC).build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<LegendComponent>> LEGEND = TYPES.register("legend", () -> DataComponentType.<LegendComponent>builder().persistent(LegendComponent.CODEC).build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> SMELTING = TYPES.register("smelting", () -> DataComponentType.<Unit>builder().persistent(Unit.CODEC).build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<LifeStealComponent>> LIFE_STEAL = TYPES.register("life_steal", () -> DataComponentType.<LifeStealComponent>builder().persistent(LifeStealComponent.CODEC).build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<ManaStealComponent>> MANA_STEAL = TYPES.register("mana_steal", () -> DataComponentType.<ManaStealComponent>builder().persistent(ManaStealComponent.CODEC).build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<CutDownComponent>> CUT_DOWN = TYPES.register("cut_down", () -> DataComponentType.<CutDownComponent>builder().persistent(CutDownComponent.CODEC).build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<LethalTempoComponent>> LETHAL_TEMPO = TYPES.register("lethal_tempo", () -> DataComponentType.<LethalTempoComponent>builder().persistent(LethalTempoComponent.CODEC).build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<ThunderBoltComponent>> THUNDER_BOLT = TYPES.register("thunder_bolt", () -> DataComponentType.<ThunderBoltComponent>builder().persistent(ThunderBoltComponent.CODEC).build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<TearComponent>> TEAR = TYPES.register("tear", () -> DataComponentType.<TearComponent>builder().persistent(TearComponent.CODEC).build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<KineticEnergyComponent>> KINETIC_ENERGY = TYPES.register("kinetic_energy", () -> DataComponentType.<KineticEnergyComponent>builder().persistent(KineticEnergyComponent.CODEC).build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<DoubleHookComponent>> DOUBLE_HOOK = TYPES.register("double_hook", () -> DataComponentType.<DoubleHookComponent>builder().persistent(DoubleHookComponent.CODEC).build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<QuickBaitComponent>> QUICK_BAIT = TYPES.register("quick_bait", () -> DataComponentType.<QuickBaitComponent>builder().persistent(QuickBaitComponent.CODEC).build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<HardenedManaComponent>> HARDENED_MANA = TYPES.register("hardened_mana", () -> DataComponentType.<HardenedManaComponent>builder().persistent(HardenedManaComponent.CODEC).build());

}
