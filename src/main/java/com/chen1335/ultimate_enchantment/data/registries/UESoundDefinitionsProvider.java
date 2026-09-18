package com.chen1335.ultimate_enchantment.data.registries;

import com.chen1335.ultimate_enchantment.API.objects.UESounds;
import com.chen1335.ultimate_enchantment.UltimateEnchantment;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

/**
 * 26.1 起 {@code ExistingFileHelper} 已从 NeoForge 移除，
 * {@link SoundDefinitionsProvider} 构造器只剩 {@code (PackOutput, String modId)}。
 * <p>
 * {@code add(...)} 现在接受 {@code Holder<SoundEvent>} / {@code SoundEvent}，
 * 故直接传 {@code UESounds.TEAR} 而不再经过 {@code getKey().identifier()}。
 */
public class UESoundDefinitionsProvider extends SoundDefinitionsProvider {

    protected UESoundDefinitionsProvider(PackOutput output) {
        super(output, UltimateEnchantment.MODID);
    }

    @Override
    public void registerSounds() {
        add(UESounds.TEAR, SoundDefinition.definition().with(sound(UltimateEnchantment.id("misc/tear"))));
    }
}
