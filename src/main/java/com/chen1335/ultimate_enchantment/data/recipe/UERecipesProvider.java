package com.chen1335.ultimate_enchantment.data.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.concurrent.CompletableFuture;

/**
 * 配方数据生成。
 * <p>
 * 26.1 对数据生成做了两段式重构：{@link RecipeProvider} 本身<b>不再是 {@code DataProvider}</b>，
 * 构造器变为 {@code (HolderLookup.Provider registries, RecipeOutput output)}，子类只负责实现
 * {@link #buildRecipes()}；真正可被 {@code DataGenerator} 接受的是
 * {@link RecipeProvider.Runner}，它通过抽象的 {@code createRecipeProvider(registries, output)}
 * 创建实现类并驱动 {@code buildRecipes()}。
 * <p>
 * 因此本类改为「实现类 + 嵌套 Runner」的形态，注册时使用 {@link Runner}。
 * <p>
 * 原有配方内容在本类中<b>整体处于注释状态</b>（原实现的 {@code buildRecipes} 体全被注释），
 * 故本次改造只调整结构、不涉及配方逻辑。将来要加配方时，直接写在此 {@code buildRecipes()} 内。
 */
public class UERecipesProvider extends RecipeProvider {

    public UERecipesProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        // 新增配方写在这里。参考 RecipeProvider 提供的 oreSmelting / netheriteSmithing 等辅助方法，
        // 以及 ShapedRecipeBuilder / ShapelessRecipeBuilder。
    }

    /** {@code DataProvider} 包装，注册到 {@code DataGenerator} 的是这个。 */
    public static class Runner extends RecipeProvider.Runner {

        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new UERecipesProvider(registries, output);
        }

        @Override
        public String getName() {
            return "Ultimate Enchantment Recipes";
        }
    }
}
