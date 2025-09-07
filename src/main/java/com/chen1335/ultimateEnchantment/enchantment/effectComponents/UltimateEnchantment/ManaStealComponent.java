package com.chen1335.ultimateEnchantment.enchantment.effectComponents.UltimateEnchantment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ManaStealComponent(float ManaRegainPercentPerLevel, float maxPercent) {
    public static Codec<ManaStealComponent> CODEC = RecordCodecBuilder.create((inst) -> inst.group(
            Codec.FLOAT.fieldOf("ManaRegainPercentPerLevel").forGetter(ManaStealComponent::ManaRegainPercentPerLevel),
            Codec.FLOAT.fieldOf("maxPercent").forGetter(ManaStealComponent::maxPercent)
    ).apply(inst, ManaStealComponent::new));
}
