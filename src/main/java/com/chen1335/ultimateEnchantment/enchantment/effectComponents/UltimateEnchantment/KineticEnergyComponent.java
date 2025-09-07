package com.chen1335.ultimateEnchantment.enchantment.effectComponents.UltimateEnchantment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record KineticEnergyComponent(float breakSpeedMultiplierPerBlock, float maxSpeedPerLevel) {
    public static Codec<KineticEnergyComponent> CODEC = RecordCodecBuilder.create((inst) -> inst.group(
            Codec.FLOAT.fieldOf("breakSpeedMultiplierPerBlock").forGetter(KineticEnergyComponent::breakSpeedMultiplierPerBlock),
            Codec.FLOAT.fieldOf("maxSpeedPerLevel").forGetter(KineticEnergyComponent::maxSpeedPerLevel)
    ).apply(inst, KineticEnergyComponent::new));
}
