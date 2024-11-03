package com.chen1335.ultimateEnchantment.enchantment.effects.UltimateEnchantment;

import com.chen1335.ultimateEnchantment.common.AttributeModifierId;
import com.chen1335.ultimateEnchantment.dataComponentType.UEDataComponentTypes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record  LastStandEffect(Holder<Attribute> attribute, LevelBasedValue.Constant maxHealthPercentage , LevelBasedValue.Linear armorPercentagePerLevel) implements EnchantmentEntityEffect {


    public static final MapCodec<LastStandEffect> CODEC = RecordCodecBuilder.mapCodec(lastStandInstance ->
                lastStandInstance.group(
                        Attribute.CODEC.fieldOf("attribute").forGetter(LastStandEffect::attribute),
                        LevelBasedValue.Constant.CODEC.fieldOf("maxHealthPercentage").forGetter(LastStandEffect::maxHealthPercentage),
                        LevelBasedValue.Linear.CODEC.fieldOf("armorPercentagePerLevel").forGetter(LastStandEffect::armorPercentagePerLevel)
                        ).apply(lastStandInstance, LastStandEffect::new)
            );

    @Override
    public void onDeactivated(@NotNull EnchantedItemInUse pItem, @NotNull Entity pEntity, @NotNull Vec3 pPos, int pEnchantmentLevel) {
        EnchantmentEntityEffect.super.onDeactivated(pItem, pEntity, pPos, pEnchantmentLevel);
    }

    @Override
    public void apply(@NotNull ServerLevel pLevel, int pEnchantmentLevel, @NotNull EnchantedItemInUse pItem, @NotNull Entity pEntity, @NotNull Vec3 pOrigin) {

    }

    @Override
    public @NotNull MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }



    @Nullable
    public AttributeModifier getAttributeModifier(int intValue, ItemStack itemStack, EquipmentSlot equipmentSlot) {
        if (itemStack.getOrDefault(UEDataComponentTypes.USER_HEALTH, 0).floatValue() <= itemStack.getOrDefault(UEDataComponentTypes.USER_MAX_HEALTH, 0).floatValue() * maxHealthPercentage.value()) {
            return new AttributeModifier(AttributeModifierId.LAST_STAND_ARMOR.withSuffix("/" + equipmentSlot.getSerializedName()), armorPercentagePerLevel.calculate(intValue), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        }
        return null;
    }
}
