package com.chen1335.ultimateEnchantment.netWork;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.client.TearParticlesClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * 撕裂特效只发目标实体 id：方向、长度、粒子铺法全部由客户端现算。
 * 这样一次结算只发一个包，而且各客户端各自随机，裂口角度天然错开。
 */
public record TearParticlesPack(int entityId) implements CustomPacketPayload {
    public static final Type<TearParticlesPack> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(UltimateEnchantment.MODID, "tear_particles"));

    public static final StreamCodec<ByteBuf, TearParticlesPack> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, TearParticlesPack::entityId,
            TearParticlesPack::new
    );

    public void handler(IPayloadContext iPayloadContext) {
        // 方法引用要到 lambda 执行时才解析，专用服务端不会因此加载 TearParticlesClient
        iPayloadContext.enqueueWork(() -> TearParticlesClient.spawnTearParticles(entityId));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
