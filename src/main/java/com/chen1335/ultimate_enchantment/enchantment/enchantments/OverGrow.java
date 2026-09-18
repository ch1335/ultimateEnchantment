package com.chen1335.ultimate_enchantment.enchantment.enchantments;

import com.chen1335.ultimate_enchantment.UltimateEnchantment;
import com.chen1335.ultimate_enchantment.common.Formula;
import com.chen1335.ultimate_enchantment.enchantment.EnchantmentBasic;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import org.jetbrains.annotations.Nullable;

import javax.script.SimpleBindings;
import java.util.List;
import java.util.Map;

public class OverGrow extends EnchantmentBasic {
    public static final Identifier MODIFIER_ID = UltimateEnchantment.id("over_grow");

    public static final Formula HEALTH_BONUS = new Formula("0.02*lvl");

    public OverGrow() {
        super("over_grow");
        supported_items = new Type.TagType<>(ItemTags.ARMOR_ENCHANTABLE);
        max_cost = new Enchantment.Cost(80, 10);
        min_cost = new Enchantment.Cost(25, 10);
        slots = List.of(EquipmentSlotGroup.ARMOR);
        max_level = 5;
        weight = 2;
    }

    public static Identifier makeId(String serializedName) {
        return MODIFIER_ID.withSuffix("/" + serializedName);
    }

    @Override
    protected void registerFormula(Map<String, Formula> formulas) {
        formulas.put("health_bonus", HEALTH_BONUS);
    }


    @Override
    public List<MutableComponent> getDesc(int level) {
        return List.of(Component.translatable(getDescId(), HEALTH_BONUS.toComponent(buildBindings(level), 100)).withStyle(ChatFormatting.GOLD));
    }

    @Override
    public void addModifier(ItemAttributeModifierEvent event, int lvl, @Nullable EquipmentSlot equipmentSlot) {
        if (equipmentSlot == null) {
            return;
        }
        SimpleBindings simpleBindings = buildBindings(lvl);
        event.addModifier(Attributes.MAX_HEALTH, new AttributeModifier(OverGrow.makeId(equipmentSlot.getSerializedName()), OverGrow.HEALTH_BONUS.calculate(simpleBindings), AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.bySlot(equipmentSlot));
    }
}
