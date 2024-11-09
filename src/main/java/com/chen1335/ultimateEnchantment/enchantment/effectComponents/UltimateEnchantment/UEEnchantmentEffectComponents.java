package com.chen1335.ultimateEnchantment.enchantment.effectComponents.UltimateEnchantment;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
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

    public static DeferredHolder<DataComponentType<?>, DataComponentType<SyphonComponent>> SYPHON = TYPES.register("syphon", () -> DataComponentType.<SyphonComponent>builder().persistent(SyphonComponent.CODEC).build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> QUICK_LATCH = TYPES.register("quick_latch", () -> DataComponentType.<Unit>builder().persistent(Unit.CODEC).build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> ULTIMATE = TYPES.register("ultimate", () -> DataComponentType.<Unit>builder().persistent(Unit.CODEC).build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<LegendComponent>> LEGEND = TYPES.register("legend", () -> DataComponentType.<LegendComponent>builder().persistent(LegendComponent.CODEC).build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> SMELTING = TYPES.register("smelting", () -> DataComponentType.<Unit>builder().persistent(Unit.CODEC).build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<LevelBasedValue>> CUT_DOWN = TYPES.register("cut_down", () -> DataComponentType.<LevelBasedValue>builder().persistent(LevelBasedValue.CODEC).build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> EXTRA_SHOOT_COUNT = TYPES.register("extra_shoot_count", () -> DataComponentType.<List<ConditionalEffect<EnchantmentValueEffect>>>builder().persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_ENTITY).listOf()).build());

}
