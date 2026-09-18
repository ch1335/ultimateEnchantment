package com.chen1335.ultimate_enchantment.data.tags;

import com.chen1335.ultimate_enchantment.UltimateEnchantment;
import com.chen1335.ultimate_enchantment.tags.UEItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * 物品标签的数据生成。
 * <p>
 * 26.1 起原版的 {@code net.minecraft.data.tags.ItemTagsProvider} 已被移除
 * （其职责并入 {@code BlockItemTagsProvider}），改用 NeoForge 提供的
 * {@code net.neoforged.neoforge.common.data.ItemTagsProvider}。
 * <p>
 * 新构造器为 {@code (PackOutput, CompletableFuture<HolderLookup.Provider>, String modId)} ——
 * 不再需要 {@code blockTags} 与 {@code ExistingFileHelper}（后者已从 NeoForge 移除）。
 * <p>
 * 基类经 {@code IntrinsicHolderTagsProvider<Item>} 提供
 * {@code tag(TagKey<Item>) -> TagAppender<Item, Item>}，其元素类型是 {@code Item} 实例，
 * 因此 {@code .addTag(...)} 的用法与旧实现保持一致。
 */
public class UEItemTagsProvider extends ItemTagsProvider {
    public UEItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, UltimateEnchantment.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(UEItemTags.WEAPON_TOOLS)
                .addTag(ItemTags.BOW_ENCHANTABLE)
                .addTag(ItemTags.CROSSBOW_ENCHANTABLE)
                .addTag(ItemTags.MELEE_WEAPON_ENCHANTABLE)
                .addTag(ItemTags.AXES)
                // Twilight Forest special weapon: 官方未发布 26.1.2 版本，按移植决策暂时禁用该集成。
                // 原实现为 .addOptionalTag(ItemTagGenerator.BLOCK_AND_CHAIN_ENCHANTABLE)
                // 待官方 26.1.2 版本发布后，恢复该引用（import twilightforest.data.tags.ItemTagGenerator）。
                .addTag(ItemTags.SHARP_WEAPON_ENCHANTABLE)
                .addTag(Tags.Items.RANGED_WEAPON_TOOLS)
                .addTag(Tags.Items.MELEE_WEAPON_TOOLS);
        tag(UEItemTags.RANGE_WEAPON_ENCHANTABLE)
                .addTag(ItemTags.BOW_ENCHANTABLE)
                .addTag(ItemTags.CROSSBOW_ENCHANTABLE);
    }
}
