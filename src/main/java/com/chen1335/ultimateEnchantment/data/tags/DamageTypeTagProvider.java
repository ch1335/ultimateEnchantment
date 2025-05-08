package com.chen1335.ultimateEnchantment.data.tags;

import com.chen1335.ultimateEnchantment.API.UEDamageTypeTags;
import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.data.registries.UEDamageType;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class DamageTypeTagProvider extends DamageTypeTagsProvider {
    public DamageTypeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, UltimateEnchantment.MODID, existingFileHelper);
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
