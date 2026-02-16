package com.chen1335.ultimateEnchantment.common.conditions;

import com.chen1335.ultimateEnchantment.config.EnchantmentEnableInfo;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.conditions.ICondition;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public record EnchantmentEnableCondition(ResourceLocation id) implements ICondition {
    public static MapCodec<EnchantmentEnableCondition> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder
                    .group(
                            ResourceLocation.CODEC.fieldOf("id").forGetter(EnchantmentEnableCondition::id))
                    .apply(builder, EnchantmentEnableCondition::new));

    @Override
    public boolean test(IContext context) {
        return EnchantmentEnableInfo.enableInfo.getOrDefault(id, true);
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
