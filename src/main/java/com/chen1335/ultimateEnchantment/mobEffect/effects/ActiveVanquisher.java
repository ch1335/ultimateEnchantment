package com.chen1335.ultimateEnchantment.mobEffect.effects;

import com.chen.simpleRPGCore.API.objects.SRCAttributes;
import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class ActiveVanquisher extends MobEffect {


    public ActiveVanquisher(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath(UltimateEnchantment.MODID, "vanquisher"), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, key -> 0.5);
        this.addAttributeModifier(Attributes.ATTACK_SPEED, ResourceLocation.fromNamespaceAndPath(UltimateEnchantment.MODID, "vanquisher"), AttributeModifier.Operation.ADD_MULTIPLIED_BASE, key -> 1);
        this.addAttributeModifier(SRCAttributes.LIFE_STEAL, ResourceLocation.fromNamespaceAndPath(UltimateEnchantment.MODID, "vanquisher"), AttributeModifier.Operation.ADD_VALUE, key -> 0.05);
    }


}
