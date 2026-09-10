package com.chen1335.ultimateEnchantment.client;

import com.chen1335.ultimateEnchantment.data.registries.enchatments.ApothicEnchantingEnchantments;
import com.chen1335.ultimateEnchantment.data.registries.enchatments.IronsSpellBooksEnchantments;
import com.chen1335.ultimateEnchantment.data.registries.enchatments.UEEnchantmentsDataGen;
import com.chen1335.ultimateEnchantment.enchantment.EnchantmentBasic;
import com.chen1335.ultimateEnchantment.enchantment.UEEnchantments;
import com.chen1335.ultimateEnchantment.enchantment.effectComponents.UEEnchantmentEffectComponents;
import com.chen1335.ultimateEnchantment.enchantment.effectComponents.UltimateEnchantment.*;
import com.chen1335.ultimateEnchantment.enchantment.effects.UltimateEnchantment.LastStandEffect;
import dev.shadowsoffire.apothic_attributes.api.ALObjects;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

public class EnchantmentSpecialDesc {
    private static final Map<ResourceKey<Enchantment>, BiFunction<Holder<Enchantment>, Integer, MutableComponent>> DESC = new HashMap<>();

    public static MutableComponent getNewDescription(Holder<Enchantment> holder, int level) {
        @Nullable ResourceKey<Enchantment> resourceKey = holder.getKey();
        if (resourceKey == null) {
            return null;
        }

        for (Map.Entry<ResourceKey<Enchantment>, EnchantmentBasic> entry : UEEnchantments.MAP.entrySet()) {
            if (entry.getKey().equals(resourceKey)) {
                MutableComponent component = entry.getValue().getDesc(level);
                ComponentUtils.mergeStyles(component, Style.EMPTY.withColor(ChatFormatting.DARK_GRAY));
                return component;
            }
        }


        return null;
    }

    static {
        DESC.put(UEEnchantmentsDataGen.THUNDER_BOLT, (holder, level) -> {
            ThunderBoltComponent thunderBoltComponent = Objects.requireNonNull(holder.value().effects().get(UEEnchantmentEffectComponents.THUNDER_BOLT.get()));
            return Component.translatable("enchantment.ultimate_enchantment.thunder_bolt.specialDesc", format(level * thunderBoltComponent.mainTargetDamage() * 100), format(level * thunderBoltComponent.otherTargetDamage() * 100), thunderBoltComponent.range());
        });
        DESC.put(UEEnchantmentsDataGen.LIFE_STEAL, (holder, level) -> {
            LifeStealComponent lifeStealComponent = Objects.requireNonNull(holder.value().effects().get(UEEnchantmentEffectComponents.LIFE_STEAL.get()));

            return Component.translatable("enchantment.ultimate_enchantment.life_steal.specialDesc", format(lifeStealComponent.healPercentPerLevel() * level * 100), format(lifeStealComponent.maxPercent() * 100));
        });

        DESC.put(UEEnchantmentsDataGen.LEGEND, (holder, level) -> {
            LegendComponent component = Objects.requireNonNull(holder.value().effects().get(UEEnchantmentEffectComponents.LEGEND.get()));

            return Component.translatable("enchantment.ultimate_enchantment.legend.specialDesc", format(level * component.attributeMultiplePerLevel() * 100)).withStyle(ChatFormatting.LIGHT_PURPLE);
        });

        DESC.put(UEEnchantmentsDataGen.LAST_STAND, (holder, level) -> {
            float value = 0;
            List<ConditionalEffect<EnchantmentEntityEffect>> list = holder.value().getEffects(UEEnchantmentEffectComponents.LAST_STAND.value());
            for (ConditionalEffect<EnchantmentEntityEffect> enchantmentEntityEffectConditionalEffect : list) {
                if (enchantmentEntityEffectConditionalEffect.effect() instanceof LastStandEffect lastStandEffect) {
                    value = lastStandEffect.armorPercentagePerLevel().calculate(level);
                }
            }
            return Component.translatable("enchantment.ultimate_enchantment.last_stand.specialDesc", format(value * 100)).withStyle(ChatFormatting.LIGHT_PURPLE);
        });

        DESC.put(UEEnchantmentsDataGen.OVER_GROW, (holder, level) -> {
            float value = 0;
            List<EnchantmentAttributeEffect> list = holder.value().getEffects(EnchantmentEffectComponents.ATTRIBUTES);
            for (EnchantmentAttributeEffect effect : list) {
                if (effect.attribute().value() == Attributes.MAX_HEALTH.value() && effect.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_BASE) {
                    value = effect.amount().calculate(level);
                }
            }
            return Component.translatable("enchantment.ultimate_enchantment.over_grow.specialDesc", format(value * 100));
        });


        DESC.put(ApothicEnchantingEnchantments.SCABBING, (holder, level) -> {
            float value = 0;
            List<EnchantmentAttributeEffect> list = holder.value().getEffects(EnchantmentEffectComponents.ATTRIBUTES);
            for (EnchantmentAttributeEffect effect : list) {
                if (effect.attribute().value() == ALObjects.Attributes.ARMOR_SHRED.value() && effect.operation() == AttributeModifier.Operation.ADD_VALUE) {
                    value = effect.amount().calculate(level);
                }
            }
            return Component.translatable("enchantment.ultimate_enchantment.scabbing.specialDesc", format(value * 100));
        });

        DESC.put(ApothicEnchantingEnchantments.QUICK_SHOOTING, (holder, level) -> {
            float value = 0;
            List<EnchantmentAttributeEffect> list = holder.value().getEffects(EnchantmentEffectComponents.ATTRIBUTES);
            for (EnchantmentAttributeEffect effect : list) {
                if (effect.attribute().value() == ALObjects.Attributes.DRAW_SPEED.value() && effect.operation() == AttributeModifier.Operation.ADD_VALUE) {
                    value = effect.amount().calculate(level);
                }
            }
            return Component.translatable("enchantment.ultimate_enchantment.quick_shooting.specialDesc", format(value * 100));
        });

        DESC.put(IronsSpellBooksEnchantments.MANA_STEAL, (holder, level) -> {
            return Component.translatable("enchantment.ultimate_enchantment.mana_steal.specialDesc", format(Objects.requireNonNull(holder.value().effects().get(UEEnchantmentEffectComponents.MANA_STEAL.get())).ManaRegainPercentPerLevel() * level * 100));
        });

        DESC.put(IronsSpellBooksEnchantments.HARDENED_MANA, (holder, level) -> {
            HardenedManaComponent hardenedManaComponent = Objects.requireNonNull(holder.value().effects().get(UEEnchantmentEffectComponents.HARDENED_MANA.get()));

            return Component.translatable("enchantment.ultimate_enchantment.hardened_mana.specialDesc", format(hardenedManaComponent.maxArmorPerLevel()), format(level * hardenedManaComponent.maxArmorPerLevel()));
        });
        DESC.put(UEEnchantmentsDataGen.KINETIC_ENERGY, (holder, level) -> {
            KineticEnergyComponent component = Objects.requireNonNull(holder.value().effects().get(UEEnchantmentEffectComponents.KINETIC_ENERGY.get()));

            return Component.translatable("enchantment.ultimate_enchantment.kinetic_energy.specialDesc", format(component.breakSpeedMultiplierPerBlock() * 100, 1), format(level * component.maxSpeedPerLevel() * 100));
        });
        DESC.put(UEEnchantmentsDataGen.TEAR, (holder, level) -> {
            TearComponent component = Objects.requireNonNull(holder.value().effects().get(UEEnchantmentEffectComponents.TEAR.get()));

            return Component.translatable("enchantment.ultimate_enchantment.tear.specialDesc", 1 + level, component.damageAdd() * 100, component.totalHealthDamage() * 100).withStyle(ChatFormatting.LIGHT_PURPLE);
        });

        DESC.put(UEEnchantmentsDataGen.QUICK_BAIT, (holder, level) -> {
            QuickBaitComponent component = Objects.requireNonNull(holder.value().effects().get(UEEnchantmentEffectComponents.QUICK_BAIT.get()));

            return Component.translatable("enchantment.ultimate_enchantment.quick_bait.specialDesc", level * component.speedPerLevel() * 100);
        });
        DESC.put(UEEnchantmentsDataGen.DOUBLE_HOOK, (holder, level) -> {
            DoubleHookComponent component = Objects.requireNonNull(holder.value().effects().get(UEEnchantmentEffectComponents.DOUBLE_HOOK.get()));
            return Component.translatable("enchantment.ultimate_enchantment.double_hook.specialDesc", level * component.chancePerLevel() * 100);
        });

        DESC.put(UEEnchantmentsDataGen.VANQUISHER, (holder, level) -> {
            VanquisherComponent component = Objects.requireNonNull(holder.value().effects().get(UEEnchantmentEffectComponents.VANQUISHER.get()));

            return Component.translatable("enchantment.ultimate_enchantment.vanquisher.specialDesc");
        });

        DESC.put(UEEnchantmentsDataGen.ETERNAL, (holder, level) -> {
            return Component.translatable("enchantment.ultimate_enchantment.eternal.specialDesc");
        });
    }

    private static String format(float f) {
        return format(f, 1);
    }

    private static String format(float value, int i) {
        return new BigDecimal(String.format("%." + i + "f", value)).stripTrailingZeros().toPlainString();
    }
}
