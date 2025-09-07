package com.chen1335.ultimateEnchantment.enchantment.effectComponents.UltimateEnchantment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record TearComponent(float damageAdd, float totalHealthDamage) {
    public static Codec<TearComponent> CODEC = RecordCodecBuilder.create((inst) -> inst.group(
            Codec.FLOAT.fieldOf("damageAdd").forGetter(TearComponent::damageAdd),
            Codec.FLOAT.fieldOf("totalHealthDamage").forGetter(TearComponent::totalHealthDamage)
    ).apply(inst, TearComponent::new));
}
