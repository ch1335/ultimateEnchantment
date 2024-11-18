package com.chen1335.ultimateEnchantment.enchantment;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.EnumMap;
import java.util.UUID;

public class SingleAttributeEnchantment extends CommonEnchantmentBase implements IAttributeEnchantment {

    public final EnumMap<EquipmentSlot, UUID> modifierUuidPerSlot = Util.make(new EnumMap<>(EquipmentSlot.class), (enumMap) -> {
        enumMap.put(EquipmentSlot.FEET, Mth.createInsecureUUID(RandomSource.createNewThreadLocalInstance()));
        enumMap.put(EquipmentSlot.LEGS, Mth.createInsecureUUID(RandomSource.createNewThreadLocalInstance()));
        enumMap.put(EquipmentSlot.CHEST, Mth.createInsecureUUID(RandomSource.createNewThreadLocalInstance()));
        enumMap.put(EquipmentSlot.HEAD, Mth.createInsecureUUID(RandomSource.createNewThreadLocalInstance()));
        enumMap.put(EquipmentSlot.MAINHAND, Mth.createInsecureUUID(RandomSource.createNewThreadLocalInstance()));
        enumMap.put(EquipmentSlot.OFFHAND, Mth.createInsecureUUID(RandomSource.createNewThreadLocalInstance()));
    });

    public float bonusPerLevel;

    public AttributeModifier.Operation operation;

    public Attribute attribute;

    public SingleAttributeEnchantment(Rarity pRarity, EnchantmentCategory pCategory, EquipmentSlot[] pApplicableSlots, UltimateEnchantment.EnchantmentType enchantmentType, Attribute attribute, float bonusPerLevel, AttributeModifier.Operation operation) {
        super(pRarity, pCategory, pApplicableSlots, enchantmentType);
        this.bonusPerLevel = bonusPerLevel;
        this.operation = operation;
        this.attribute = attribute;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifier(EquipmentSlot slot, int level) {
        return ImmutableMultimap.of(
                attribute, new AttributeModifier(modifierUuidPerSlot.get(slot), "", bonusPerLevel * level, operation)
        );
    }
}
