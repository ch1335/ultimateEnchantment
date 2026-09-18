package com.chen1335.ultimate_enchantment.attachmentDatas;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import org.jetbrains.annotations.Nullable;

public class UEProjectileData implements ValueIOSerializable {
    public boolean isLethalTempoAdditionArrow = false;

    /**
     * 以太凝矢：发射点坐标，命中时拿它到箭的当前位置量直线距离。
     * <p>
     * 没记录过时是 {@code null} 而不是 {@link Vec3#ZERO} —— 世界坐标本来就可以取到
     * 原点附近，用零向量当「没有」会把它和真的在原点放箭混为一谈。
     */
    public @Nullable Vec3 etherealArrowOrigin = null;

    @Override
    public void serialize(ValueOutput output) {
        if (this.etherealArrowOrigin != null) {
            output.putDouble("EtherealArrowOriginX", this.etherealArrowOrigin.x);
            output.putDouble("EtherealArrowOriginY", this.etherealArrowOrigin.y);
            output.putDouble("EtherealArrowOriginZ", this.etherealArrowOrigin.z);
        }
    }

    @Override
    public void deserialize(ValueInput input) {
        // 与旧实现等价：以 X 键是否存在作为「记录过发射点」的判据。
        // ValueInput 没有 contains，也没有 getDouble(name)（只有 getDoubleOr），
        // 但 getString(name) 返回 Optional，可用它探测键是否存在。
        if (input.getString("EtherealArrowOriginX").isEmpty()) {
            return;
        }
        double x = input.getDoubleOr("EtherealArrowOriginX", 0.0D);
        double y = input.getDoubleOr("EtherealArrowOriginY", 0.0D);
        double z = input.getDoubleOr("EtherealArrowOriginZ", 0.0D);
        this.etherealArrowOrigin = new Vec3(x, y, z);
    }
}
