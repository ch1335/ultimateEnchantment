package com.chen1335.ultimateEnchantment.enchantment.enchantments;

import com.chen1335.arcanumlib.modules.attribute.ArcanumAttributes;
import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.common.Formula;
import com.chen1335.ultimateEnchantment.enchantment.EnchantmentBasic;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class Trail extends EnchantmentBasic {
    public static final Formula SEEKING_BONUS = new Formula("0.1*lvl");
    public static final ResourceLocation MODIFIER_ID = UltimateEnchantment.id("trail");

    public Trail() {
        super("trail");
        supported_items = new Type.TagType<>(ItemTags.HEAD_ARMOR_ENCHANTABLE);
        primary_items = supported_items;
        max_cost = new Enchantment.Cost(60, 10);
        min_cost = new Enchantment.Cost(20, 10);
        slots = List.of(EquipmentSlotGroup.HEAD);
        max_level = 3;
        weight = 2;
    }

    @Override
    protected void registerFormula(Map<String, Formula> formulas) {
        formulas.put("seeking_bonus", SEEKING_BONUS);
    }

    @Override
    public void addModifier(ItemAttributeModifierEvent event, int lvl, @Nullable EquipmentSlot equipmentSlot) {
        event.addModifier(ArcanumAttributes.SEEKING,
                new AttributeModifier(MODIFIER_ID, SEEKING_BONUS.calculate(buildBindings(lvl)), AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.HEAD);
    }

    @Override
    public List<MutableComponent> getDesc(int level, TooltipFlag tooltipFlag) {
        AttributeModifier modifier = new AttributeModifier(MODIFIER_ID, SEEKING_BONUS.calculate(buildBindings(level)), AttributeModifier.Operation.ADD_VALUE);
        return List.of(Component.translatable(getDescId(),
                ArcanumAttributes.SEEKING.value().toComponent(modifier, tooltipFlag).withStyle(ChatFormatting.GOLD))
                .withStyle(ChatFormatting.GOLD));
    }
}
