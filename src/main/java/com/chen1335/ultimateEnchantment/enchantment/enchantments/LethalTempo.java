package com.chen1335.ultimateEnchantment.enchantment.enchantments;

import com.chen1335.ultimateEnchantment.common.Formula;
import com.chen1335.ultimateEnchantment.enchantment.EnchantmentBasic;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;


public class LethalTempo extends EnchantmentBasic {
    public final Formula damageMul = new Formula("0.2");
    public final Formula chancePerHit = new Formula("0.2");
    public final Formula maxChance = new Formula("0.4*lvl");

    public LethalTempo() {
        super("lethal_tempo");
        registerArg("damageMul", damageMul);
        registerArg("chancePerHit", chancePerHit);
        registerArg("maxChance", maxChance);
        supported_items = new Type.TagType<>(ItemTags.BOW_ENCHANTABLE);
        exclusive_set = new Type.TagType<>(EnchantmentTags.ARMOR_EXCLUSIVE);

    }

}
