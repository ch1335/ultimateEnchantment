package com.chen1335.ultimateEnchantment.utils;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;


public class Util {

    public static int getAttackedCount(LivingEntity living) {
        CompoundTag UEData = getOrCreateUEData(living.getPersistentData());
        return UEData.getInt("AttackedCount");
    }

    public static void addAttackedCount(LivingEntity living) {
        CompoundTag UEData = getOrCreateUEData(living.getPersistentData());
        UEData.putInt("AttackedCount", UEData.getInt("AttackedCount") + 1);
    }

    public static CompoundTag getOrCreateUEData(CompoundTag compoundTag) {
        if (!compoundTag.contains("UEData")) {
            compoundTag.put("UEData", new CompoundTag());
        }
        return compoundTag.getCompound("UEData");
    }
}
