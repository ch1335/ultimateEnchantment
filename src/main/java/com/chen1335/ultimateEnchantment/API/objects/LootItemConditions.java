package com.chen1335.ultimateEnchantment.API.objects;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.loot.predicates.CreeperIsPoweredCondition;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LootItemConditions {
    public static final DeferredRegister<LootItemConditionType> LOOT_ITEM_CONDITION_TYPES = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, UltimateEnchantment.MODID);

    public static final DeferredHolder<LootItemConditionType, LootItemConditionType> CREEPER_IS_POWERED_CONDITION = LOOT_ITEM_CONDITION_TYPES.register("creeper_is_powered_condition", () -> CreeperIsPoweredCondition.LOOT_CONDITION_TYPE);
}
