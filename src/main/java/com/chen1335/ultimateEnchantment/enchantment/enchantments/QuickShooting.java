package com.chen1335.ultimateEnchantment.enchantment.enchantments;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.common.Formula;
import com.chen1335.ultimateEnchantment.enchantment.EnchantmentBasic;
import dev.shadowsoffire.apothic_attributes.api.ALObjects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

import javax.script.SimpleBindings;
import java.util.List;
import java.util.Map;

public class QuickShooting extends EnchantmentBasic {
    public static final Formula DRAW_SPEED = new Formula("0.05*lvl");
    public static final ResourceLocation MODIFIER_ID = UltimateEnchantment.id("quick_shooting");

    public QuickShooting() {
        super("quick_shooting", "apothic_enchanting");
        supported_items = new Type.TagType<>(ItemTags.BOW_ENCHANTABLE);
        max_cost = new Enchantment.Cost(90, 10);
        min_cost = new Enchantment.Cost(20, 10);
        slots = List.of(EquipmentSlotGroup.HAND);
        max_level = 5;
        weight = 2;
    }

    @Override
    protected void registerFormula(Map<String, Formula> formulas) {
        formulas.put("draw_speed", DRAW_SPEED);
    }

    @Override
    public void addModifier(ItemAttributeModifierEvent event, int lvl, EquipmentSlot equipmentSlot) {
        SimpleBindings simpleBindings = buildBindings(lvl);
        event.addModifier(ALObjects.Attributes.DRAW_SPEED, new AttributeModifier(MODIFIER_ID, DRAW_SPEED.calculate(simpleBindings), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
    }

    @Override
    public List<MutableComponent> getDesc(int level, TooltipFlag tooltipFlag) {
        // 与 addModifier 用同一个 modifier 值与 operation，交给属性自己渲染成
        // 「+25% 拉弓速度」这种完整写法，不再手拼百分比。
        AttributeModifier modifier = new AttributeModifier(MODIFIER_ID, DRAW_SPEED.calculate(buildBindings(level)), AttributeModifier.Operation.ADD_VALUE);
        return List.of(Component.translatable(getDescId(),
                ALObjects.Attributes.DRAW_SPEED.value().toComponent(modifier, tooltipFlag).withStyle(ChatFormatting.GOLD)).withStyle(ChatFormatting.GOLD));
    }
}
