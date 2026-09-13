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
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

import java.util.List;
import java.util.Map;

public class Scabbing extends EnchantmentBasic {
    public static final Formula ARMOR_SHRED = new Formula("1 - Math.pow(1-0.04,lvl)");
    public static final ResourceLocation MODIFIER_ID = UltimateEnchantment.id("scabbing");

    public Scabbing() {
        super("scabbing", "apothic_enchanting");
        supported_items = new Type.TagType<>(ItemTags.WEAPON_ENCHANTABLE);
        max_cost = new Enchantment.Cost(90, 10);
        min_cost = new Enchantment.Cost(25, 5);
        slots = List.of(EquipmentSlotGroup.HAND);
        max_level = 5;
        weight = 2;
    }

    @Override
    public void addModifier(ItemAttributeModifierEvent event, int lvl, EquipmentSlot equipmentSlot) {
        event.addModifier(ALObjects.Attributes.ARMOR_SHRED, new AttributeModifier(MODIFIER_ID, ARMOR_SHRED.calculate(buildBindings(lvl)), AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.MAINHAND);
    }

    @Override
    protected void registerFormula(Map<String, Formula> formulas) {
        formulas.put("armor_shred", ARMOR_SHRED);
    }

    @Override
    public List<MutableComponent> getDesc(int level) {
        return List.of(Component.translatable(getDescId(), ARMOR_SHRED.toComponent(buildBindings(level), 100,1)).withStyle(ChatFormatting.GOLD));
    }
}
