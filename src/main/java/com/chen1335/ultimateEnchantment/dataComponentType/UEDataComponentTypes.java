package com.chen1335.ultimateEnchantment.dataComponentType;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.dataComponentType.dataComponentTypes.ItemFishedCount;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class UEDataComponentTypes {
    public static DeferredRegister.DataComponents AEA_DATA = DeferredRegister.DataComponents.createDataComponents(Registries.DATA_COMPONENT_TYPE, UltimateEnchantment.MODID);

    public static DeferredHolder<DataComponentType<?>, DataComponentType<Float>> USER_HEALTH = AEA_DATA.register("user_health",
            () -> DataComponentType.<Float>builder().persistent(Codec.FLOAT).networkSynchronized(ByteBufCodecs.FLOAT).build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<Float>> USER_MAX_HEALTH = AEA_DATA.register("user_max_health", () -> DataComponentType.<Float>builder().networkSynchronized(ByteBufCodecs.FLOAT).persistent(Codec.FLOAT).build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> LAST_STAND_ACTIVE = AEA_DATA.register("last_sand_active", () -> DataComponentType.<Boolean>builder().networkSynchronized(ByteBufCodecs.BOOL).persistent(Codec.BOOL).build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<Float>> ADDITION_SHOOT_CHANCE = AEA_DATA.register("addition_shoot_chance", () -> DataComponentType.<Float>builder().networkSynchronized(ByteBufCodecs.FLOAT).persistent(Codec.FLOAT).build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> LETHAL_TEMPO_TIME_LEFT = AEA_DATA.register("lethal_tempo_time_left", () -> DataComponentType.<Integer>builder().networkSynchronized(ByteBufCodecs.INT).persistent(Codec.INT).build());

}
