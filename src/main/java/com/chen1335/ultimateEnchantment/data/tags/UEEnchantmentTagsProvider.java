package com.chen1335.ultimateEnchantment.data.tags;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.enchantment.enchatments.ApothicEnchantingEnchantments;
import com.chen1335.ultimateEnchantment.enchantment.enchatments.IronsSpellBooksEnchantments;
import com.chen1335.ultimateEnchantment.enchantment.enchatments.UEEnchantments;
import com.chen1335.ultimateEnchantment.tags.UEEnchantmentTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class UEEnchantmentTagsProvider extends EnchantmentTagsProvider {
    public UEEnchantmentTagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, ExistingFileHelper existingFileHelper) {
        super(pOutput, pLookupProvider, UltimateEnchantment.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {

//        tag(UEEnchantmentTags.IGNORE_ULTIMATE);

        tag(EnchantmentTags.TOOLTIP_ORDER)
                .addTag(UEEnchantmentTags.ULTIMATE_ENCHANTMENT)
                .addTag(UEEnchantmentTags.LEGENDARY_ENCHANTMENT)
                .addTag(UEEnchantmentTags.COMMON_ENCHANTMENT);

        tag(EnchantmentTags.TREASURE)
                .addTag(UEEnchantmentTags.ULTIMATE_ENCHANTMENT)
                .addTag(UEEnchantmentTags.LEGENDARY_ENCHANTMENT);

        tag(EnchantmentTags.IN_ENCHANTING_TABLE)
                .addTag(UEEnchantmentTags.TRADEABLE_LEGENDARY_ENCHANTMENT)
                .addOptional(UEEnchantments.VANQUISHER.location());

        tag(EnchantmentTags.DOUBLE_TRADE_PRICE)
                .addTag(UEEnchantmentTags.TRADEABLE_LEGENDARY_ENCHANTMENT);

        tag(EnchantmentTags.TRADEABLE)
                .addTag(UEEnchantmentTags.TRADEABLE_LEGENDARY_ENCHANTMENT);


        tag(EnchantmentTags.ON_RANDOM_LOOT)
                .addTag(UEEnchantmentTags.LEGENDARY_ENCHANTMENT);
        tag(EnchantmentTags.NON_TREASURE)
                .addTag(UEEnchantmentTags.COMMON_ENCHANTMENT);

        tag(UEEnchantmentTags.LIFE_STEAL_ENCHANTMENT)
                .addOptional(UEEnchantments.LIFE_STEAL.location());

        tag(UEEnchantmentTags.ULTIMATE_ENCHANTMENT)
                .addOptional(UEEnchantments.ULTIMATE.location())
                .addOptional(UEEnchantments.LEGEND.location())
                .addOptional(UEEnchantments.VANQUISHER.location())
                .addOptional(UEEnchantments.LAST_STAND.location())
                .addOptional(UEEnchantments.ETERNAL.location())
                .addOptional(UEEnchantments.TEAR.location())
                .addOptional(UEEnchantments.LETHAL_TEMPO.location());

        tag(UEEnchantmentTags.ULTIMATE_ENCHANTMENT_EXCLUSIVE)
                .addTag(UEEnchantmentTags.ULTIMATE_ENCHANTMENT);

        tag(UEEnchantmentTags.LEGENDARY_ENCHANTMENT)
                .add(Enchantments.MENDING)
                .addTag(UEEnchantmentTags.TRADEABLE_LEGENDARY_ENCHANTMENT)
                .addTag(UEEnchantmentTags.UN_TRADEABLE_LEGENDARY_ENCHANTMENT);

        tag(UEEnchantmentTags.TRADEABLE_LEGENDARY_ENCHANTMENT)
                .addOptional(UEEnchantments.CUT_DOWN.location())
                .addOptional(UEEnchantments.OVER_GROW.location())
                .addOptional(UEEnchantments.KINETIC_ENERGY.location())
                .addOptional(UEEnchantments.DOUBLE_HOOK.location())
                .addOptional(UEEnchantments.QUICK_BAIT.location());


        tag(UEEnchantmentTags.UN_TRADEABLE_LEGENDARY_ENCHANTMENT)
                .addOptional(UEEnchantments.THUNDER_BOLT.location());

        tag(UEEnchantmentTags.COMMON_ENCHANTMENT)
                .addOptional(UEEnchantments.LIFE_STEAL.location())
                .addOptional(UEEnchantments.SMELTING.location())
                .addOptional(UEEnchantments.QUICK_LATCH.location());

        tag(UEEnchantmentTags.ENCHANTMENTS)
                .addTag(UEEnchantmentTags.ULTIMATE_ENCHANTMENT)
                .addTag(UEEnchantmentTags.LEGENDARY_ENCHANTMENT)
                .addTag(UEEnchantmentTags.COMMON_ENCHANTMENT);

        tag(UEEnchantmentTags.USE_CUSTOM_COLOR)
                .addTag(UEEnchantmentTags.ULTIMATE_ENCHANTMENT)
                .addTag(UEEnchantmentTags.LEGENDARY_ENCHANTMENT);

        tag(UEEnchantmentTags.END_CITY_LOOTABLE)
                .addOptional(UEEnchantments.TEAR.location())
                .addOptional(UEEnchantments.LETHAL_TEMPO.location())
                .addOptional(UEEnchantments.VANQUISHER.location());

        this.addApothicEnchantingAdditionTag(pProvider);
        this.addIronsSpellBooksTag(pProvider);
    }

    private void addApothicEnchantingAdditionTag(HolderLookup.@NotNull Provider pProvider) {
        tag(EnchantmentTags.IN_ENCHANTING_TABLE)
                .addOptional(ApothicEnchantingEnchantments.TERMINATOR.location());

        tag(UEEnchantmentTags.ULTIMATE_ENCHANTMENT)
                .addOptional(ApothicEnchantingEnchantments.TERMINATOR.location());

        tag(UEEnchantmentTags.TRADEABLE_LEGENDARY_ENCHANTMENT)
                .addOptional(ApothicEnchantingEnchantments.SCABBING.location())
                .addOptional(ApothicEnchantingEnchantments.QUICK_SHOOTING.location());

        tag(UEEnchantmentTags.UE_APOTHIC_ENCHANTING_ADDITION)
                .addOptional(ApothicEnchantingEnchantments.TERMINATOR.location())
                .addOptional(ApothicEnchantingEnchantments.SCABBING.location())
                .addOptional(ApothicEnchantingEnchantments.QUICK_SHOOTING.location());
    }

    private void addIronsSpellBooksTag(HolderLookup.@NotNull Provider pProvider) {
        tag(UEEnchantmentTags.COMMON_ENCHANTMENT)
                .addOptional(IronsSpellBooksEnchantments.HARDENED_MANA.location())
                .addOptional(IronsSpellBooksEnchantments.MANA_STEAL.location())
        ;
    }
}
