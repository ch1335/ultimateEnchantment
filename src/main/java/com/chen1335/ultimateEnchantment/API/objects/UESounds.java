package com.chen1335.ultimateEnchantment.API.objects;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class UESounds {
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(
            Registries.SOUND_EVENT, UltimateEnchantment.MODID
    );

    public static final Holder<SoundEvent> TEAR = registerSound("tear");

    private static Holder<SoundEvent> registerSound(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(UltimateEnchantment.id(name)));
    }

    public static void register(IEventBus modEventBus) {
        SOUNDS.register(modEventBus);
    }
}
