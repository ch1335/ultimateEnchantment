package com.chen1335.ultimateEnchantment.mixinsAPI.minecraft;

import com.chen1335.ultimateEnchantment.config.CommonConfig;
import com.chen1335.ultimateEnchantment.enchantment.enchatments.UEEnchantments;
import com.chen1335.ultimateEnchantment.utils.UEEnchantmentHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

public class CommonHookMixinHooks {
    public static void modifyLoot(ResourceLocation lootTableId, ObjectArrayList<ItemStack> generatedLoot, LootContext context, CallbackInfoReturnable<ObjectArrayList<ItemStack>> cir) {
        if (lootTableId != null) {
            if (lootTableId.equals(EntityType.CREEPER.getDefaultLootTable().location())) {
                Entity entity = context.getParam(LootContextParams.THIS_ENTITY);
                if (entity instanceof Creeper creeper && creeper.isPowered()) {
                    UEEnchantmentHelper.getEnchantment(UEEnchantments.THUNDER_BOLT).ifPresent(holder -> {
                        generatedLoot.add(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(holder, 1)));

                    });
                }
            } else if (lootTableId.equals(BuiltInLootTables.END_CITY_TREASURE.location()) && CommonConfig.willEndCityTreasureLootUltimateEnchant) {
                int level = UniformGenerator.between(1, 2).getInt(context);

                if (UniformGenerator.between(0, 100).getFloat(context) < 50) {
                    List<Holder<Enchantment>> holders = new ArrayList<>();
                    UEEnchantmentHelper.getEnchantment(UEEnchantments.LETHAL_TEMPO).ifPresent(holders::add);
                    UEEnchantmentHelper.getEnchantment(UEEnchantments.VANQUISHER).ifPresent(holders::add);
                    UEEnchantmentHelper.getEnchantment(UEEnchantments.TEAR).ifPresent(holders::add);
                    if (!holders.isEmpty()) {
                        int i = UniformGenerator.between(0, holders.size() - 1).getInt(context);
                        Holder<Enchantment> holder = holders.get(i);
                        generatedLoot.add(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(holder, level)));

                    }
                }


            }
        }
    }
}
