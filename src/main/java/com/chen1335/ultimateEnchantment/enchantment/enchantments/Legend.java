package com.chen1335.ultimateEnchantment.enchantment.enchantments;

import com.chen1335.arcanumlib.modules.attribute.ArcanumAttributes;
import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.common.Formula;
import com.chen1335.ultimateEnchantment.enchantment.EnchantmentBasic;
import com.chen1335.ultimateEnchantment.tags.UEEnchantmentTags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class Legend extends EnchantmentBasic {
    public static final Formula ATTRIBUTE_BONUS = new Formula("0.01*lvl");

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(UltimateEnchantment.MODID, "legend");


    public Legend() {
        super("legend");
        Type<Item> enchantables = new Type.TagType<>(Tags.Items.ENCHANTABLES);
        supported_items = enchantables;
        primary_items = enchantables;
        exclusive_set = new Type.TagType<>(UEEnchantmentTags.ULTIMATE_ENCHANTMENT_EXCLUSIVE);
        max_cost = new Enchantment.Cost(200, 0);
        min_cost = new Enchantment.Cost(200, 0);
        slots = List.of(EquipmentSlotGroup.ANY);
        max_level = 5;
        weight = 1;
    }


    public static ResourceLocation idForSlot(StringRepresentable pSlot) {
        return ID.withSuffix("/" + pSlot.getSerializedName());
    }

    @Override
    protected void registerFormula(Map<String, Formula> formulas) {
        formulas.put("attribute_bonus", ATTRIBUTE_BONUS);
    }

    @Override
    public void addModifier(ItemAttributeModifierEvent event, int lvl, @Nullable EquipmentSlot equipmentSlot) {
        EquipmentSlot slot = equipmentSlot == null ? EquipmentSlot.MAINHAND : equipmentSlot;
        event.addModifier(
                ArcanumAttributes.ALL_ATTRIBUTE_MULTIPLIER,
                new AttributeModifier(idForSlot(slot), ATTRIBUTE_BONUS.calculate(buildBindings(lvl)), AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.bySlot(slot));
    }

    @Override
    public List<MutableComponent> getDesc(int level, TooltipFlag tooltipFlag) {
        AttributeModifier modifier = new AttributeModifier(ID, ATTRIBUTE_BONUS.calculate(buildBindings(level)), AttributeModifier.Operation.ADD_VALUE);
        return List.of(Component.translatable(getDescId(),
                ArcanumAttributes.ALL_ATTRIBUTE_MULTIPLIER.value().toComponent(modifier, tooltipFlag).withStyle(ChatFormatting.LIGHT_PURPLE)).withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}
