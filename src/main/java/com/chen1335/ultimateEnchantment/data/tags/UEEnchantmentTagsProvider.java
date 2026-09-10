package com.chen1335.ultimateEnchantment.data.tags;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.data.registries.enchatments.ApothicEnchantingEnchantments;
import com.chen1335.ultimateEnchantment.data.registries.enchatments.IronsSpellBooksEnchantments;
import com.chen1335.ultimateEnchantment.data.registries.enchatments.UEEnchantmentsDataGen;
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
                .addOptional(UEEnchantmentsDataGen.VANQUISHER.location());

        tag(EnchantmentTags.DOUBLE_TRADE_PRICE)
                .addTag(UEEnchantmentTags.TRADEABLE_LEGENDARY_ENCHANTMENT);

        tag(EnchantmentTags.TRADEABLE)
                .addTag(UEEnchantmentTags.TRADEABLE_LEGENDARY_ENCHANTMENT);


        tag(EnchantmentTags.ON_RANDOM_LOOT)
                .addTag(UEEnchantmentTags.LEGENDARY_ENCHANTMENT);
        tag(EnchantmentTags.NON_TREASURE)
                .addTag(UEEnchantmentTags.COMMON_ENCHANTMENT);

        tag(UEEnchantmentTags.LIFE_STEAL_ENCHANTMENT)
                .addOptional(UEEnchantmentsDataGen.LIFE_STEAL.location());

        tag(UEEnchantmentTags.ULTIMATE_ENCHANTMENT)
                .addOptional(UEEnchantmentsDataGen.ULTIMATE.location())
                .addOptional(UEEnchantmentsDataGen.LEGEND.location())
                .addOptional(UEEnchantmentsDataGen.VANQUISHER.location())
                .addOptional(UEEnchantmentsDataGen.LAST_STAND.location())
                .addOptional(UEEnchantmentsDataGen.ETERNAL.location())
                .addOptional(UEEnchantmentsDataGen.TEAR.location())
                .addOptional(UEEnchantmentsDataGen.LETHAL_TEMPO.location());

        tag(UEEnchantmentTags.ULTIMATE_ENCHANTMENT_EXCLUSIVE)
                .addTag(UEEnchantmentTags.ULTIMATE_ENCHANTMENT);

        tag(UEEnchantmentTags.LEGENDARY_ENCHANTMENT)
                .add(Enchantments.MENDING)
                .addTag(UEEnchantmentTags.TRADEABLE_LEGENDARY_ENCHANTMENT)
                .addTag(UEEnchantmentTags.UN_TRADEABLE_LEGENDARY_ENCHANTMENT);

        tag(UEEnchantmentTags.TRADEABLE_LEGENDARY_ENCHANTMENT)
                .addOptional(UEEnchantmentsDataGen.CUT_DOWN.location())
                .addOptional(UEEnchantmentsDataGen.OVER_GROW.location())
                .addOptional(UEEnchantmentsDataGen.KINETIC_ENERGY.location())
                .addOptional(UEEnchantmentsDataGen.DOUBLE_HOOK.location())
                .addOptional(UEEnchantmentsDataGen.QUICK_BAIT.location());


        tag(UEEnchantmentTags.UN_TRADEABLE_LEGENDARY_ENCHANTMENT)
                .addOptional(UEEnchantmentsDataGen.THUNDER_BOLT.location());

        tag(UEEnchantmentTags.COMMON_ENCHANTMENT)
                .addOptional(UEEnchantmentsDataGen.LIFE_STEAL.location())
                .addOptional(UEEnchantmentsDataGen.SMELTING.location())
                .addOptional(UEEnchantmentsDataGen.QUICK_LATCH.location());

        tag(UEEnchantmentTags.ENCHANTMENTS)
                .addTag(UEEnchantmentTags.ULTIMATE_ENCHANTMENT)
                .addTag(UEEnchantmentTags.LEGENDARY_ENCHANTMENT)
                .addTag(UEEnchantmentTags.COMMON_ENCHANTMENT);

        tag(UEEnchantmentTags.USE_CUSTOM_COLOR)
                .addTag(UEEnchantmentTags.ULTIMATE_ENCHANTMENT)
                .addTag(UEEnchantmentTags.LEGENDARY_ENCHANTMENT);

        tag(UEEnchantmentTags.END_CITY_LOOTABLE)
                .addOptional(UEEnchantmentsDataGen.TEAR.location())
                .addOptional(UEEnchantmentsDataGen.LETHAL_TEMPO.location())
                .addOptional(UEEnchantmentsDataGen.VANQUISHER.location());

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
