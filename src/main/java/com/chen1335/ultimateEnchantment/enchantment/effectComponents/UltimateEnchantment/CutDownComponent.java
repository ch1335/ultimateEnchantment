package com.chen1335.ultimateEnchantment.enchantment.effectComponents.UltimateEnchantment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record CutDownComponent(float damageMultiplierPerLevel, float maxDamageMultiplierPerLevel) {
    public static Codec<CutDownComponent> CODEC = RecordCodecBuilder.create((inst) -> inst.group(
            Codec.FLOAT.fieldOf("damageMultiplierPerLevel").forGetter(CutDownComponent::damageMultiplierPerLevel),
            Codec.FLOAT.fieldOf("maxDamageMultiplierPerLevel").forGetter(CutDownComponent::maxDamageMultiplierPerLevel)
    ).apply(inst, CutDownComponent::new));
}
