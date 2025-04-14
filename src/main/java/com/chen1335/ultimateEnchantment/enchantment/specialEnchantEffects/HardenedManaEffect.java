package com.chen1335.ultimateEnchantment.enchantment.specialEnchantEffects;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.Objects;

public class HardenedManaEffect {
    private final Map<EquipmentSlot, EffectObjectHolder> holderMap = ImmutableMap.of(
            EquipmentSlot.HEAD, new EffectObjectHolder(),
            EquipmentSlot.CHEST, new EffectObjectHolder(),
            EquipmentSlot.LEGS, new EffectObjectHolder(),
            EquipmentSlot.FEET, new EffectObjectHolder()
    );


    public void addArmor(Player player, EquipmentSlot equipmentSlot, float armor, float maxArmor) {
        EffectObjectHolder holder = holderMap.get(equipmentSlot);
        if (holder != null) {
            holder.armorStored = Math.min(holderMap.get(equipmentSlot).armorStored + armor, maxArmor);
            holder.noUpdatedTick = 0;
            removeAndAddAttribute(player, equipmentSlot, holder.armorStored);
        }
    }

    public void tick(Player player) {
        holderMap.forEach((equipmentSlot, effectObjectHolder) -> {
            if (effectObjectHolder.armorStored > 0) {
                if (effectObjectHolder.noUpdatedTick > 200) {
                    effectObjectHolder.noUpdatedTick = 0;
                    effectObjectHolder.armorStored = 0;
                    removeAttribute(player, equipmentSlot);
                } else {
                    effectObjectHolder.noUpdatedTick++;
                }
            }
        });
    }

    private void removeAttribute(Player player, EquipmentSlot equipmentSlot) {
        Objects.requireNonNull(player.getAttribute(Attributes.ARMOR)).removeModifier(ResourceLocation.fromNamespaceAndPath(UltimateEnchantment.MODID, "hardened_mana_effect_" + equipmentSlot.getName()));
    }

    private void removeAndAddAttribute(Player player, EquipmentSlot equipmentSlot, float value) {
        removeAttribute(player, equipmentSlot);
        Objects.requireNonNull(player.getAttribute(Attributes.ARMOR)).addTransientModifier(new AttributeModifier(ResourceLocation.fromNamespaceAndPath(UltimateEnchantment.MODID, "hardened_mana_effect_" + equipmentSlot.getName()), value, AttributeModifier.Operation.ADD_VALUE));
    }

    private static class EffectObjectHolder {
        private int noUpdatedTick = 0;
        private float armorStored = 0;
    }
}
