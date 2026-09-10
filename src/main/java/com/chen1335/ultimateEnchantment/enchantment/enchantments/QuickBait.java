package com.chen1335.ultimateEnchantment.enchantment.enchantments;

import com.chen1335.ultimateEnchantment.common.Formula;
import com.chen1335.ultimateEnchantment.enchantment.EnchantmentBasic;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;
import java.util.Map;

public class QuickBait extends EnchantmentBasic {
    public static final Formula SPEED = new Formula("0.05*lvl");

    public QuickBait() {
        super("quick_bait");
        supported_items = new Type.TagType<>(ItemTags.FISHING_ENCHANTABLE);
        max_cost = new Enchantment.Cost(60, 10);
        min_cost = new Enchantment.Cost(20, 5);
        slots = List.of();
        max_level = 5;
        weight = 3;
    }

    @Override
    protected void registerFormula(Map<String, Formula> formulas) {
        formulas.put("speed", SPEED);
    }

    @Override
    public MutableComponent getDesc(int level) {
        return Component.translatable(getDescId(), SPEED.toComponent(buildBindings(level), 100));
    }
}
