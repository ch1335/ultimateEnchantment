package com.chen1335.ultimateEnchantment.AttachmentDatas;

import com.chen1335.ultimateEnchantment.enchantment.specialEnchantEffects.HardenedManaEffect;
import com.chen1335.ultimateEnchantment.netWork.BreakSpeedMultiplierPack;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class PlayerData implements INBTSerializable<CompoundTag> {
    public HardenedManaEffect hardenedManaEffect = new HardenedManaEffect();
    public float breakSpeedMultiplier = 0;
    public float breakSpeedMultiplierRemainingTime = 0;

    public void tick(Player player) {
        if (breakSpeedMultiplier > 0) {
            breakSpeedMultiplierRemainingTime--;
            if (breakSpeedMultiplierRemainingTime <= 0) {
                breakSpeedMultiplier = 0;
                if (!player.level().isClientSide()) {
                    PacketDistributor.sendToPlayer((ServerPlayer) player,new BreakSpeedMultiplierPack(breakSpeedMultiplier));
                }
            }
        }
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
