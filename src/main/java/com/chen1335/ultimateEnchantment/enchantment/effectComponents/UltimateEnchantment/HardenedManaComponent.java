package com.chen1335.ultimateEnchantment.enchantment.effectComponents.UltimateEnchantment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record HardenedManaComponent(float manaCostPercent, float maxArmorPerLevel) {
    public static Codec<HardenedManaComponent> CODEC = RecordCodecBuilder.create((inst) -> inst.group(
            Codec.FLOAT.fieldOf("manaCostPercent").forGetter(HardenedManaComponent::manaCostPercent),
            Codec.FLOAT.fieldOf("maxArmorPerLevel").forGetter(HardenedManaComponent::maxArmorPerLevel)
    ).apply(inst, HardenedManaComponent::new));
}
