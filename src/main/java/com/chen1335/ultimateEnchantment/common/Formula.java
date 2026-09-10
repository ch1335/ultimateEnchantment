package com.chen1335.ultimateEnchantment.common;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptException;

public class Formula {
    private String formula;
    private CompiledScript compile;
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

    public Formula(String formula) {
        this.formula = formula;
        try {
            compile(formula);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    public void compile(String formula) throws ScriptException {
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
