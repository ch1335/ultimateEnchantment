package com.chen1335.ultimateEnchantment.enchantment.enchantments;

import com.chen1335.arcanumlib.modules.attribute.ArcanumAttributes;
import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.common.Formula;
import com.chen1335.ultimateEnchantment.enchantment.EnchantmentBasic;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import org.jetbrains.annotations.Nullable;

import javax.script.SimpleBindings;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LuckyMind extends EnchantmentBasic {
    public static final Formula FORTUNE_BONUS = new Formula("0.25*lvl");
    public static final Formula LOOTING_BONUS = new Formula("0.25*lvl");
    public static final Formula LUCK_BONUS = new Formula("0.5*lvl");

    public static final ResourceLocation MODIFIER_ID = UltimateEnchantment.id("lucky_mind");

    public LuckyMind() {
        super("lucky_mind");
        supported_items = new Type.TagType<>(ItemTags.HEAD_ARMOR_ENCHANTABLE);
        primary_items = supported_items;
        max_cost = new Enchantment.Cost(60, 10);
        min_cost = new Enchantment.Cost(20, 10);
        slots = List.of(EquipmentSlotGroup.HEAD);
        max_level = 4;
        weight = 2;
    }

    @Override
    protected void registerFormula(Map<String, Formula> formulas) {
        formulas.put("fortune_bonus", FORTUNE_BONUS);
        formulas.put("looting_bonus", LOOTING_BONUS);
        formulas.put("luck_bonus", LUCK_BONUS);
    }

    @Override
    public void addModifier(ItemAttributeModifierEvent event, int lvl, @Nullable EquipmentSlot equipmentSlot) {
        SimpleBindings bindings = buildBindings(lvl);
        event.addModifier(ArcanumAttributes.FORTUNE,
                new AttributeModifier(MODIFIER_ID.withSuffix("/fortune"), FORTUNE_BONUS.calculate(bindings), AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.HEAD);
        event.addModifier(ArcanumAttributes.LOOTING,
                new AttributeModifier(MODIFIER_ID.withSuffix("/looting"), LOOTING_BONUS.calculate(bindings), AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.HEAD);
        event.addModifier(Attributes.LUCK,
                new AttributeModifier(MODIFIER_ID.withSuffix("/luck"), LUCK_BONUS.calculate(bindings), AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.HEAD);
    }

    @Override
    public List<MutableComponent> getDesc(int level, TooltipFlag tooltipFlag) {
        SimpleBindings bindings = buildBindings(level);
        List<MutableComponent> desc = new ArrayList<>(3);
        desc.add(ArcanumAttributes.FORTUNE.value().toComponent(new AttributeModifier(MODIFIER_ID.withSuffix("/fortune"),
                FORTUNE_BONUS.calculate(bindings), AttributeModifier.Operation.ADD_VALUE), tooltipFlag).withStyle(ChatFormatting.GOLD));
        desc.add(ArcanumAttributes.LOOTING.value().toComponent(new AttributeModifier(MODIFIER_ID.withSuffix("/looting"),
                LOOTING_BONUS.calculate(bindings), AttributeModifier.Operation.ADD_VALUE), tooltipFlag).withStyle(ChatFormatting.GOLD));
        desc.add(Attributes.LUCK.value().toComponent(new AttributeModifier(MODIFIER_ID.withSuffix("/luck"),
                LUCK_BONUS.calculate(bindings), AttributeModifier.Operation.ADD_VALUE), tooltipFlag).withStyle(ChatFormatting.GOLD));
        return desc;
    }
}
