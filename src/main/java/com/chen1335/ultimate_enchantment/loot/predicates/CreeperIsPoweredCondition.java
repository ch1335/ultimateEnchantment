package com.chen1335.ultimate_enchantment.loot.predicates;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jspecify.annotations.NonNull;

/**
 * 「苦力怕已充能」战利品条件。
 * <p>
 * 26.1 起战利品条件注册表直接存放 {@link MapCodec}，{@code LootItemConditionType} 已移除，
 * 接口方法也从 {@code getType()} 改为 {@code codec()}。因此本类不再自建类型对象，
 * 由 {@code API/objects/LootItemConditions} 直接把 {@link #MAP_CODEC} 注册进注册表。
 */
public class CreeperIsPoweredCondition implements LootItemCondition {
    public static final CreeperIsPoweredCondition INSTANCE = new CreeperIsPoweredCondition();
    public static final MapCodec<CreeperIsPoweredCondition> MAP_CODEC = MapCodec.unit(INSTANCE);

    @Override
    public @NonNull MapCodec<CreeperIsPoweredCondition> codec() {
        return MAP_CODEC;
    }

    @Override
    public boolean test(LootContext context) {
        Entity entity = context.getOptionalParameter(LootContextParams.THIS_ENTITY);
        if (entity instanceof Creeper creeper) {
            return creeper.isPowered();
        }
        return false;
    }

    public static LootItemCondition.Builder creeperIsPowered() {
        return () -> INSTANCE;
    }
}
