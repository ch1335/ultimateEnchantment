package com.chen1335.ultimateEnchantment.enchantment.effectComponents.UltimateEnchantment;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.nbt.CompoundTag;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptException;
import java.util.Map;

public record FormulaComponent(Map<String, Formula> formulas) {
    public static Codec<FormulaComponent> CODEC =


    public static class Formula {
        private final String formula;
        private final CompiledScript compile;
        public static final Codec<Formula> CODEC = Codec.STRING.xmap(Formula::new, Formula::getFormula);

        public Formula(String formula) throws ScriptException {
            this.formula = formula;
            compile = ((Compilable) UltimateEnchantment.SCRIPT_ENGINE).compile(formula);
        }

        public String getFormula() {
            return formula;
        }

        public double calculate(Bindings bindings) throws ScriptException {
            return (double) compile.eval(bindings);
        }
    }
}
