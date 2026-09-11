package com.chen1335.ultimateEnchantment.enchantment.enchantments;

import com.chen1335.ultimateEnchantment.common.Formula;
import com.chen1335.ultimateEnchantment.enchantment.EnchantmentBasic;
import com.chen1335.ultimateEnchantment.tags.UEEnchantmentTags;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;
import java.util.Map;

public class ManaSteal extends EnchantmentBasic {
    public static final Formula MANA_PERCENT = new Formula("(6 + 2 * (lvl-1))*0.01");
    public static final Formula MAX_PERCENT = new Formula("Math.min((5+lvl),10)*0.01");

    public ManaSteal() {
        super("mana_steal", "irons_spellbooks");
        supported_items = new Type.TagType<>(ItemTags.SHARP_WEAPON_ENCHANTABLE);
        exclusive_set = new Type.TagType<>(UEEnchantmentTags.LIFE_STEAL_ENCHANTMENT);
        max_cost = new Enchantment.Cost(60, 10);
        min_cost = new Enchantment.Cost(15, 10);
        slots = List.of();
        max_level = 4;
        weight = 3;
    }

    @Override
    protected void registerFormula(Map<String, Formula> formulas) {
        formulas.put("mana_percent", MANA_PERCENT);
        formulas.put("max_percent", MAX_PERCENT);
    }

    @Override
    public List<MutableComponent> getDesc(int level) {
        return List.of(Component.translatable(getDescId(), MANA_PERCENT.toComponent(buildBindings(level), 100),MAX_PERCENT.toComponent(buildBindings(level), 100)));
    }
}
