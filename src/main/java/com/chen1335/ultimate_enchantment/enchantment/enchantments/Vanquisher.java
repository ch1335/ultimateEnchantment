package com.chen1335.ultimate_enchantment.enchantment.enchantments;

import com.chen1335.ultimate_enchantment.common.Formula;
import com.chen1335.ultimate_enchantment.enchantment.EnchantmentBasic;
import com.chen1335.ultimate_enchantment.tags.UEEnchantmentTags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;

import javax.script.SimpleBindings;
import java.util.List;
import java.util.Map;

public class Vanquisher extends EnchantmentBasic {
    public static final Formula BUFF_DURATION = new Formula("200");
    public static final Formula MAX_STACKS = new Formula("10");
    public static final Formula DAMAGE_PER_STACK = new Formula("0.05");
    public static final Formula SPEED_PER_STACK = new Formula("0.1");
    public static final Formula COOL_DOWN = new Formula("20");
    public static final Formula LIFE_STEAL = new Formula("0.04");

    public Vanquisher() {
        super("vanquisher");
        supported_items = new Type.TagType<>(ItemTags.SHARP_WEAPON_ENCHANTABLE);
        primary_items = new Type.TagType<>(ItemTags.MELEE_WEAPON_ENCHANTABLE);
        exclusive_set = new Type.TagType<>(UEEnchantmentTags.ULTIMATE_ENCHANTMENT_EXCLUSIVE);
        max_cost = new Enchantment.Cost(200, 0);
        min_cost = new Enchantment.Cost(200, 0);
        slots = List.of(EquipmentSlotGroup.HAND);
        max_level = 1;
        weight = 1;
    }

    @Override
    protected void registerFormula(Map<String, Formula> formulas) {
        formulas.put("buff_duration", BUFF_DURATION);
        formulas.put("max_stacks", MAX_STACKS);
        formulas.put("damage_per_stack", DAMAGE_PER_STACK);
        formulas.put("speed_per_stack", SPEED_PER_STACK);
        formulas.put("cool_down", COOL_DOWN);
    }

    @Override
    public List<MutableComponent> getDesc(int level) {
        SimpleBindings bindings = new SimpleBindings();
        return List.of(Component.translatable(getDescId(),
                COOL_DOWN.toComponent(bindings, 0.05F),
                MAX_STACKS.toComponent(bindings, 1),
                DAMAGE_PER_STACK.toComponent(bindings, 100),
                SPEED_PER_STACK.toComponent(bindings, 100),
                LIFE_STEAL.toComponent(bindings, 100)
        ).withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}
