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

import javax.script.SimpleBindings;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class UltimateSlayer extends EnchantmentBasic {

    public static final Formula DAMAGE_BONUS = new Formula("0.03*lvl");

    public static final Formula LOOT_BONUS = new Formula("0.05*lvl");

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

    /**
     * 对本实体每一件带本附魔的盔甲<b>单独代入公式求值</b>，返回这些结果之和。
     * <p>
     * 刻意不先把等级累加起来再算一次：{@link Formula} 支持 {@code lvl*lvl}、{@code lvl/2}
     * 这类非线性写法，也支持带 {@code return} 的多语句，此时「四件各 I 级各算一遍」与
     * 「合并成 4 级算一遍」结果并不相等。要让每件装备按它自己的等级计贡献，就只能逐件求值。
     * <p>
     * 顺带一提，也不能拿
     * {@code EnchantmentHelper#getEnchantmentLevel(Holder, LivingEntity)} 代替：那个方法
     * 内部是 {@code if (j > i) i = j}，返回的是单件最高等级。
     * <p>
     * {@code lookup} 取自 {@link EnchantmentLookup} 的缓存并供所有槽复用，而不是每次调用
     * 都 {@code lookupOrThrow} —— 后者在 5 个槽的循环里就是 5 次多余的注册表解析。
     * <p>
     * 缓存能这么用是因为 {@code ENCHANTMENT} 属于 {@code RegistryLayer.WORLDGEN}，
     * 不在 {@code /reload} 重建的那一层里；前提与刷新时机见 {@link EnchantmentLookup} 的类注释。
     *
     * @return 各部位贡献之和；没有装备本附魔时返回 {@code 0}
     */
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
