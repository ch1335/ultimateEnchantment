package com.chen1335.ultimate_enchantment.common.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.neoforge.common.conditions.ICondition;

public record EnchantmentEnableCondition(boolean enable) implements ICondition {
    public static MapCodec<EnchantmentEnableCondition> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder
                    .group(
                            Codec.BOOL.fieldOf("enable").forGetter(EnchantmentEnableCondition::enable))
                    .apply(builder, EnchantmentEnableCondition::new));

    @Override
    public boolean test(IContext context) {
        return enable;
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
