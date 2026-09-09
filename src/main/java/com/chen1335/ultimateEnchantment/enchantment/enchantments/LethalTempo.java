package com.chen1335.ultimateEnchantment.enchantment.enchantments;

import com.chen1335.ultimateEnchantment.common.Arg;
import com.chen1335.ultimateEnchantment.enchantment.EnchantmentBasic;

public class LethalTempo extends EnchantmentBasic {
    public final Arg damageMul = new Arg();
    public final Arg chancePerHit = new Arg();
    public final Arg maxChance = new Arg();
    public LethalTempo(){
        super("lethal_tempo");
        registerArg("damageMul",damageMul);
        registerArg("chancePerHit",chancePerHit);
        registerArg("maxChance",maxChance);
    }

}
