package com.chen1335.ultimate_enchantment.mobEffect.effects;

import com.chen1335.ultimate_enchantment.UltimateEnchantment;
import com.chen1335.ultimate_enchantment.enchantment.enchantments.Vanquisher;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import javax.script.SimpleBindings;

/**
 * 「未激活的征服者」药水效果。
 * <p>
 * 26.1 迁移：原本这里还有一个 {@code renderLevel(GuiGraphics, ...)} 静态方法负责在 HUD 图标上
 * 叠加等级数字，但它定义在 common 侧却引用纯客户端类型。该职责已迁到
 * {@code client.effects.UnActiveVanquisherClientExtensions}（走 NeoForge 的
 * {@code IClientMobEffectExtensions} 扩展点），本类不再持有任何客户端引用。
 */
public class UnActiveVanquisher extends MobEffect {

    public UnActiveVanquisher(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, Identifier.fromNamespaceAndPath(UltimateEnchantment.MODID, "vanquisher"), Vanquisher.DAMAGE_PER_STACK.calculate(new SimpleBindings()), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_SPEED, Identifier.fromNamespaceAndPath(UltimateEnchantment.MODID, "vanquisher"), Vanquisher.SPEED_PER_STACK.calculate(new SimpleBindings()), AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }
}
