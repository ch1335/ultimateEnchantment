package com.chen1335.ultimateEnchantment.loot.predicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.SimpleMapCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class CreeperIsPoweredCondition implements LootItemCondition {
    public static CreeperIsPoweredCondition INSTANCE = new CreeperIsPoweredCondition();
    public static final MapCodec<CreeperIsPoweredCondition> CODEC = SimpleMapCodec.unit(INSTANCE);

    public static final LootItemConditionType LOOT_CONDITION_TYPE = new LootItemConditionType(CODEC);

    @Override
    public LootItemConditionType getType() {
        return LOOT_CONDITION_TYPE;
    }

    @Override
    public boolean test(LootContext context) {
        Entity paramOrNull = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (paramOrNull instanceof Creeper creeper) {
            return creeper.isPowered();
        }
        return false;
    }

    public static LootItemCondition.Builder creeperIsPowered() {
        return () -> INSTANCE;
    }
}
