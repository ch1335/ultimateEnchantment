package com.chen1335.ultimateEnchantment.enchantment.effectComponents.UltimateEnchantment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record SyphonComponent(float maxSyphonCritDmgPerLevel, float healPerDmgPerLevel) {
    public static Codec<SyphonComponent> CODEC = RecordCodecBuilder.create((inst) -> inst.group(
            Codec.FLOAT.fieldOf("maxSyphonCritDmgPerLevel").forGetter(SyphonComponent::maxSyphonCritDmgPerLevel),
            Codec.FLOAT.fieldOf("healPerDmgPerLevel").forGetter(SyphonComponent::healPerDmgPerLevel)
            ).apply(inst, SyphonComponent::new));
}
