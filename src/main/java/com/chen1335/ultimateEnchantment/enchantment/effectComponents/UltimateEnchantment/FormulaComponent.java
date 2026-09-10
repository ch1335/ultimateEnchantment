package com.chen1335.ultimateEnchantment.enchantment.effectComponents.UltimateEnchantment;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;

import javax.script.*;
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

    public static class Formula {
        private final String formula;
        private final CompiledScript compile;
        public static final Codec<Formula> CODEC = Codec.STRING.flatXmap(
                formula -> {
                    try {
                        return DataResult.success(new Formula(formula));
                    } catch (ScriptException exception) {
                        return DataResult.error(() -> "Invalid formula expression: " + formula + " - " + exception.getMessage());
                    }
                },
                formula -> DataResult.success(formula.getFormula())
        );

        public Formula(String formula) throws ScriptException {
            this.formula = formula;
            Compilable compilable = (Compilable) UltimateEnchantment.SCRIPT_ENGINE;
            CompiledScript compiled;
            try {
                // 表达式风格：直接支持 a+b 这类不带 return 的公式
                compiled = compilable.compile("(function(){\nreturn (\n" + formula + "\n);\n})()");
            } catch (ScriptException expressionFailure) {
                // 语句风格：支持 var a = 1; return a+b 这类带 return 的写法
                compiled = compilable.compile("(function(){\n" + formula + "\n})()");
            }
            this.compile = compiled;
        }

        public String getFormula() {
            return formula;
        }

        public double calculate(Bindings bindings) throws ScriptException {
            Object result = compile.eval(bindings);
            if (result == null) {
                throw new ScriptException("公式没有返回值(缺少 return 且不是表达式): " + formula);
            }
            return ((Number) result).doubleValue();
        }
    }
}
