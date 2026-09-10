package com.chen1335.ultimateEnchantment.client;

import com.chen1335.ultimateEnchantment.enchantment.EnchantmentBasic;
import com.chen1335.ultimateEnchantment.enchantment.UEEnchantments;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class EnchantmentSpecialDesc {
    private static final Map<ResourceKey<Enchantment>, BiFunction<Holder<Enchantment>, Integer, MutableComponent>> DESC = new HashMap<>();

    public static MutableComponent getNewDescription(Holder<Enchantment> holder, int level) {
        @Nullable ResourceKey<Enchantment> resourceKey = holder.getKey();
        if (resourceKey == null) {
            return null;
        }

        for (Map.Entry<ResourceKey<Enchantment>, EnchantmentBasic> entry : UEEnchantments.MAP.entrySet()) {
            if (entry.getKey().equals(resourceKey)) {
                MutableComponent component = entry.getValue().getDesc(level);
                ComponentUtils.mergeStyles(component, Style.EMPTY.withColor(ChatFormatting.DARK_GRAY));
                return component;
            }
        }


        return null;
    }


    private static String format(float f) {
        return format(f, 1);
    }

    private static String format(float value, int i) {
        return new BigDecimal(String.format("%." + i + "f", value)).stripTrailingZeros().toPlainString();
    }
}
