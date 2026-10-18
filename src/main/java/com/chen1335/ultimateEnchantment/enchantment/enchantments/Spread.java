package com.chen1335.ultimateEnchantment.enchantment.enchantments;

import com.chen1335.ultimateEnchantment.common.Formula;
import com.chen1335.ultimateEnchantment.enchantment.EnchantmentBasic;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;
import java.util.Map;

public class Spread extends EnchantmentBasic {
    public static final Formula CHAIN_COUNT = new Formula("2*lvl");

    public Spread() {
        super("spread");
        supported_items = new Type.TagType<>(ItemTags.MINING_ENCHANTABLE);
        max_cost = new Enchantment.Cost(60, 10);
        min_cost = new Enchantment.Cost(15, 10);
        slots = List.of(EquipmentSlotGroup.MAINHAND);
        max_level = 3;
        weight = 3;
    }

    @Override
    protected void registerFormula(Map<String, Formula> formulas) {
        formulas.put("chain_count", CHAIN_COUNT);
    }

    @Override
    public List<MutableComponent> getDesc(int level, TooltipFlag tooltipFlag) {
        return List.of(Component.translatable(getDescId(),
                CHAIN_COUNT.toComponent(buildBindings(level), 1, 0)));
    }
}
