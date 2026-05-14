package com.chen1335.ultimateEnchantment.API;

import com.chen1335.ultimateEnchantment.AttachmentDatas.CommonEntityData;
import com.chen1335.ultimateEnchantment.AttachmentDatas.PlayerData;
import com.chen1335.ultimateEnchantment.AttachmentDatas.UEProjectileData;
import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class AttachmentTypes {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, UltimateEnchantment.MODID);

    public static final Supplier<AttachmentType<PlayerData>> PLAYER_DATA = ATTACHMENT_TYPES.register(
            "player_data", () -> AttachmentType.serializable((holder) -> new PlayerData()).build()
    );

    public static final Supplier<AttachmentType<UEProjectileData>> PROJECTILE_DATA = ATTACHMENT_TYPES.register(
            "projectile_data", () -> AttachmentType.serializable((holder) -> new UEProjectileData()).build()
    );

    public static final Supplier<AttachmentType<CommonEntityData>> COMMON_ENTITY = ATTACHMENT_TYPES.register(
            "common_entity", () -> AttachmentType.serializable((holder) -> new CommonEntityData()).build()
    );
}
