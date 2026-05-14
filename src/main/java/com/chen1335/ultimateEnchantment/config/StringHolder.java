package com.chen1335.ultimateEnchantment.config;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;

import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.util.function.Consumer;

public class StringHolder {
    private String current;
    private final String original;

    public StringHolder(String string) {
        this.original = string;
        current = original;
    }

    public String getCurrent() {
        return current;
    }

    public String getOriginal() {
        return original;
    }

    public void rust() {
        current = original;
    }

    public boolean changed() {
        return !current.equals(original);
    }

    public void set(String string) {
        current = string;
    }

    public float getValue(int lvl) {
        SimpleBindings simpleBindings = new SimpleBindings();
        simpleBindings.put("lvl", lvl);
        try {
            return (float) UltimateEnchantment.SCRIPT_ENGINE.eval(current, simpleBindings);
        } catch (ScriptException e) {
            UltimateEnchantment.LOGGER.error(String.valueOf(e));
        }
        return 0;
    }

    public void tryAndRun(int lvl, Consumer<Double> consumer) {
        SimpleBindings simpleBindings = new SimpleBindings();
        simpleBindings.put("lvl", lvl);
        try {
            consumer.accept((Double) UltimateEnchantment.SCRIPT_ENGINE.eval(current, simpleBindings));
        } catch (ScriptException e) {
            UltimateEnchantment.LOGGER.error(String.valueOf(e));
        }
    }
}
