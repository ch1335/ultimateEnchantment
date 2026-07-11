package com.chen1335.ultimateEnchantment.mixinsAPI.minecraft;

import com.chen1335.ultimateEnchantment.enchantment.effectComponents.UEEnchantmentEffectComponents;
import com.chen1335.ultimateEnchantment.utils.ItemEnchantmentHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

public class CommonHookMixinHooks {
    public static void modifyLoot(ResourceLocation lootTableId, ObjectArrayList<ItemStack> generatedLoot, LootContext context, CallbackInfoReturnable<ObjectArrayList<ItemStack>> cir) {
        LootContextParamSet lootContextParamSet = ((ILootParamsExtension) context.params).ue$getParamSet();
        if (lootContextParamSet == LootContextParamSets.FISHING) {
            Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
            ItemStack rod = context.getParamOrNull(LootContextParams.TOOL);
            if (entity != null && rod != null) {
                ItemEnchantmentHelper.runIfItemStackHaveEnchantComponent(rod, UEEnchantmentEffectComponents.DOUBLE_HOOK, (doubleHookComponent, level) -> {
                    if (entity.getRandom().nextFloat() < level * doubleHookComponent.chancePerLevel()) {
                        List<ItemStack> old = List.copyOf(generatedLoot);
                        for (ItemStack itemStack : old) {
                            generatedLoot.add(itemStack.copy());
                        }
                    }
                });
            }
        }
    }
}
