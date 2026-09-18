package com.chen1335.ultimate_enchantment.netWork;

import com.chen1335.ultimate_enchantment.API.AttachmentTypes;
import com.chen1335.ultimate_enchantment.UltimateEnchantment;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record BreakSpeedMultiplierPack(float breakSpeedMultiplier) implements CustomPacketPayload {
    public static final Type<BreakSpeedMultiplierPack> TYPE = new Type<>(Identifier.fromNamespaceAndPath(UltimateEnchantment.MODID, "player_data"));

    public static final StreamCodec<ByteBuf, BreakSpeedMultiplierPack> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, BreakSpeedMultiplierPack::breakSpeedMultiplier,
            BreakSpeedMultiplierPack::new
    );

    public void handler(IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> iPayloadContext.player().getData(AttachmentTypes.PLAYER_DATA).breakSpeedMultiplier = breakSpeedMultiplier);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
