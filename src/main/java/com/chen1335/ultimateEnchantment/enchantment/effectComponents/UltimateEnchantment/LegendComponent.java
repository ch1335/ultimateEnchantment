package com.chen1335.ultimateEnchantment.enchantment.effectComponents.UltimateEnchantment;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;

public record LegendComponent(float attributeMultiplePerLevel) {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(UltimateEnchantment.MODID, "legend");

    public static ResourceLocation idForSlot(StringRepresentable pSlot) {
        return ID.withSuffix("/" + pSlot.getSerializedName());
    }

    public static Codec<LegendComponent> CODEC = RecordCodecBuilder.create((inst) -> inst.group(
            Codec.FLOAT.fieldOf("attributeMultiplePerLevel").forGetter(LegendComponent::attributeMultiplePerLevel)
    ).apply(inst, LegendComponent::new));
}
