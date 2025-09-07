package com.chen1335.ultimateEnchantment.enchantment.effectComponents.UltimateEnchantment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record DoubleHookComponent(float chancePerLevel) {
    public static Codec<DoubleHookComponent> CODEC = RecordCodecBuilder.create((inst) -> inst.group(
            Codec.FLOAT.fieldOf("chancePerLevel").forGetter(DoubleHookComponent::chancePerLevel)
    ).apply(inst, DoubleHookComponent::new));
}
