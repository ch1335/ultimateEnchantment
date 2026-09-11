package com.chen1335.ultimateEnchantment.mixins.ultimate_enchantment;

import com.chen1335.ultimateEnchantment.migration.UEEnchantmentIdMigration;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

/**
 * 在 codec 反序列化路径上重定向旧附魔 ID，详见 {@link UEEnchantmentIdMigration}。
 * <p>
 * 选择 {@code RegistryOps#getter} 作为注入点是刻意的：它是
 * {@code RegistryFixedCodec#decode}（附魔的物品组件 codec）查注册表的唯一入口，
 * 却<b>不是</b> {@code retrieveGetter} / {@code retrieveRegistryLookup} 的入口，
 * 因此干预面恰好覆盖 NBT/JSON -> Holder 的解析，不波及注册表本身，
 * 也不会破坏依赖 {@code RegistryLookup} 类型的 codec。
 */
@Mixin(RegistryOps.class)
public class RegistryOpsMixin {

    @ModifyReturnValue(method = "getter", at = @At("RETURN"))
    private Optional<HolderGetter<?>> getter(
            Optional<HolderGetter<?>> original,
            ResourceKey<? extends Registry<?>> registryKey
    ) {
        return UEEnchantmentIdMigration.wrap(original, registryKey);
    }
}
