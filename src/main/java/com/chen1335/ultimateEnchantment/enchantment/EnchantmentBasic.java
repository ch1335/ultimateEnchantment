package com.chen1335.ultimateEnchantment.enchantment;

import com.chen1335.ultimateEnchantment.common.Arg;

import java.util.HashMap;
import java.util.Map;

public class EnchantmentBasic {
    public final Map<String, Arg> args = new HashMap<>();
    private final String name;
    public EnchantmentBasic(String name){
        this.name = name;
    }
    protected void registerArg(String name, Arg arg) {
    }
}
