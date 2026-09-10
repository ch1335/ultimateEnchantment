package com.chen1335.ultimateEnchantment.enchantment.enchantments;

import com.chen1335.ultimateEnchantment.common.Formula;
import com.chen1335.ultimateEnchantment.enchantment.EnchantmentBasic;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;
import java.util.Map;

public class HardenedMana extends EnchantmentBasic {
    public static final Formula MANA_PERCENT = new Formula("0.01");
    public static final Formula MAX_ARMOR = new Formula("lvl");
    public static final Formula KEEP_TIME = new Formula("200");

    public HardenedMana() {
        super("hardened_mana", "irons_spellbooks");
        supported_items = new Type.TagType<>(ItemTags.CHEST_ARMOR_ENCHANTABLE);
        max_cost = new Enchantment.Cost(50, 10);
        min_cost = new Enchantment.Cost(15, 8);
        slots = List.of();
        max_level = 4;
        weight = 3;
    }

    @Override
    protected void registerFormula(Map<String, Formula> formulas) {
        formulas.put("mana_percent", MANA_PERCENT);
        formulas.put("max_armor", MAX_ARMOR);
        formulas.put("keep_time", KEEP_TIME);
    }

    @Override
    public MutableComponent getDesc(int level) {
        return Component.translatable(getDescId(),
                MANA_PERCENT.toComponent(buildBindings(level), 100),
                MAX_ARMOR.toComponent(buildBindings(level), 1));
    }
}
