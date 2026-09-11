package com.chen1335.ultimateEnchantment.enchantment.enchantments;

import com.chen1335.ultimateEnchantment.common.Formula;
import com.chen1335.ultimateEnchantment.enchantment.EnchantmentBasic;
import com.chen1335.ultimateEnchantment.tags.UEEnchantmentTags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;
import java.util.Map;

public class Tear extends EnchantmentBasic {
    public static final Formula DAMAGE_ADD = new Formula("0.2");
    public static final Formula HEALTH_DAMAGE = new Formula("0.01");
    public static final Formula HIT_COUNT = new Formula("lvl+1");
    public static final Formula DURATION = new Formula("60");

    public Tear() {
        super("tear");
        supported_items = new Type.TagType<>(ItemTags.SHARP_WEAPON_ENCHANTABLE);
        exclusive_set = new Type.TagType<>(UEEnchantmentTags.ULTIMATE_ENCHANTMENT_EXCLUSIVE);
        max_cost = new Enchantment.Cost(150, 0);
        min_cost = new Enchantment.Cost(80, 0);
        slots = List.of();
        max_level = 5;
        weight = 3;
    }

    @Override
    protected void registerFormula(Map<String, Formula> formulas) {
        formulas.put("damage_add", DAMAGE_ADD);
        formulas.put("health_damage", HEALTH_DAMAGE);
        formulas.put("hit_count", HIT_COUNT);
        formulas.put("duration", DURATION);
    }

    @Override
    public MutableComponent getDesc(int level) {
        return Component.translatable(getDescId(), level + 1,
                DAMAGE_ADD.toComponent(buildBindings(level), 100),
                HEALTH_DAMAGE.toComponent(buildBindings(level), 100),
                DURATION.toComponent(buildBindings(level), 0.05F)).withStyle(ChatFormatting.LIGHT_PURPLE);
    }
}
