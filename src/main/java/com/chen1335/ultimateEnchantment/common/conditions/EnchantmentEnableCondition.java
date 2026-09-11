package com.chen1335.ultimateEnchantment.common.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.neoforged.neoforge.common.conditions.ICondition;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public record EnchantmentEnableCondition(boolean enable) implements ICondition {
    public static MapCodec<EnchantmentEnableCondition> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder
                    .group(
                            Codec.BOOL.fieldOf("id").forGetter(EnchantmentEnableCondition::enable))
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
