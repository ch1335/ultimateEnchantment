package com.chen1335.ultimateEnchantment.enchantment.effectComponents.UltimateEnchantment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record LethalTempoComponent(float additionHitDamage,float addChanceOnHit, float maxChancePerLevel,int keepTime) {
    public static Codec<LethalTempoComponent> CODEC = RecordCodecBuilder.create((inst) -> inst.group(
            Codec.FLOAT.fieldOf("additionHitDamage").forGetter(LethalTempoComponent::additionHitDamage),
            Codec.FLOAT.fieldOf("addChanceOnHit").forGetter(LethalTempoComponent::addChanceOnHit),
            Codec.FLOAT.fieldOf("maxChancePerLevel").forGetter(LethalTempoComponent::maxChancePerLevel),
            Codec.INT.fieldOf("keepTime").forGetter(LethalTempoComponent::keepTime)

    ).apply(inst, LethalTempoComponent::new));
}
