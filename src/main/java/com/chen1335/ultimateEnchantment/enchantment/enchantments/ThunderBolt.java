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

public class ThunderBolt extends EnchantmentBasic {
    public static final Formula MAIN_DAMAGE = new Formula("0.1*lvl");
    public static final Formula OTHER_DAMAGE = new Formula("0.25*lvl");
    public static final Formula RANGE = new Formula("2");
    public static final Formula HEIGHT = new Formula("4");
    public static final Formula HIT_INTERVAL = new Formula("3");

    public ThunderBolt() {
        super("thunder_bolt");
        supported_items = new Type.TagType<>(ItemTags.SHARP_WEAPON_ENCHANTABLE);
        max_cost = new Enchantment.Cost(30, 0);
        min_cost = new Enchantment.Cost(25, 0);
        slots = List.of();
        max_level = 3;
        weight = 3;
    }

    @Override
    protected void registerFormula(Map<String, Formula> formulas) {
        formulas.put("main_damage", MAIN_DAMAGE);
        formulas.put("other_damage", OTHER_DAMAGE);
        formulas.put("range", RANGE);
        formulas.put("height", HEIGHT);
        formulas.put("hit_interval", HIT_INTERVAL);
    }

    @Override
    public List<MutableComponent> getDesc(int level) {
        return List.of(Component.translatable(getDescId(),
                MAIN_DAMAGE.toComponent(buildBindings(level), 100),
                OTHER_DAMAGE.toComponent(buildBindings(level), 100),
                RANGE.toComponent(buildBindings(level), 1)).withStyle(ChatFormatting.GOLD));
    }
}
