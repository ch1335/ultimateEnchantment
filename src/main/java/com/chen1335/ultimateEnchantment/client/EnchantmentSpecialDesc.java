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
import java.util.List;
import java.util.Map;

public class EnchantmentSpecialDesc {

    /** 逐行加到说明上的样式。 */
    private static final Style DESC_STYLE = Style.EMPTY.withColor(ChatFormatting.DARK_GRAY);

    /**
     * 取该附魔的说明，一行一个组件，按显示顺序排。没有对应说明时返回空列表。
     * <p>
     * 调用方逐行 accept 即可，不要试图把多行拼成一个组件 —— 那是这个 API 存在的意义。
     */
    public static List<MutableComponent> getNewDescription(Holder<Enchantment> holder, int level) {
        @Nullable ResourceKey<Enchantment> resourceKey = holder.getKey();
        if (resourceKey == null) {
            return List.of();
        }

        for (Map.Entry<ResourceKey<Enchantment>, EnchantmentBasic> entry : UEEnchantments.MAP.entrySet()) {
            if (entry.getKey().equals(resourceKey)) {
                // 收成新列表而不是就地改：mergeStyles 内部走的是 setStyle，两种写法结果一样，
                // 但不依赖那个实现细节更稳，而且 getDesc 返回的列表不保证可变。
                return entry.getValue().getDesc(level).stream()
                        .map(line -> ComponentUtils.mergeStyles(line, DESC_STYLE))
                        .toList();
            }
        }

        return List.of();
    }


    private static String format(float f) {
        return format(f, 1);
    }

    private static String format(float value, int i) {
        return new BigDecimal(String.format("%." + i + "f", value)).stripTrailingZeros().toPlainString();
    }
}
