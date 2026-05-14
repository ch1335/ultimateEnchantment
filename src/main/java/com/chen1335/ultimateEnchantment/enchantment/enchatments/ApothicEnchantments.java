package com.chen1335.ultimateEnchantment.enchantment.enchatments;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApothicEnchantments {
    public static final Map<ResourceKey<?>, List<ICondition>> conditions = new HashMap<>();

    public static final ResourceKey<Enchantment> APOTHIC_SLAYER = key("apothic_slayer");

    public static void bootstrap(BootstrapContext<Enchantment> pContext) {
        HolderGetter<Enchantment> enchantmentHolderGetter = pContext.lookup(Registries.ENCHANTMENT);
        HolderGetter<Item> itemHolderGetter = pContext.lookup(Registries.ITEM);


    }

    private static void register(BootstrapContext<Enchantment> pContext, ResourceKey<Enchantment> pKey, Enchantment.Builder pBuilder) {
        conditions.put(pKey, List.of(new ModLoadedCondition("apotheosis")));
        pContext.register(pKey, pBuilder.build(pKey.location()));
    }

    private static ResourceKey<Enchantment> key(String pName) {
        return ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(UltimateEnchantment.MODID, pName));
    }

}
