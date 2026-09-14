package com.chen1335.ultimateEnchantment.enchantment.enchantments;

import com.chen1335.ultimateEnchantment.common.EnchantmentLookup;
import com.chen1335.ultimateEnchantment.common.Formula;
import com.chen1335.ultimateEnchantment.enchantment.EnchantmentBasic;
import com.chen1335.ultimateEnchantment.enchantment.UEEnchantments;
import com.chen1335.ultimateEnchantment.tags.UEEnchantmentTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.Tags;

import javax.script.SimpleBindings;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class UltimateSlayer extends EnchantmentBasic {

    public static final Formula DAMAGE_BONUS = new Formula("0.03*lvl");

    public static final Formula LOOT_BONUS = new Formula("0.05*lvl");

    private static final String APOTH_BOSS_KEY = "apoth.boss";

    public UltimateSlayer() {
        super("ultimate_slayer");
        supported_items = new Type.TagType<>(ItemTags.ARMOR_ENCHANTABLE);
        primary_items = supported_items;
        exclusive_set = new Type.TagType<>(UEEnchantmentTags.ULTIMATE_ENCHANTMENT_EXCLUSIVE);
        max_cost = new Enchantment.Cost(200, 0);
        min_cost = new Enchantment.Cost(200, 0);
        slots = List.of(EquipmentSlotGroup.ARMOR);
        max_level = 5;
        weight = 1;
    }


    @Override
    protected void registerFormula(Map<String, Formula> formulas) {
        formulas.put("damage_bonus", DAMAGE_BONUS);
        formulas.put("loot_bonus", LOOT_BONUS);
    }

    public static float getRatio(LivingEntity thisEntity, LivingEntity killer) {
        if (!canApply(thisEntity)) {
            return 0;
        }
        return ratioFor(killer);
    }

    public static float sumPerSlot(LivingEntity entity, Formula formula) {
        HolderLookup.RegistryLookup<Enchantment> lookup = EnchantmentLookup.get();
        float total = 0.0F;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.isArmor()) {
                int lvl = UEEnchantments.ULTIMATE_SLAYER.getEnchantmentLevel(entity.getItemBySlot(slot), lookup);
                if (lvl > 0) {
                    total += formula.calculate(buildBindings(lvl));
                }
            }
        }
        return total;
    }

    public static float ratioFor(LivingEntity killer) {
        float ratio = UltimateSlayer.sumPerSlot(killer, UltimateSlayer.LOOT_BONUS);
        // 非有限值理论上到不了这里：公式求值会把 Infinity/NaN 转成异常并回滚（见 Formula#calculate）。
        // 仍然挡一道，因为下面的整数部分是 Mth.floor 出来的循环上界，拿到 Infinity 就是死循环。
        if (!Float.isFinite(ratio)) {
            return 0.0F;
        }
        return ratio;
    }

    public static boolean canApply(LivingEntity entity) {
        boolean isBoss = entity.getType().is(Tags.EntityTypes.BOSSES)
                || entity.getPersistentData().getBoolean(APOTH_BOSS_KEY);

        return isBoss;
    }

    @Override
    public List<MutableComponent> getDesc(int level) {
        SimpleBindings bindings = buildBindings(level);
        List<MutableComponent> desc = new ArrayList<>(4);
        desc.add(Component.translatable(getDescId()).withStyle(ChatFormatting.LIGHT_PURPLE));
        desc.add(Component.translatable(getDescId() + ".damage", DAMAGE_BONUS.toComponent(bindings, 100)).withStyle(ChatFormatting.LIGHT_PURPLE));
        desc.add(Component.translatable(getDescId() + ".loot", LOOT_BONUS.toComponent(bindings, 100)).withStyle(ChatFormatting.LIGHT_PURPLE));
        if (ModList.get().isLoaded("apotheosis")) {
            // 神化的 Boss 用的是它自己那套标签，不带原版 boss 标签，本附魔认不出来，
            // 所以没装神化时这行不显示 —— 说了反而是误导。
            desc.add(Component.translatable(getDescId() + ".apotheosis").withStyle(ChatFormatting.GRAY));
        }
        return desc;
    }
}
