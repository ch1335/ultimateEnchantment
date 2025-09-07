package com.chen1335.ultimateEnchantment.enchantment.effectComponents.UltimateEnchantment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record QuickBaitComponent(float speedPerLevel) {
    public static Codec<QuickBaitComponent> CODEC = RecordCodecBuilder.create((inst) -> inst.group(
            Codec.FLOAT.fieldOf("speedPerLevel").forGetter(QuickBaitComponent::speedPerLevel)
    ).apply(inst, QuickBaitComponent::new));
}
