package com.chen1335.ultimateEnchantment.enchantment.specialEnchantEffects;

import com.chen1335.ultimateEnchantment.dataComponentType.UEDataComponentTypes;
import com.chen1335.ultimateEnchantment.enchantment.enchatments.UEEnchantments;
import com.chen1335.ultimateEnchantment.utils.ItemEnchantmentHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FishingEffect {
    public static void retrieve(ItemStack rod, @Nullable Player entity, FishingHook hookEntity, List<ItemStack> drops) {
        if (entity == null) {
            return;
        }
        rod.set(UEDataComponentTypes.ITEM_FISHED_COUNT, rod.getOrDefault(UEDataComponentTypes.ITEM_FISHED_COUNT, 0));
        int doubleHook = ItemEnchantmentHelper.getEnchantmentLevel(rod, UEEnchantments.DOUBLE_HOOK);
        if (entity.getRandom().nextFloat() < doubleHook * 0.05) {
            List<ItemStack> old = List.copyOf(drops);
            for (ItemStack itemStack : old) {
                drops.add(itemStack.copy());
            }
        }
    }
}
