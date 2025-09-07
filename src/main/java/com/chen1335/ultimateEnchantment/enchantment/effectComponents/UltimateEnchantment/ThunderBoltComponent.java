package com.chen1335.ultimateEnchantment.enchantment.effectComponents.UltimateEnchantment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ThunderBoltComponent(float mainTargetDamage, float otherTargetDamage, double range) {
    public static Codec<ThunderBoltComponent> CODEC = RecordCodecBuilder.create((inst) -> inst.group(
            Codec.FLOAT.fieldOf("mainTargetDamage").forGetter(ThunderBoltComponent::mainTargetDamage),
            Codec.FLOAT.fieldOf("otherTargetDamage").forGetter(ThunderBoltComponent::otherTargetDamage),
            Codec.DOUBLE.fieldOf("range").forGetter(ThunderBoltComponent::range)

    ).apply(inst, ThunderBoltComponent::new));
}
