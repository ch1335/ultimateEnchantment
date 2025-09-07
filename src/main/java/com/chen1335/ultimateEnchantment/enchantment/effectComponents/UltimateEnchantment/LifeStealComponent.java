package com.chen1335.ultimateEnchantment.enchantment.effectComponents.UltimateEnchantment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record LifeStealComponent(float healPercentPerLevel, float maxPercent) {
    public static Codec<LifeStealComponent> CODEC = RecordCodecBuilder.create((inst) -> inst.group(
            Codec.FLOAT.fieldOf("healPercentPerLevel").forGetter(LifeStealComponent::healPercentPerLevel),
            Codec.FLOAT.fieldOf("maxPercent").forGetter(LifeStealComponent::maxPercent)
    ).apply(inst, LifeStealComponent::new));
}
