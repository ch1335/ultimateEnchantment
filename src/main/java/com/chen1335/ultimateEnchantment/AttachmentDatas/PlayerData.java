package com.chen1335.ultimateEnchantment.AttachmentDatas;

import com.chen1335.ultimateEnchantment.enchantment.specialEnchantEffects.HardenedManaEffect;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class PlayerData implements INBTSerializable<CompoundTag> {
    public HardenedManaEffect hardenedManaEffect = new HardenedManaEffect();

    public void tick(Player player) {
        hardenedManaEffect.tick(player);
    }


    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {

    }
}
