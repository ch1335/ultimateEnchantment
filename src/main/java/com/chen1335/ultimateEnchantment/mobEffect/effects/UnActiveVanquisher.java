package com.chen1335.ultimateEnchantment.mobEffect.effects;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.enchantment.enchantments.Vanquisher;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import javax.script.SimpleBindings;

public class UnActiveVanquisher extends MobEffect {

    public UnActiveVanquisher(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath(UltimateEnchantment.MODID, "vanquisher"), Vanquisher.DAMAGE_PER_STACK.calculate(new SimpleBindings()), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_SPEED, ResourceLocation.fromNamespaceAndPath(UltimateEnchantment.MODID, "vanquisher"), Vanquisher.SPEED_PER_STACK.calculate(new SimpleBindings()), AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    public static void renderLevel(GuiGraphics pGuiGraphics, Font font, int x, int y, MobEffectInstance mobeffectinstance) {
        MutableComponent component = Component.translatable("enchantment.level." + (mobeffectinstance.getAmplifier() + 1));
        int width = font.width(component);
        pGuiGraphics.drawString(font, component, x - width / 2 + 10, y + 10, 16777215);
    }
}
