package com.chen1335.ultimateEnchantment.mixins.minecraft;

import com.chen1335.ultimateEnchantment.enchantment.enchatments.UEEnchantments;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.common.CommonHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CommonHooks.class, priority = 500)
public abstract class CommonHooksMixin {


    @Inject(method = "modifyLoot(Lnet/minecraft/resources/ResourceLocation;Lit/unimi/dsi/fastutil/objects/ObjectArrayList;Lnet/minecraft/world/level/storage/loot/LootContext;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;", at = @At("RETURN"))
    private static void modifyLoot(ResourceLocation lootTableId, ObjectArrayList<ItemStack> generatedLoot, LootContext context, CallbackInfoReturnable<ObjectArrayList<ItemStack>> cir) {
        if (lootTableId != null && lootTableId.equals(EntityType.CREEPER.getDefaultLootTable().location())) {
            Entity entity = context.getParam(LootContextParams.THIS_ENTITY);
            if (entity instanceof Creeper creeper && creeper.isPowered()) {
                generatedLoot.add(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(creeper.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(UEEnchantments.THUNDER_BOLT), 1)));
            }
        }
    }
}
