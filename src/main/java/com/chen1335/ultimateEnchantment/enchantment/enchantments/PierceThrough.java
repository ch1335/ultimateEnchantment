package com.chen1335.ultimateEnchantment.enchantment.enchantments;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.common.Formula;
import com.chen1335.ultimateEnchantment.enchantment.EnchantmentBasic;
import com.chen1335.ultimateEnchantment.tags.UEItemTags;
import dev.shadowsoffire.apothic_attributes.api.ALObjects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

import java.util.List;
import java.util.Map;

/**
 * 凿穿：弓版本的护甲撕裂。
 * <p>
 * 效果与 {@link Scabbing} 一致 —— 同样是护甲撕裂，等级、权重、槽位、代价都照搬
 * —— 只是作用在弓上，且单级撕裂比例由 4% 提到 5%。
 * <p>
 * 两者沿用同一条复利递减曲线：以单级比例作底数逐级衰减，而不是单级比例乘等级。
 * 所以高等级的边际收益是递减的 —— 满级 5 级时 {@link Scabbing} 约 18.5%，
 * 这里约 22.6%。
 * <p>
 * 护甲撕裂本身来自 Apothic Attributes，所以和 {@link Scabbing} 一样要求
 * {@code apothic_enchanting} 在场。
 */
public class PierceThrough extends EnchantmentBasic {
    public static final Formula ARMOR_SHRED = new Formula("1 - Math.pow(1-0.05,lvl)");
    public static final ResourceLocation MODIFIER_ID = UltimateEnchantment.id("pierce_through");

    public PierceThrough() {
        super("pierce_through", "apothic_enchanting");
        supported_items = new Type.TagType<>(UEItemTags.RANGE_WEAPON_ENCHANTABLE);
        primary_items = supported_items;
        max_cost = new Enchantment.Cost(90, 10);
        min_cost = new Enchantment.Cost(25, 12);
        slots = List.of(EquipmentSlotGroup.HAND);
        max_level = 5;
        weight = 2;
    }

    @Override
    public void addModifier(ItemAttributeModifierEvent event, int lvl, EquipmentSlot equipmentSlot) {
        event.addModifier(ALObjects.Attributes.ARMOR_SHRED, new AttributeModifier(MODIFIER_ID, ARMOR_SHRED.calculate(buildBindings(lvl)), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
    }

    @Override
    protected void registerFormula(Map<String, Formula> formulas) {
        formulas.put("armor_shred", ARMOR_SHRED);
    }

    @Override
    public List<MutableComponent> getDesc(int level) {
        return List.of(Component.translatable(getDescId(), ARMOR_SHRED.toComponent(buildBindings(level), 100,1)).withStyle(ChatFormatting.GOLD));
    }
}
