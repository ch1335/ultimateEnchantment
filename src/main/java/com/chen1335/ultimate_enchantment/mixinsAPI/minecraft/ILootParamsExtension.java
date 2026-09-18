package com.chen1335.ultimate_enchantment.mixinsAPI.minecraft;

import net.minecraft.util.context.ContextKeySet;

/**
 * 给 {@code LootParams} 附加「它是由哪个参数集创建的」这一信息。
 * <p>
 * 26.1 起原版把 {@code LootContextParamSet} 更名为 {@link ContextKeySet}，
 * 且 {@code LootParams} 不再公开 {@code params} 字段（改为 {@code contextMap()}），
 * {@code ContextMap} 本身也不再记录参数集 —— 因此仍然需要一个外部载体来携带它。
 */
public interface ILootParamsExtension {
    ContextKeySet ue$getParamSet();

    void ue$setParamSet(ContextKeySet paramSet);
}
