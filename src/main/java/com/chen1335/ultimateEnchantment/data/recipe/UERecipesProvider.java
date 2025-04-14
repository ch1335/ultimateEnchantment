package com.chen1335.ultimateEnchantment.data.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.packs.VanillaRecipeProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class UERecipesProvider extends VanillaRecipeProvider {


    public UERecipesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);

    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput recipeOutput, HolderLookup.@NotNull Provider holderLookup) {
//        Holder<Enchantment> eternal = holderLookup.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(UEEnchantments.ETERNAL);
//        Holder<Enchantment> mending = holderLookup.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.MENDING);
//
//        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
//        mutable.upgrade(mending, 1);
//        DataComponentMap dataComponents = DataComponentMap.EMPTY;
//        dataComponents.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
//
//
//
//        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EnchantedBookItem.createForEnchantment(new EnchantmentInstance(mending, 1)))
//                .define('B', Items.BOOK)
//                .define('T', Items.TOTEM_OF_UNDYING)
//                .define('N', Items.NETHERITE_INGOT)
//                .define('M', DataComponentIngredient.of(false, DataComponents.ENCHANTMENTS, mutable.toImmutable(), Items.ENCHANTED_BOOK))
//                .define('D', Items.DIAMOND_BLOCK)
//                .pattern("DTD")
//                .pattern("MBM")
//                .pattern("DND")
//                .unlockedBy("has_totem_of_undying", has(Items.TOTEM_OF_UNDYING))
//                .showNotification(false)
//                .save(recipeOutput);
    }
}
