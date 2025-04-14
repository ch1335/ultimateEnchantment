package com.chen1335.ultimateEnchantment.client;

import com.chen1335.ultimateEnchantment.enchantment.EnchantmentConfigs;
import com.chen1335.ultimateEnchantment.enchantment.enchatments.ApothicEnchantingEnchantments;
import com.chen1335.ultimateEnchantment.enchantment.enchatments.IronsSpellBooksEnchantments;
import com.chen1335.ultimateEnchantment.enchantment.enchatments.UEEnchantments;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

public class EnchantmentSpecialDesc {
    public static MutableComponent getNewDescription(Holder<Enchantment> holder, int level) {
        @Nullable ResourceKey<Enchantment> resourceKey = holder.getKey();
        if (resourceKey == null) {
            return null;
        }

        if (resourceKey.equals(UEEnchantments.LIFE_STEAL)) {
            return Component.translatable("enchantment.ultimate_enchantment.life_steal.specialDesc", format(EnchantmentConfigs.LifeSteal.healPercentPerLevel * level * 100), format(EnchantmentConfigs.LifeSteal.maxHealPercentBaseMaxHealth * level * 100));
        } else if (resourceKey.equals(UEEnchantments.CUT_DOWN)) {
            return Component.translatable("enchantment.ultimate_enchantment.cut_down.specialDesc", format((float) (0.01 * level), 2), format(10 * level));
        } else if (resourceKey.equals(UEEnchantments.PIERCE_THROUGH)) {
            return Component.translatable("enchantment.ultimate_enchantment.pierce_through.specialDesc", format(2 * level));
        } else if (resourceKey.equals(UEEnchantments.LEGEND)) {
            return Component.translatable("enchantment.ultimate_enchantment.legend.specialDesc", format(2 * level)).withStyle(ChatFormatting.LIGHT_PURPLE);
        } else if (resourceKey.equals(UEEnchantments.ULTIMATE)) {
            return Component.translatable("enchantment.ultimate_enchantment.ultimate.specialDesc", format(level)).withStyle(ChatFormatting.LIGHT_PURPLE);
        } else if (resourceKey.equals(UEEnchantments.LAST_STAND)) {
            return Component.translatable("enchantment.ultimate_enchantment.last_stand.specialDesc", format(5 * level)).withStyle(ChatFormatting.LIGHT_PURPLE);
        } else if (resourceKey.equals(UEEnchantments.OVER_GROW)) {
            return Component.translatable("enchantment.ultimate_enchantment.over_grow.specialDesc", format(2 * level));
        } else if (resourceKey.equals(UEEnchantments.CRITICAL_CHANCE)) {
            return Component.translatable("enchantment.ultimate_enchantment.critical_chance.specialDesc", format(2 * level));
        } else if (resourceKey.equals(UEEnchantments.MANA_STEAL)) {
            return Component.translatable("enchantment.ultimate_enchantment.mana_steal.specialDesc", format(EnchantmentConfigs.ManaSteal.ManaRegainPercentPerLevel * level * 100), format(EnchantmentConfigs.ManaSteal.maxManaRegainPercentBaseMaxMana * level * 100));
        } else if (resourceKey.equals(UEEnchantments.CRITICAL_DAMAGE)) {
            return Component.translatable("enchantment.ultimate_enchantment.critical_damage.specialDesc", format(5 * level));
        } else if (resourceKey.equals(ApothicEnchantingEnchantments.SCABBING)) {
            return Component.translatable("enchantment.ultimate_enchantment.scabbing.specialDesc", format(5 * level));
        } else if (resourceKey.equals(ApothicEnchantingEnchantments.QUICK_SHOOTING)) {
            return Component.translatable("enchantment.ultimate_enchantment.quick_shooting.specialDesc", format(5 * level));
        } else if (resourceKey.equals(IronsSpellBooksEnchantments.HARDENED_MANA)) {
            return Component.translatable("enchantment.ultimate_enchantment.hardened_mana.specialDesc", format(level));
        }

        return null;
    }

    private static String format(float f) {
        return String.format("%.1f", f);
    }

    private static String format(float f, int index) {
        return String.format("%." + index + "f", f);
    }
}
