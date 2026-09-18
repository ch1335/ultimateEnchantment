package com.chen1335.ultimate_enchantment.mobEffect.effects;


import com.chen1335.ultimate_enchantment.UltimateEnchantment;
import com.chen1335.ultimate_enchantment.enchantment.enchantments.Vanquisher;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import javax.script.SimpleBindings;

public class ActiveVanquisher extends MobEffect {


    public ActiveVanquisher(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, Identifier.fromNamespaceAndPath(UltimateEnchantment.MODID, "vanquisher"), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
                key -> Vanquisher.DAMAGE_PER_STACK.calculate(new SimpleBindings(new SimpleBindings())) * Vanquisher.MAX_STACKS.calculate(new SimpleBindings(new SimpleBindings())));
        this.addAttributeModifier(Attributes.ATTACK_SPEED, Identifier.fromNamespaceAndPath(UltimateEnchantment.MODID, "vanquisher"), AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                key -> Vanquisher.SPEED_PER_STACK.calculate(new SimpleBindings(new SimpleBindings())) * Vanquisher.MAX_STACKS.calculate(new SimpleBindings(new SimpleBindings())));
    }


}
