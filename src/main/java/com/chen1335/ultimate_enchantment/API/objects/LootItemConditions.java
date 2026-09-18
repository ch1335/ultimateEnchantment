package com.chen1335.ultimate_enchantment.API.objects;

import com.chen1335.ultimate_enchantment.UltimateEnchantment;
import com.chen1335.ultimate_enchantment.loot.predicates.CreeperIsPoweredCondition;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 战利品条件注册。
 * <p>
 * 26.1 起 {@code Registries.LOOT_CONDITION_TYPE} 直接存放 {@code MapCodec<? extends LootItemCondition>}，
 * 原先的 {@code LootItemConditionType} 包装类型已被移除。
 */
public class LootItemConditions {
    public static final DeferredRegister<MapCodec<? extends LootItemCondition>> LOOT_ITEM_CONDITION_TYPES =
            DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, UltimateEnchantment.MODID);

    public static final DeferredHolder<MapCodec<? extends LootItemCondition>, MapCodec<CreeperIsPoweredCondition>> CREEPER_IS_POWERED_CONDITION =
            LOOT_ITEM_CONDITION_TYPES.register("creeper_is_powered_condition", () -> CreeperIsPoweredCondition.MAP_CODEC);
}
