package com.chen1335.ultimateEnchantment.mobEffect;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.mobEffect.effects.ActiveVanquisher;
import com.chen1335.ultimateEnchantment.mobEffect.effects.UnActiveVanquisher;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.awt.*;

public class MobEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECT_DEFERRED_REGISTER = DeferredRegister.create(Registries.MOB_EFFECT, UltimateEnchantment.MODID);

    public static final DeferredHolder<MobEffect, UnActiveVanquisher> UN_ACTIVE_VANQUISHER = MOB_EFFECT_DEFERRED_REGISTER.register("un_active_vanquisher", () -> new UnActiveVanquisher(MobEffectCategory.BENEFICIAL, new Color(255, 255, 255, 0).getRGB()));

    public static final DeferredHolder<MobEffect, ActiveVanquisher> ACTIVE_VANQUISHER = MOB_EFFECT_DEFERRED_REGISTER.register("active_vanquisher", () -> new ActiveVanquisher(MobEffectCategory.BENEFICIAL, new Color(255, 255, 255, 0).getRGB()));
}
