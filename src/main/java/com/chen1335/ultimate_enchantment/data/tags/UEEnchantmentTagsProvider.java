package com.chen1335.ultimate_enchantment.data.tags;

import com.chen1335.ultimate_enchantment.UltimateEnchantment;
import com.chen1335.ultimate_enchantment.enchantment.UEEnchantments;
import com.chen1335.ultimate_enchantment.tags.UEEnchantmentTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * 附魔标签的数据生成。
 * <p>
 * 26.1 起 {@link EnchantmentTagsProvider} 改为继承 {@code KeyTagProvider<Enchantment>}，
 * 因此 {@code tag(...)} 返回的是 {@code TagAppender<ResourceKey<Enchantment>, Enchantment>}，
 * 其 {@code add}/{@code addOptional} 接受 {@link ResourceKey} 而非 {@code Identifier} ——
 * 故所有调用点从 {@code getKey()} 改为直接传 {@code getKey()}。
 * <p>
 * 同版本移除了 {@code ExistingFileHelper}，构造器改用带 modId 的变体。
 */
public class UEEnchantmentTagsProvider extends EnchantmentTagsProvider {
    public UEEnchantmentTagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider) {
        super(pOutput, pLookupProvider, UltimateEnchantment.MODID);
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
                .addTag(UEEnchantmentTags.TRADEABLE_LEGENDARY_ENCHANTMENT);

        tag(EnchantmentTags.DOUBLE_TRADE_PRICE)
                .addTag(UEEnchantmentTags.TRADEABLE_LEGENDARY_ENCHANTMENT);

        tag(EnchantmentTags.TRADEABLE)
                .addTag(UEEnchantmentTags.TRADEABLE_LEGENDARY_ENCHANTMENT);


        tag(EnchantmentTags.ON_RANDOM_LOOT)
                .addTag(UEEnchantmentTags.LEGENDARY_ENCHANTMENT);
        tag(EnchantmentTags.NON_TREASURE)
                .addTag(UEEnchantmentTags.COMMON_ENCHANTMENT);

        tag(UEEnchantmentTags.LIFE_STEAL_ENCHANTMENT)
                .addOptional(UEEnchantments.LIFE_STEAL.getKey());

        tag(UEEnchantmentTags.ULTIMATE_ENCHANTMENT)
                .addOptional(UEEnchantments.ULTIMATE.getKey())
                .addOptional(UEEnchantments.LEGEND.getKey())
                .addOptional(UEEnchantments.VANQUISHER.getKey())
                .addOptional(UEEnchantments.LAST_STAND.getKey())
                .addOptional(UEEnchantments.ETERNAL.getKey())
                .addOptional(UEEnchantments.TEAR.getKey())
                .addOptional(UEEnchantments.LETHAL_TEMPO.getKey())
                .addOptional(UEEnchantments.THE_FORTRESS.getKey())
                .addOptional(UEEnchantments.ULTIMATE_SLAYER.getKey());

        tag(UEEnchantmentTags.ULTIMATE_ENCHANTMENT_EXCLUSIVE)
                .addTag(UEEnchantmentTags.ULTIMATE_ENCHANTMENT);

        tag(UEEnchantmentTags.LEGENDARY_ENCHANTMENT)
                .add(Enchantments.MENDING)
                .addTag(UEEnchantmentTags.TRADEABLE_LEGENDARY_ENCHANTMENT)
                .addTag(UEEnchantmentTags.UN_TRADEABLE_LEGENDARY_ENCHANTMENT);

        tag(UEEnchantmentTags.TRADEABLE_LEGENDARY_ENCHANTMENT)
                .addOptional(UEEnchantments.CUT_DOWN.getKey())
                .addOptional(UEEnchantments.OVER_GROW.getKey())
                .addOptional(UEEnchantments.KINETIC_ENERGY.getKey())
                .addOptional(UEEnchantments.DOUBLE_HOOK.getKey())
                .addOptional(UEEnchantments.QUICK_BAIT.getKey())
                .addOptional(UEEnchantments.ETHEREAL_ARROW.getKey())
                .addOptional(UEEnchantments.HURT_DEEPENS.getKey())
                .addOptional(UEEnchantments.HARVEST.getKey());


        tag(UEEnchantmentTags.UN_TRADEABLE_LEGENDARY_ENCHANTMENT)
                .addOptional(UEEnchantments.THUNDER_BOLT.getKey());

        tag(UEEnchantmentTags.COMMON_ENCHANTMENT)
                .addOptional(UEEnchantments.LIFE_STEAL.getKey())
                .addOptional(UEEnchantments.SMELTING.getKey())
                .addOptional(UEEnchantments.QUICK_LATCH.getKey());

        tag(UEEnchantmentTags.ENCHANTMENTS)
                .addTag(UEEnchantmentTags.ULTIMATE_ENCHANTMENT)
                .addTag(UEEnchantmentTags.LEGENDARY_ENCHANTMENT)
                .addTag(UEEnchantmentTags.COMMON_ENCHANTMENT);

        tag(UEEnchantmentTags.USE_CUSTOM_COLOR)
                .addTag(UEEnchantmentTags.ULTIMATE_ENCHANTMENT)
                .addTag(UEEnchantmentTags.LEGENDARY_ENCHANTMENT);

        tag(UEEnchantmentTags.END_CITY_LOOTABLE)
                .addOptional(UEEnchantments.TEAR.getKey())
                .addOptional(UEEnchantments.LETHAL_TEMPO.getKey())
                .addOptional(UEEnchantments.VANQUISHER.getKey());

        this.addApothicEnchantingAdditionTag(pProvider);
    }

    private void addApothicEnchantingAdditionTag(HolderLookup.@NotNull Provider pProvider) {
        tag(EnchantmentTags.IN_ENCHANTING_TABLE);

        tag(UEEnchantmentTags.ULTIMATE_ENCHANTMENT);

        tag(UEEnchantmentTags.TRADEABLE_LEGENDARY_ENCHANTMENT)
                .addOptional(UEEnchantments.SCABBING.getKey())
                .addOptional(UEEnchantments.QUICK_SHOOTING.getKey())
                .addOptional(UEEnchantments.PIERCE_THROUGH.getKey());

        tag(UEEnchantmentTags.UE_APOTHIC_ENCHANTING_ADDITION)
                .addOptional(UEEnchantments.SCABBING.getKey())
                .addOptional(UEEnchantments.QUICK_SHOOTING.getKey())
                .addOptional(UEEnchantments.PIERCE_THROUGH.getKey());
    }
}
