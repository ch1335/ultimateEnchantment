package com.chen1335.ultimate_enchantment.dataComponentType;

import com.chen1335.ultimate_enchantment.UltimateEnchantment;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.Unit;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class UEDataComponentTypes {
    public static DeferredRegister.DataComponents AEA_DATA = DeferredRegister.DataComponents.createDataComponents(Registries.DATA_COMPONENT_TYPE, UltimateEnchantment.MODID);

    public static DeferredHolder<DataComponentType<?>, DataComponentType<Float>> USER_HEALTH = AEA_DATA.register("user_health",
            () -> DataComponentType.<Float>builder().persistent(Codec.FLOAT).networkSynchronized(ByteBufCodecs.FLOAT).build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<Float>> USER_MAX_HEALTH = AEA_DATA.register("user_max_health", () -> DataComponentType.<Float>builder().networkSynchronized(ByteBufCodecs.FLOAT).persistent(Codec.FLOAT).build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> LAST_STAND_ACTIVE = AEA_DATA.register("last_sand_active", () -> DataComponentType.<Boolean>builder().networkSynchronized(ByteBufCodecs.BOOL).persistent(Codec.BOOL).build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<Float>> ADDITION_SHOOT_CHANCE = AEA_DATA.register("addition_shoot_chance", () -> DataComponentType.<Float>builder().networkSynchronized(ByteBufCodecs.FLOAT).persistent(Codec.FLOAT).build());

    public static DeferredHolder<DataComponentType<?>, DataComponentType<Long>> LETHAL_TEMPO_TIME_RECORD = AEA_DATA.register("lethal_tempo_time_record",
            () -> DataComponentType.<Long>builder().networkSynchronized(null).persistent(Codec.LONG).build());

    /**
     * 「附魔特殊说明」tooltip appender 的挂载点。
     *
     * <p>26.1 的 {@link net.neoforged.neoforge.event.RegisterTooltipAppendersEvent} 要求
     * 每个 appender 绑定一个 {@link DataComponentType}，且
     * {@code registerComponentAppender} 内部用 {@code putIfAbsent} —— 为<b>已被原版占用的类型</b>
     * （如 {@code minecraft:stored_enchantments}）注册会抛 {@code IllegalStateException}。
     *
     * <p>因此这里声明一个<b>不携带任何数据、也不会出现在任何物品上</b>的组件类型，
     * 仅用于在 appender 顺序图中定位。通过
     * {@code registerComponentAppenderAfter(本类型, STORED_ENCHANTMENTS, appender)}
     * 把它挂在原版附魔行之后，从而精确复现原实现「在附魔行之后追加说明」的效果。
     *
     * <p>刻意<b>不加</b> {@code networkSynchronized}：它永远不该被同步。
     * 但 codec 是<b>必须</b>的 —— 26.1 注册数据组件时会校验，缺 codec 会直接抛
     * {@code NullPointerException: Missing Codec for component} 并导致加载崩溃（实测踩到）。
     * 用 {@code MapCodec.unitCodec} 表示「结构上恒为空对象」，与「不携带数据」的语义一致。
     * （注意：{@code Codec.unit(...)} 在 DataFixerUpper 9.x 已移除，改用 {@code MapCodec.unitCodec(...)}。）
     */
    public static DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> ENCHANTMENT_DESCRIPTION_APPENDER = AEA_DATA.register("enchantment_description_appender",
            () -> DataComponentType.<Unit>builder().persistent(MapCodec.unitCodec(Unit.INSTANCE)).build());

}
