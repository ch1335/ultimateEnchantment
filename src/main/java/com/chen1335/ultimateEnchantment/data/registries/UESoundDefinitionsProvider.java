package com.chen1335.ultimateEnchantment.data.registries;

import com.chen1335.ultimateEnchantment.API.objects.UESounds;
import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public class UESoundDefinitionsProvider extends SoundDefinitionsProvider {

    protected UESoundDefinitionsProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, UltimateEnchantment.MODID, helper);
    }

    @Override
    public void registerSounds() {
        add(UESounds.TEAR.getKey().location(), SoundDefinition.definition().with(sound(UltimateEnchantment.id("misc/tear"))));
    }
}
