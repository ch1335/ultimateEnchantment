package com.chen1335.ultimateEnchantment.enchantment.enchantments;

import com.chen1335.ultimateEnchantment.common.Formula;
import com.chen1335.ultimateEnchantment.enchantment.EnchantmentBasic;
import com.chen1335.ultimateEnchantment.tags.UEEnchantmentTags;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.Tags;

import java.util.List;
import java.util.Map;

public class Legend extends EnchantmentBasic {
    public static final Formula ATTRIBUTE_BONUS = new Formula("0.01*lvl");

    public Legend() {
        super("legend");
        Type<Item> enchantables = new Type.TagType<>(Tags.Items.ENCHANTABLES);
        supported_items = enchantables;
        primary_items = enchantables;
        exclusive_set = new Type.TagType<>(UEEnchantmentTags.ULTIMATE_ENCHANTMENT_EXCLUSIVE);
        max_cost = new Enchantment.Cost(999, 0);
        min_cost = new Enchantment.Cost(999, 0);
        slots = List.of(EquipmentSlotGroup.ANY);
        max_level = 5;
        weight = 1;
    }

    @Override
    protected void registerFormula(Map<String, Formula> formulas) {
        formulas.put("attribute_bonus", ATTRIBUTE_BONUS);
    }

    @Override
    public MutableComponent getDesc(int level) {
        return Component.translatable(getDescId(), ATTRIBUTE_BONUS.toComponent(buildBindings(level), 100));
    }
}
