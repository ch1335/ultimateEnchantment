package com.chen1335.ultimateEnchantment.enchantment.enchantments;

import com.chen1335.ultimateEnchantment.common.Formula;
import com.chen1335.ultimateEnchantment.enchantment.EnchantmentBasic;
import com.chen1335.ultimateEnchantment.tags.UEEnchantmentTags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.Tags;

import java.util.List;
import java.util.Map;

public class Ultimate extends EnchantmentBasic {
    public static final Formula LEVEL_ADD = new Formula("1 * lvl");

    public Ultimate() {
        super("ultimate");
        TagKey<Item> enchantables = Tags.Items.ENCHANTABLES;
        supported_items = new Type.TagType<>(enchantables);
        primary_items = new Type.TagType<>(enchantables);
        exclusive_set = new Type.TagType<>(UEEnchantmentTags.ULTIMATE_ENCHANTMENT_EXCLUSIVE);
        max_cost = new Enchantment.Cost(999, 0);
        min_cost = new Enchantment.Cost(999, 0);
        slots = List.of(EquipmentSlotGroup.ANY);
        max_level = 5;
        weight = 2;
    }

    @Override
    protected void registerFormula(Map<String, Formula> formulas) {
        formulas.put("level_add", LEVEL_ADD);
    }

    @Override
    public List<MutableComponent> getDesc(int level) {
        return List.of(Component.translatable(getDescId(), Math.round(LEVEL_ADD.calculate(buildBindings(level)))).withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}
