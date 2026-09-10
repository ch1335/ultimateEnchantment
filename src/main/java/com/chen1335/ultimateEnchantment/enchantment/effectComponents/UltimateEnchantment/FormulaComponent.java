package com.chen1335.ultimateEnchantment.enchantment.effectComponents.UltimateEnchantment;

import com.chen1335.ultimateEnchantment.common.Formula;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;

import java.util.Map;

/**
 * 公式组件：持有多个命名公式（键为公式名，值为编译后的公式）。
 * <p>
 * 与 {@link CompoundTag} 互相转换时只使用 StringTag：
 * CompoundTag 的键为公式名，值为公式字符串。
 * </p>
 */
public record FormulaComponent(Map<String, Formula> formulas) {

    /**
     * 与 CompoundTag 互相转换的 Codec，键为公式名，值为 StringTag 公式字符串
     */
    public static Codec<FormulaComponent> CODEC =
            Codec.unboundedMap(Codec.STRING, Formula.CODEC)
                    .xmap(FormulaComponent::new, FormulaComponent::formulas);

    /**
     * 转换为 CompoundTag
     */
    public CompoundTag toTag() {
        return (CompoundTag) CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow();
    }

    /**
     * 从 CompoundTag 转换
     */
    public static FormulaComponent fromTag(CompoundTag tag) {
        return CODEC.parse(NbtOps.INSTANCE, tag).getOrThrow(IllegalStateException::new);
    }

}
