package com.chen1335.ultimateEnchantment.enchantment.enchantments;

import com.chen1335.ultimateEnchantment.common.AttributeModifierId;
import com.chen1335.ultimateEnchantment.common.Formula;
import com.chen1335.ultimateEnchantment.dataComponentType.UEDataComponentTypes;
import com.chen1335.ultimateEnchantment.enchantment.EnchantmentBasic;
import com.chen1335.ultimateEnchantment.tags.UEEnchantmentTags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import org.jetbrains.annotations.Nullable;

import javax.script.SimpleBindings;
import java.util.List;
import java.util.Map;

public class LastStand extends EnchantmentBasic {
    public static final Formula HEALTH_THRESHOLD = new Formula("0.4");
    public static final Formula ARMOR_BONUS = new Formula("0.05*lvl");

    public LastStand() {
        super("last_stand");
        supported_items = new Type.TagType<>(ItemTags.ARMOR_ENCHANTABLE);
        exclusive_set = new Type.TagType<>(UEEnchantmentTags.ULTIMATE_ENCHANTMENT_EXCLUSIVE);
        max_cost = new Enchantment.Cost(200, 0);
        min_cost = new Enchantment.Cost(200, 0);
        slots = List.of(EquipmentSlotGroup.ARMOR);
        max_level = 5;
        weight = 1;
    }

    @Override
    protected void registerFormula(Map<String, Formula> formulas) {
        formulas.put("health_threshold", HEALTH_THRESHOLD);
        formulas.put("armor_bonus", ARMOR_BONUS);
    }

    @Override
    public List<MutableComponent> getDesc(int level, TooltipFlag tooltipFlag) {
        SimpleBindings bindings = buildBindings(level);
        AttributeModifier armor = new AttributeModifier(AttributeModifierId.LAST_STAND_ARMOR,
                ARMOR_BONUS.calculate(bindings), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        AttributeModifier toughness = new AttributeModifier(AttributeModifierId.LAST_STAND_ARMOR_TOUGHNESS,
                ARMOR_BONUS.calculate(bindings), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        return List.of(Component.translatable(getDescId(),
                                HEALTH_THRESHOLD.toComponent(bindings, 100),
                                Attributes.ARMOR.value().toComponent(armor, tooltipFlag).withStyle(ChatFormatting.LIGHT_PURPLE),
                                Attributes.ARMOR_TOUGHNESS.value().toComponent(toughness, tooltipFlag).withStyle(ChatFormatting.LIGHT_PURPLE)
                        ).withStyle(ChatFormatting.LIGHT_PURPLE)
        );
    }

    @Override
    public void addModifier(ItemAttributeModifierEvent event, int lvl, @Nullable EquipmentSlot equipmentSlot) {
        if (equipmentSlot == null) {
            return;
        }
        ItemStack itemStack = event.getItemStack();
        SimpleBindings simpleBindings = LastStand.buildBindings(lvl);
        if (itemStack.getOrDefault(UEDataComponentTypes.USER_HEALTH, 0).floatValue() <= itemStack.getOrDefault(UEDataComponentTypes.USER_MAX_HEALTH, 0).floatValue() * LastStand.HEALTH_THRESHOLD.calculate(simpleBindings)) {
            AttributeModifier attributeModifier = new AttributeModifier(AttributeModifierId.LAST_STAND_ARMOR.withSuffix("/" + equipmentSlot.getSerializedName()), LastStand.ARMOR_BONUS.calculate(simpleBindings), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            AttributeModifier attributeModifier1 = new AttributeModifier(AttributeModifierId.LAST_STAND_ARMOR_TOUGHNESS.withSuffix("/" + equipmentSlot.getSerializedName()), LastStand.ARMOR_BONUS.calculate(simpleBindings), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            event.addModifier(Attributes.ARMOR, attributeModifier, EquipmentSlotGroup.bySlot(equipmentSlot));
            event.addModifier(Attributes.ARMOR_TOUGHNESS, attributeModifier1, EquipmentSlotGroup.bySlot(equipmentSlot));
        }
    }
}
