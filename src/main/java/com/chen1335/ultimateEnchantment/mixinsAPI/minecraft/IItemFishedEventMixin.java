package com.chen1335.ultimateEnchantment.mixinsAPI.minecraft;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IItemFishedEventMixin {
    List<ItemStack> getUe$originalDrops();
}
