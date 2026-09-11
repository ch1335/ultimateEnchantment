package com.chen1335.ultimateEnchantment.mobEffect.effects;


import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.enchantment.enchantments.Vanquisher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import javax.script.SimpleBindings;

public class ActiveVanquisher extends MobEffect {


    public ActiveVanquisher(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath(UltimateEnchantment.MODID, "vanquisher"), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
                key -> Vanquisher.DAMAGE_PER_STACK.calculate(new SimpleBindings(new SimpleBindings())) * Vanquisher.MAX_STACKS.calculate(new SimpleBindings(new SimpleBindings())));
        this.addAttributeModifier(Attributes.ATTACK_SPEED, ResourceLocation.fromNamespaceAndPath(UltimateEnchantment.MODID, "vanquisher"), AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                key -> Vanquisher.SPEED_PER_STACK.calculate(new SimpleBindings(new SimpleBindings())) * Vanquisher.MAX_STACKS.calculate(new SimpleBindings(new SimpleBindings())));
    }


}
