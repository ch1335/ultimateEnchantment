package com.chen1335.ultimateEnchantment.attachmentDatas;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public class UEProjectileData implements INBTSerializable<CompoundTag> {
    public boolean isLethalTempoAdditionArrow = false;

    /**
     * 以太凝矢：发射点坐标，命中时拿它到箭的当前位置量直线距离。
     * <p>
     * 没记录过时是 {@code null} 而不是 {@link Vec3#ZERO} —— 世界坐标本来就可以取到
     * 原点附近，用零向量当「没有」会把它和真的在原点放箭混为一谈。
     */
    public @Nullable Vec3 etherealArrowOrigin = null;

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        if (this.etherealArrowOrigin != null) {
            tag.putDouble("EtherealArrowOriginX", this.etherealArrowOrigin.x);
            tag.putDouble("EtherealArrowOriginY", this.etherealArrowOrigin.y);
            tag.putDouble("EtherealArrowOriginZ", this.etherealArrowOrigin.z);
        }
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        if (nbt.contains("EtherealArrowOriginX")) {
            this.etherealArrowOrigin = new Vec3(
                    nbt.getDouble("EtherealArrowOriginX"),
                    nbt.getDouble("EtherealArrowOriginY"),
                    nbt.getDouble("EtherealArrowOriginZ")
            );
        }
    }
}
