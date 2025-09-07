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
                .add(UEEnchantments.VANQUISHER);

        tag(EnchantmentTags.DOUBLE_TRADE_PRICE)
                .addTag(UEEnchantmentTags.TRADEABLE_LEGENDARY_ENCHANTMENT);

        tag(EnchantmentTags.TRADEABLE)
                .addTag(UEEnchantmentTags.TRADEABLE_LEGENDARY_ENCHANTMENT);


        tag(EnchantmentTags.ON_RANDOM_LOOT)
                .addTag(UEEnchantmentTags.LEGENDARY_ENCHANTMENT);
        tag(EnchantmentTags.NON_TREASURE)
                .addTag(UEEnchantmentTags.COMMON_ENCHANTMENT);

        tag(UEEnchantmentTags.LIFE_STEAL_ENCHANTMENT).add(
                UEEnchantments.LIFE_STEAL
        );

        tag(UEEnchantmentTags.ULTIMATE_ENCHANTMENT)
                .add(
                        UEEnchantments.ULTIMATE,
                        UEEnchantments.LEGEND,
                        UEEnchantments.VANQUISHER,
                        UEEnchantments.LAST_STAND,
                        UEEnchantments.ETERNAL,
                        UEEnchantments.TEAR,
                        UEEnchantments.LETHAL_TEMPO
                );

        tag(UEEnchantmentTags.ULTIMATE_ENCHANTMENT_EXCLUSIVE)
                .addTag(UEEnchantmentTags.ULTIMATE_ENCHANTMENT);

        tag(UEEnchantmentTags.LEGENDARY_ENCHANTMENT)
                .add(Enchantments.MENDING)
                .addTag(UEEnchantmentTags.TRADEABLE_LEGENDARY_ENCHANTMENT)
                .addTag(UEEnchantmentTags.UN_TRADEABLE_LEGENDARY_ENCHANTMENT);

        tag(UEEnchantmentTags.TRADEABLE_LEGENDARY_ENCHANTMENT).add(
                UEEnchantments.CUT_DOWN,
                UEEnchantments.OVER_GROW,
                UEEnchantments.KINETIC_ENERGY,
                UEEnchantments.DOUBLE_HOOK,
                UEEnchantments.QUICK_BAIT
        );


        tag(UEEnchantmentTags.UN_TRADEABLE_LEGENDARY_ENCHANTMENT).add(
                UEEnchantments.THUNDER_BOLT
        );

        tag(UEEnchantmentTags.COMMON_ENCHANTMENT).add(
                UEEnchantments.LIFE_STEAL,
                UEEnchantments.SMELTING,
                UEEnchantments.QUICK_LATCH);

        tag(UEEnchantmentTags.ENCHANTMENTS)
                .addTag(UEEnchantmentTags.ULTIMATE_ENCHANTMENT)
                .addTag(UEEnchantmentTags.LEGENDARY_ENCHANTMENT)
                .addTag(UEEnchantmentTags.COMMON_ENCHANTMENT);

        tag(UEEnchantmentTags.USE_CUSTOM_COLOR)
                .addTag(UEEnchantmentTags.ULTIMATE_ENCHANTMENT)
                .addTag(UEEnchantmentTags.LEGENDARY_ENCHANTMENT);


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
