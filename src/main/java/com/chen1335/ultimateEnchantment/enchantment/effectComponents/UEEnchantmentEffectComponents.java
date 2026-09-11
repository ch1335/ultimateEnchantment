package com.chen1335.ultimateEnchantment.enchantment.effectComponents;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class UEEnchantmentEffectComponents {
    public static DeferredRegister<DataComponentType<?>> TYPES = DeferredRegister.create(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, UltimateEnchantment.MODID);

    public static DeferredHolder<DataComponentType<?>, DataComponentType<FormulaComponent>> FORMULA = TYPES.register("formula", () -> DataComponentType.<FormulaComponent>builder().persistent(FormulaComponent.CODEC).build());

}
