package com.chen1335.ultimate_enchantment.attachmentDatas;

import com.chen1335.ultimate_enchantment.enchantment.enchantments.Tear;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

public class CommonEntityData implements ValueIOSerializable {
    public final Tear.Ticker ticker = new Tear.Ticker();
    private final Entity owner;

    public CommonEntityData(IAttachmentHolder holder) {
        owner = (Entity) holder;
    }

    // 本类目前没有需要持久化的字段（原有实现同样写入空 NBT），故两个方法保持空实现。
    @Override
    public void serialize(ValueOutput output) {
    }

    @Override
    public void deserialize(ValueInput input) {
    }

    public void tick() {

        if (owner instanceof LivingEntity living) {
            ticker.tick(living);
        }
    }
}
