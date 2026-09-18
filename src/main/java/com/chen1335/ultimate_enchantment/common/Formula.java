package com.chen1335.ultimate_enchantment.common;

import com.chen1335.ultimate_enchantment.UltimateEnchantment;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.chat.Component;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptException;
import java.math.BigDecimal;
import java.util.Locale;

public class Formula {
    private String formula;
    private CompiledScript compile;
    private String formulaDefault;
    private CompiledScript compileDefault;
    public static final Codec<Formula> CODEC = Codec.STRING.flatXmap(
            formula -> {
                try {
                    return DataResult.success(new Formula(formula));
                } catch (RuntimeException exception) {
                    return DataResult.error(() -> "Invalid formula expression: " + formula + " - " + exception.getMessage());
                }
            },
            formula -> DataResult.success(formula.getFormula())
    );

    /**
     * 构造时编译并留存一份"默认公式"作为回滚基准。
     * <p>
     * 默认公式是代码里写死的字符串，编不过属于开发期错误，直接抛出去让 {@link #CODEC} 转成
     * {@code DataResult.error}，不在这里降级 —— 兜底的那一份自己都不能坏。
     */
    public Formula(String formula) {
        try {
            compileDefault = compileScript(formula);
        } catch (ScriptException exception) {
            throw new RuntimeException(exception);
        }
        formulaDefault = formula;
        this.formula = formula;
        compile = compileDefault;
    }

    /**
     * 用外部（数据包或配置文件）给的公式重新编译。
     * <p>
     * 编不过就把 {@link #formula} 与 {@link #compile} 一起还原成构造期留存的那一份 —— 它一定编得过，
     * 所以本对象任何时刻都处在可用状态，坏公式不会被带进后续计算。
     * <p>
     * 这里故意不向调用方抛 {@link ScriptException}：唯一的调用点在 {@code ServerStartedEvent} 里，
     * 抛出去等于开档即崩，而降级成默认公式只是数值不同。
     */
    public void compile(String formula) {
        CompiledScript compiled;
        try {
            compiled = compileScript(formula);
        } catch (ScriptException exception) {
            UltimateEnchantment.LOGGER.error(
                    "Failed to compile enchantment formula {}, reverting to default {}",
                    formula,
                    formulaDefault,
                    exception
            );
            this.formula = formulaDefault;
            this.compile = compileDefault;
            return;
        }
        this.formula = formula;
        this.compile = compiled;
    }

    private static CompiledScript compileScript(String formula) throws ScriptException {
        Compilable compilable = (Compilable) UltimateEnchantment.SCRIPT_ENGINE;
        try {
            // 表达式风格：直接支持 a+b 这类不带 return 的公式
            return compilable.compile("(function(){\nreturn (\n" + formula + "\n);\n})()");
        } catch (ScriptException expressionFailure) {
            // 语句风格：支持 var a = 1; return a+b 这类带 return 的写法
            return compilable.compile("(function(){\n" + formula + "\n})()");
        }
    }

    public String getFormula() {
        return formula;
    }

    /**
     * 求值。出错时不向调用方抛异常：调用点全是伤害结算、属性计算和 tooltip 渲染这类热路径，
     * 抛出去就是崩游戏。出错时先把公式回滚到默认的那份，再用默认公式重算一次。
     */
    public float calculate(Bindings bindings) {
        try {
            return eval(compile, formula, bindings);
        } catch (RuntimeException exception) {
            if (compile != compileDefault) {
                UltimateEnchantment.LOGGER.error(
                        "Failed to evaluate enchantment formula {}, reverting to default {}",
                        formula,
                        formulaDefault,
                        exception
                );
                this.formula = formulaDefault;
                this.compile = compileDefault;
                try {
                    return eval(compileDefault, formulaDefault, bindings);
                } catch (RuntimeException fallbackFailure) {
                    UltimateEnchantment.LOGGER.error(
                            "Default enchantment formula {} also failed to evaluate, returning 0",
                            formulaDefault,
                            fallbackFailure
                    );
                    return 0.0F;
                }
            }
            // 走到这里说明默认公式自己就算不出来，通常是调用方没给全 bindings。
            // 这个分支不记日志：调用点每 tick、每次伤害都会经过，记了就是刷屏。
            return 0.0F;
        }
    }

    private static float eval(CompiledScript script, String formulaText, Bindings bindings) {
        Object result;
        try {
            result = script.eval(bindings);
        } catch (ScriptException exception) {
            throw new RuntimeException(exception);
        }
        if (result == null) {
            throw new RuntimeException("Formula returned no value (no return statement and not a single expression): " + formulaText);
        }
        if (!(result instanceof Number number)) {
            throw new RuntimeException("Formula returned a non-numeric value: " + formulaText + " -> " + result);
        }
        float value = number.floatValue();
        // 1/0、0/0、Math.log(-1) 这类语法合法但结果为 Infinity/NaN 的公式，按计算错误处理走回滚：
        // 而若放任不管，NaN 会顺着伤害和属性一路传下去（血量变成 NaN 的实体此后杀不死）。
        if (!Float.isFinite(value)) {
            throw new RuntimeException("Formula produced a non-finite value: " + formulaText + " -> " + value);
        }
        return value;
    }

    public Component toComponent(Bindings bindings, float scale) {
        return toComponent(bindings, scale, 1);
    }

    public Component toComponent(Bindings bindings, float scale, int i) {
        float v = calculate(bindings) * scale;
        return Component.literal(format(v, i));
    }

    private static String format(float f) {
        return format(f, 1);
    }

    private static String format(float value, int i) {
        // 这里也要兜一道：上面 format 的是 calculate() * scale，有限的结果乘上大的 scale 照样会溢出成 Infinity。
        if (!Float.isFinite(value)) {
            return "0";
        }
        // Locale.ROOT 不能省：String.format 默认跟随 JVM 的区域设置，法语/德语等地区的小数点是逗号，
        // 会输出 "1,5"，而 BigDecimal 只认 "1.5"，于是抛 NumberFormatException ——
        // 这条路径就是 tooltip 渲染，一抛就是客户端崩溃。
        return new BigDecimal(String.format(Locale.ROOT, "%." + i + "f", value)).stripTrailingZeros().toPlainString();
    }
}
