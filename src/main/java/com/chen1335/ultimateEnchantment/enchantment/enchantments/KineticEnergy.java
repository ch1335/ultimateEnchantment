package com.chen1335.ultimateEnchantment.enchantment.enchantments;

import com.chen1335.ultimateEnchantment.common.Formula;
import com.chen1335.ultimateEnchantment.enchantment.EnchantmentBasic;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;
import java.util.Map;

public class KineticEnergy extends EnchantmentBasic {
    public static final Formula INCREMENT = new Formula("0.01");
    public static final Formula MAX_SPEED = new Formula("0.1*lvl");
    public static final Formula KEEP_TIME = new Formula("200");

    public KineticEnergy() {
        super("kinetic_energy");
        supported_items = new Type.TagType<>(ItemTags.MINING_ENCHANTABLE);
        max_cost = new Enchantment.Cost(60, 10);
        min_cost = new Enchantment.Cost(20, 10);
        slots = List.of();
        max_level = 5;
        weight = 3;
    }

    @Override
    protected void registerFormula(Map<String, Formula> formulas) {
        formulas.put("increment", INCREMENT);
        formulas.put("max_speed", MAX_SPEED);
        formulas.put("keep_time", KEEP_TIME);
    }

    @Override
    public List<MutableComponent> getDesc(int level) {
        return List.of(Component.translatable(getDescId(),
                INCREMENT.toComponent(buildBindings(level), 100, 1),
                MAX_SPEED.toComponent(buildBindings(level), 100),
                KEEP_TIME.toComponent(buildBindings(level), 0.05F, 1)).withStyle(ChatFormatting.GOLD));
    }
}
