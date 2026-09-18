package com.chen1335.ultimate_enchantment.mixinsAPI.minecraft;

import com.chen1335.ultimate_enchantment.enchantment.UEEnchantments;
import javax.script.SimpleBindings;

import com.chen1335.ultimate_enchantment.enchantment.enchantments.DoubleHook;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

public class CommonHookMixinHooks {
    public static void modifyLoot(Identifier lootTableId, ObjectArrayList<ItemStack> generatedLoot, LootContext context, CallbackInfoReturnable<ObjectArrayList<ItemStack>> cir) {
        // 26.1：LootContextParamSet 更名为 ContextKeySet；取参方法 getParamOrNull → getOptionalParameter。
        // 参数集仍由挂在本模组 LootParamsMixin 上的扩展字段承载：原版的 LootParams 构造器只收
        // ContextMap（不含参数集），所以必须由 LootParams.Builder.create 注入时记录下来。
        // context.params 是 private 字段，靠 accesstransformer.cfg 的
        // "public net.minecraft.world.level.storage.loot.LootContext params" 提升可见性。
        ContextKeySet lootContextParamSet = ((ILootParamsExtension) context.params).ue$getParamSet();
        if (lootContextParamSet == LootContextParamSets.FISHING) {
            Entity entity = context.getOptionalParameter(LootContextParams.THIS_ENTITY);
            ItemStack rod = (ItemStack) context.getOptionalParameter(LootContextParams.TOOL);
            if (entity != null && rod != null) {
                int level = UEEnchantments.DOUBLE_HOOK.getEnchantmentLevel(rod, entity.level());
                if (level > 0) {
                    SimpleBindings bindings = UEEnchantments.buildBindings(level);
                    float chance = DoubleHook.CHANCE.calculate( bindings);
                    if (entity.getRandom().nextFloat() < chance) {
                        List<ItemStack> old = List.copyOf(generatedLoot);
                        for (ItemStack itemStack : old) {
                            generatedLoot.add(itemStack.copy());
                        }
                    }
                }
            }
        }
    }
}
