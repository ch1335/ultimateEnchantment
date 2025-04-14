package com.chen1335.ultimateEnchantment.enchantment.enchatments;

import com.chen.simpleRPGCore.common.conditions.ManaSystemEnableCondition;
import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IronsSpellBooksEnchantments {
    public static final Map<ResourceKey<?>, List<ICondition>> conditions = new HashMap<>();

    public static final ResourceKey<Enchantment> HARDENED_MANA = key("hardened_mana");

    public static void bootstrap(BootstrapContext<Enchantment> pContext) {
        HolderGetter<Enchantment> enchantmentHolderGetter = pContext.lookup(Registries.ENCHANTMENT);
        HolderGetter<Item> itemHolderGetter = pContext.lookup(Registries.ITEM);

        register(
                pContext,
                HARDENED_MANA,
                Enchantment.enchantment(
                        Enchantment.definition(
                                itemHolderGetter.getOrThrow(ItemTags.CHEST_ARMOR_ENCHANTABLE),
                                3,
                                4,
                                Enchantment.dynamicCost(15, 8),
                                Enchantment.dynamicCost(50, 10),
                                1
                        )
                )
        );
    }

    private static void register(BootstrapContext<Enchantment> pContext, ResourceKey<Enchantment> pKey, Enchantment.Builder pBuilder) {
        ArrayList<ICondition> arrayList = new ArrayList<>();
        arrayList.add(new ModLoadedCondition(IronsSpellbooks.MODID));
        conditions.put(pKey, arrayList);
        pContext.register(pKey, pBuilder.build(pKey.location()));
    }

    private static ResourceKey<Enchantment> key(String pName) {
        return ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(UltimateEnchantment.MODID, pName));
    }
}
