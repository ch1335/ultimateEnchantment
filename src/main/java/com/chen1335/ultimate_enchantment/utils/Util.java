package com.chen1335.ultimate_enchantment.utils;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;


public class Util {

    public static int getAttackedCount(LivingEntity living) {
        CompoundTag UEData = getOrCreateUEData(living.getPersistentData());
        return UEData.getIntOr("AttackedCount", 0);
    }

    public static void addAttackedCount(LivingEntity living) {
        CompoundTag UEData = getOrCreateUEData(living.getPersistentData());
        UEData.putInt("AttackedCount", UEData.getIntOr("AttackedCount", 0) + 1);
    }

    public static CompoundTag getOrCreateUEData(CompoundTag compoundTag) {
        if (!compoundTag.contains("UEData")) {
            compoundTag.put("UEData", new CompoundTag());
        }
        return compoundTag.getCompoundOrEmpty("UEData");
    }
}
