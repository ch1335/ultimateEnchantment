package com.chen1335.ultimateEnchantment.dataComponentType;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class UEDataComponentTypes {
    public static DeferredRegister<DataComponentType<?>> AEA_DATA = DeferredRegister.DataComponents.createDataComponents(Registries.DATA_COMPONENT_TYPE,UltimateEnchantment.MODID);

    public static DeferredHolder<DataComponentType<?>,DataComponentType<Float>> USER_HEALTH = AEA_DATA.register("user_health",()-> DataComponentType.<Float>builder().persistent(ExtraCodecs.POSITIVE_FLOAT).networkSynchronized(ByteBufCodecs.FLOAT).build());

    public static DeferredHolder<DataComponentType<?>,DataComponentType<Float>> USER_MAX_HEALTH = AEA_DATA.register("user_max_health",()-> DataComponentType.<Float>builder().networkSynchronized(ByteBufCodecs.FLOAT).persistent(ExtraCodecs.POSITIVE_FLOAT).build());

    public static DeferredHolder<DataComponentType<?>,DataComponentType<Boolean>> LAST_STAND_ACTIVE = AEA_DATA.register("last_sand_active",()-> DataComponentType.<Boolean>builder().networkSynchronized(ByteBufCodecs.BOOL).persistent(Codec.BOOL).build());

}
