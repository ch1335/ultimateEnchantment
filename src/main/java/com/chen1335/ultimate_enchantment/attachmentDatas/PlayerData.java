package com.chen1335.ultimate_enchantment.attachmentDatas;

import com.chen1335.ultimate_enchantment.netWork.BreakSpeedMultiplierPack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.network.PacketDistributor;

public class PlayerData implements ValueIOSerializable {
    public float breakSpeedMultiplier = 0;
    public float breakSpeedMultiplierRemainingTime = 0;

    public int vanquisherCooldown = 0;

    public void tick(Player player) {
        if (!player.level().isClientSide() && breakSpeedMultiplier > 0) {
            breakSpeedMultiplierRemainingTime--;
            if (breakSpeedMultiplierRemainingTime <= 0) {
                breakSpeedMultiplier = 0;
                PacketDistributor.sendToPlayer((ServerPlayer) player, new BreakSpeedMultiplierPack(breakSpeedMultiplier));
            }
        }
        if (vanquisherCooldown > 0) {
            vanquisherCooldown--;
        }
    }

    // 本类目前没有需要持久化的字段（原有实现同样写入空 NBT），故两个方法保持空实现。
    @Override
    public void serialize(ValueOutput output) {
    }

    @Override
    public void deserialize(ValueInput input) {
    }
}
