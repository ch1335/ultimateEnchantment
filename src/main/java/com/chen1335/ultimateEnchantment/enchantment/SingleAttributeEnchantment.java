package com.chen1335.ultimateEnchantment.enchantment;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.shadowsoffire.placebo.config.Configuration;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;

public class SingleAttributeEnchantment extends CommonEnchantmentBase implements IAttributeEnchantment {

    private int attributeModifierHolderCount = 0;

    public List<AttributeModifierHolder> attributeModifierHolders = new ArrayList<>();

    public final EnumMap<EquipmentSlot, UUID> modifierUuidPerSlot;

    public SingleAttributeEnchantment(Rarity pRarity, EnchantmentCategory pCategory, EquipmentSlot[] pApplicableSlots, UltimateEnchantment.EnchantmentType enchantmentType, Attribute attribute, float bonusPerLevel, AttributeModifier.Operation operation) {
        super(pRarity, pCategory, pApplicableSlots, enchantmentType);
        this.modifierUuidPerSlot = Util.make(new EnumMap<>(EquipmentSlot.class), (enumMap) -> {
            for (EquipmentSlot pApplicableSlot : EquipmentSlot.values()) {
                enumMap.put(pApplicableSlot, Mth.createInsecureUUID(RandomSource.createNewThreadLocalInstance()));
            }
        });
        this.addAttributeModifierHolder(new AttributeModifierHolder(attribute, bonusPerLevel, operation));
    }
    

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifier(EquipmentSlot slot, int level) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        this.attributeModifierHolders.forEach(attributeModifierHolder -> {
            UUID uuid = modifierUuidPerSlot.get(slot);
            if (uuid == null) {
                throw new Error("EquipmentSlot not found :" + slot.getName());
            }
            builder.put(attributeModifierHolder.attribute, new AttributeModifier(uuid, "", attributeModifierHolder.bonusPerLevel * level, attributeModifierHolder.operation));
        });
        return builder.build();
    }

    public void addAttributeModifierHolder(AttributeModifierHolder attributeModifierHolder) {
        this.attributeModifierHolders.add(attributeModifierHolder);
    }

    @Override
    public void loadConfig(Configuration config) {
        super.loadConfig(config);

        for (int i = 0; i < this.attributeModifierHolders.size(); i++) {
            attributeModifierHolders.get(i).loadFromConfig(i, this, config);
        }
    }

    public static class AttributeModifierHolder {


        public float bonusPerLevel;

        public AttributeModifier.Operation operation;

        public Attribute attribute;

        public AttributeModifierHolder(Attribute attribute, float bonusPerLevel, AttributeModifier.Operation operation) {
            this.attribute = attribute;
            this.bonusPerLevel = bonusPerLevel;
            this.operation = operation;
        }

        public void loadFromConfig(int id, SingleAttributeEnchantment enchantment, Configuration config) {
            ResourceLocation key = ForgeRegistries.ATTRIBUTES.getKey(attribute);
            if (key != null) {
                attribute = ForgeRegistries.ATTRIBUTES.getValue(ResourceLocation.tryParse(config.getString("attribute", enchantment.getSimpleName() + ".AttributeModifierHolders." + id, key.toString(), "the attribute")));
                bonusPerLevel = config.getFloat("bonusPerLevel", enchantment.getSimpleName() + ".AttributeModifierHolders." + id, bonusPerLevel, -100, 100, "the bonusPerLevel");
                operation = AttributeModifier.Operation.valueOf(config.getString("operation", enchantment.getSimpleName() + ".AttributeModifierHolders." + id, operation.toString(), "the operation:ADDITION,MULTIPLY_BASE,MULTIPLY_TOTAL"));
            }
        }
    }
}
