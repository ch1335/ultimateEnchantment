package com.chen1335.ultimateEnchantment.client;

import com.chen1335.ultimateEnchantment.enchantment.EnchantmentConfigs;
import com.chen1335.ultimateEnchantment.enchantment.enchatments.ApothicEnchantingEnchantments;
import com.chen1335.ultimateEnchantment.enchantment.enchatments.IronsSpellBooksEnchantments;
import com.chen1335.ultimateEnchantment.enchantment.enchatments.UEEnchantments;
import com.chen1335.ultimateEnchantment.enchantment.specialEnchantEffects.CutDown;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class EnchantmentSpecialDesc {
    private static final Map<ResourceKey<Enchantment>, BiFunction<Holder<Enchantment>, Integer, MutableComponent>> DESC = new HashMap<>();

    public static MutableComponent getNewDescription(Holder<Enchantment> holder, int level) {
        @Nullable ResourceKey<Enchantment> resourceKey = holder.getKey();
        if (resourceKey == null) {
            return null;
        }

        for (Map.Entry<ResourceKey<Enchantment>, BiFunction<Holder<Enchantment>, Integer, MutableComponent>> entry : DESC.entrySet()) {
            if (entry.getKey().equals(resourceKey)) {
                return entry.getValue().apply(holder, level);
            }
        }

        return null;
    }

    static {
        DESC.put(UEEnchantments.THUNDER_BOLT, (holder, level) -> {
            return Component.translatable("enchantment.ultimate_enchantment.thunder_bolt.specialDesc", format((float) (level * 5)), format((float) (level * 10)));
        });
        DESC.put(UEEnchantments.LIFE_STEAL, (holder, level) -> {
            return Component.translatable("enchantment.ultimate_enchantment.life_steal.specialDesc", format(EnchantmentConfigs.LifeSteal.healPercentPerLevel * level * 100));
        });

        DESC.put(UEEnchantments.CUT_DOWN, (holder, level) -> {
            if (Minecraft.getInstance().hitResult instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity target && Minecraft.getInstance().player != null) {
                float playerMaxHealth = Minecraft.getInstance().player.getMaxHealth();

                float damageMultiplier = CutDown.getDamageMultiplier(playerMaxHealth, target.getHealth(), level);
                return Component.translatable("enchantment.ultimate_enchantment.cut_down.specialDesc.1", format(damageMultiplier * 100, 2));
            } else {
                return Component.translatable("enchantment.ultimate_enchantment.cut_down.specialDesc", format(CutDown.getDamageMultiplierByLevel(level) * 100, 3), format(CutDown.getMaxDamageMultiplier(level) * 100));
            }
        });

        DESC.put(UEEnchantments.LEGEND, (holder, level) -> {
            return Component.translatable("enchantment.ultimate_enchantment.legend.specialDesc", format(level)).withStyle(ChatFormatting.LIGHT_PURPLE);
        });

        DESC.put(UEEnchantments.ULTIMATE, (holder, level) -> {
            return Component.translatable("enchantment.ultimate_enchantment.ultimate.specialDesc", format(level)).withStyle(ChatFormatting.LIGHT_PURPLE);
        });

        DESC.put(UEEnchantments.LAST_STAND, (holder, level) -> {
            return Component.translatable("enchantment.ultimate_enchantment.last_stand.specialDesc", format(5 * level)).withStyle(ChatFormatting.LIGHT_PURPLE);
        });

        DESC.put(UEEnchantments.OVER_GROW, (holder, level) -> {
            return Component.translatable("enchantment.ultimate_enchantment.over_grow.specialDesc", format(2 * level));
        });


        DESC.put(ApothicEnchantingEnchantments.SCABBING, (holder, level) -> {
            return Component.translatable("enchantment.ultimate_enchantment.scabbing.specialDesc", format(5 * level));
        });

        DESC.put(ApothicEnchantingEnchantments.QUICK_SHOOTING, (holder, level) -> {
            return Component.translatable("enchantment.ultimate_enchantment.quick_shooting.specialDesc", format(5 * level));
        });

        DESC.put(IronsSpellBooksEnchantments.MANA_STEAL, (holder, level) -> {
            return Component.translatable("enchantment.ultimate_enchantment.mana_steal.specialDesc", format(EnchantmentConfigs.ManaSteal.ManaRegainPercentPerLevel * level * 100));
        });

        DESC.put(IronsSpellBooksEnchantments.HARDENED_MANA, (holder, level) -> {
            return Component.translatable("enchantment.ultimate_enchantment.hardened_mana.specialDesc", format(level));
        });
        DESC.put(UEEnchantments.KINETIC_ENERGY, (holder, level) -> {
            return Component.translatable("enchantment.ultimate_enchantment.kinetic_energy.specialDesc", format(level * 10));
        });
        DESC.put(UEEnchantments.TEAR, (holder, level) -> {
            return Component.translatable("enchantment.ultimate_enchantment.tear.specialDesc", 1 + level).withStyle(ChatFormatting.LIGHT_PURPLE);
        });
        DESC.put(UEEnchantments.LETHAL_TEMPO, (holder, level) -> {
            return Component.translatable("enchantment.ultimate_enchantment.lethal_tempo.specialDesc", 20, level * 40).withStyle(ChatFormatting.LIGHT_PURPLE);
        });

        DESC.put(UEEnchantments.QUICK_BAIT, (holder, level) -> {
            return Component.translatable("enchantment.ultimate_enchantment.quick_bait.specialDesc", level*5);
        });
        DESC.put(UEEnchantments.DOUBLE_HOOK, (holder, level) -> {
            return Component.translatable("enchantment.ultimate_enchantment.double_hook.specialDesc", level*5);
        });
    }

    private static String format(float f) {
        return String.format("%.1f", f);
    }

    private static String format(float f, int index) {
        return String.format("%." + index + "f", f);
    }
}
