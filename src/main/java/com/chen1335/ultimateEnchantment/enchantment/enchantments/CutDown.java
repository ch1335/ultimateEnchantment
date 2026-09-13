package com.chen1335.ultimateEnchantment.enchantment.enchantments;

import com.chen1335.ultimateEnchantment.common.Formula;
import com.chen1335.ultimateEnchantment.enchantment.EnchantmentBasic;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;

import javax.script.SimpleBindings;
import java.util.List;
import java.util.Map;

public class CutDown extends EnchantmentBasic {
    public static final Formula DAMAGE_MUL = new Formula("0.0002 * lvl");
    public static final Formula MAX_DAMAGE_MUL = new Formula("0.1 * lvl");

    public CutDown() {
        super("cut_down");
        supported_items = new Type.TagType<>(ItemTags.SHARP_WEAPON_ENCHANTABLE);
        max_cost = new Enchantment.Cost(60, 10);
        min_cost = new Enchantment.Cost(20, 10);
        slots = List.of(EquipmentSlotGroup.HAND);
        max_level = 5;
        weight = 3;
    }

    @Override
    protected void registerFormula(Map<String, Formula> formulas) {
        formulas.put("damage_mul", DAMAGE_MUL);
        formulas.put("max_damage_mul", MAX_DAMAGE_MUL);
    }

    @Override
    public List<MutableComponent> getDesc(int level) {
        SimpleBindings simpleBindings = buildBindings(level);
        return List.of(Component.translatable(getDescId(),
                DAMAGE_MUL.toComponent(simpleBindings, 100, 2),
                MAX_DAMAGE_MUL.toComponent(simpleBindings, 100)).withStyle(ChatFormatting.GOLD));
    }
}
