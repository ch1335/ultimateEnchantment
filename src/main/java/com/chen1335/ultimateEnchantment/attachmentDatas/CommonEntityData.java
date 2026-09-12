package com.chen1335.ultimateEnchantment.attachmentDatas;

import com.chen1335.ultimateEnchantment.enchantment.enchantments.Tear;
import com.chen1335.ultimateEnchantment.enchantment.enchantments.TheFortress;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class CommonEntityData implements INBTSerializable<CompoundTag> {
    public final Tear.Ticker ticker = new Tear.Ticker();
    public final TheFortress.Handler fortress = new TheFortress.Handler();
    private final Entity owner;

    public CommonEntityData(IAttachmentHolder holder) {
        owner = (Entity) holder;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {

    }

    public void tick() {

        if (owner instanceof LivingEntity living) {
            ticker.tick(living);
            fortress.tick(living);
        }
    }
}
