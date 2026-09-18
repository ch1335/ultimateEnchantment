package com.chen1335.ultimate_enchantment.data.tags;

import com.chen1335.ultimate_enchantment.API.UEDamageTypeTags;
import com.chen1335.ultimate_enchantment.UltimateEnchantment;
import com.chen1335.ultimate_enchantment.data.registries.UEDamageType;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * 26.1 起 {@code ExistingFileHelper} 已从 NeoForge 移除，Provider 构造器改用带 modId 的变体。
 */
public class DamageTypeTagProvider extends DamageTypeTagsProvider {
    public DamageTypeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, UltimateEnchantment.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(UEDamageTypeTags.IS_ATTACK)
                .add(DamageTypes.MOB_ATTACK)
                .add(UEDamageType.TEAR_DAMAGE)
                .addTag(DamageTypeTags.IS_PLAYER_ATTACK);

        tag(DamageTypeTags.NO_KNOCKBACK)
                .add(UEDamageType.TEAR_DAMAGE);

        tag(Tags.DamageTypes.IS_PHYSICAL)
                .add(UEDamageType.TEAR_DAMAGE);


        tag(DamageTypeTags.PANIC_CAUSES)
                .add(UEDamageType.TEAR_DAMAGE);

        tag(DamageTypeTags.BYPASSES_COOLDOWN)
                .add(UEDamageType.TEAR_DAMAGE);

    }
}
