package com.chen1335.ultimateEnchantment.mixins.minecraft;

import com.chen1335.ultimateEnchantment.mixinsAPI.minecraft.CommonHookMixinHooks;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.neoforged.neoforge.common.CommonHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CommonHooks.class, priority = 500)
public abstract class CommonHooksMixin {


    @Inject(method = "modifyLoot(Lnet/minecraft/resources/ResourceLocation;Lit/unimi/dsi/fastutil/objects/ObjectArrayList;Lnet/minecraft/world/level/storage/loot/LootContext;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;", at = @At("RETURN"))
    private static void modifyLoot(ResourceLocation lootTableId, ObjectArrayList<ItemStack> generatedLoot, LootContext context, CallbackInfoReturnable<ObjectArrayList<ItemStack>> cir) {
        CommonHookMixinHooks.modifyLoot(lootTableId, generatedLoot, context, cir);
    }
}
