package com.chen1335.ultimateEnchantment.enchantment.effectComponents.UltimateEnchantment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record VanquisherComponent(int buffDuration) {
    public static final Codec<VanquisherComponent> CODEC = RecordCodecBuilder.create((inst) -> inst.group(
            Codec.INT.fieldOf("buffDuration").forGetter(VanquisherComponent::buffDuration)
    ).apply(inst, VanquisherComponent::new));
}
