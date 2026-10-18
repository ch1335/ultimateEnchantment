package com.chen1335.ultimateEnchantment.enchantment.enchantments;

import com.chen1335.arcanumlib.modules.attribute.ArcanumAttributes;
import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.common.Formula;
import com.chen1335.ultimateEnchantment.enchantment.EnchantmentBasic;
import com.chen1335.ultimateEnchantment.enchantment.UEEnchantments;
import com.chen1335.ultimateEnchantment.tags.UEItemTags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;

import javax.script.SimpleBindings;
import java.util.List;
import java.util.Map;

public class HurtDeepens extends EnchantmentBasic {
    public static final Formula DAMAGE_PER_PERCENT = new Formula("0.01");

    public static final Formula MAX_BONUS = new Formula("0.1*lvl");

    public HurtDeepens() {
        super("hurt_deepens");
        supported_items = new Type.TagType<>(UEItemTags.RANGE_WEAPON_ENCHANTABLE);
        primary_items = supported_items;
        max_cost = new Enchantment.Cost(90, 10);
        min_cost = new Enchantment.Cost(20, 10);
        slots = List.of(EquipmentSlotGroup.HAND);
        max_level = 4;
        weight = 2;
    }

    @Override
    protected void registerFormula(Map<String, Formula> formulas) {
        formulas.put("damage_per_percent", DAMAGE_PER_PERCENT);
        formulas.put("max_bonus", MAX_BONUS);
    }

    @Override
    public List<MutableComponent> getDesc(int level, TooltipFlag tooltipFlag) {
        SimpleBindings bindings = buildBindings(level);
        // 第一个 %s 换成伤害修正属性的描述（与 CutDown 同一口径），而不是裸百分比：
        // 每低 1 个百分点获得的那一份加成本身就是一条 DAMAGE_MULTIPLIER 修正。
        AttributeModifier modifier = new AttributeModifier(
                UltimateEnchantment.id("hurt_deepens"),
                DAMAGE_PER_PERCENT.calculate(bindings),
                AttributeModifier.Operation.ADD_VALUE);
        return List.of(Component.translatable(getDescId(),
                ArcanumAttributes.DAMAGE_MULTIPLIER.value().toComponent(modifier, tooltipFlag).withStyle(ChatFormatting.GOLD),
                MAX_BONUS.toComponent(bindings, 100, 1)
        ).withStyle(ChatFormatting.GOLD));
    }

    public static float getDamageBonus(LivingEntity attacker, LivingEntity target, AbstractArrow arrow) {
        ItemStack bow = arrow.getWeaponItem();
        if (bow == null || bow.isEmpty()) {
            return 0.0F;
        }
        int lvl = UEEnchantments.HURT_DEEPENS.getEnchantmentLevel(bow, arrow.level());
        if (lvl <= 0) {
            return 0.0F;
        }
        float attackerPercent = attacker.getHealth() / attacker.getMaxHealth();
        float targetPercent = target.getHealth() / target.getMaxHealth();
        // 差值是 0~1 的比例，乘 100 换成「个百分点」再乘每点的加成比例
        float diffPoints = (attackerPercent - targetPercent) * 100.0F;
        SimpleBindings bindings = buildBindings(lvl);
        float bonus = Math.min(
                diffPoints * DAMAGE_PER_PERCENT.calculate(bindings),
                MAX_BONUS.calculate(bindings)
        );
        return Math.max(bonus, 0.0F);
    }
}
