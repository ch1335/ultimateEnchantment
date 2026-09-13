package com.chen1335.ultimateEnchantment.enchantment.enchantments;

import com.chen1335.ultimateEnchantment.common.Formula;
import com.chen1335.ultimateEnchantment.enchantment.EnchantmentBasic;
import com.chen1335.ultimateEnchantment.enchantment.UEEnchantments;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import javax.script.SimpleBindings;
import java.util.List;
import java.util.Map;

@EventBusSubscriber()
public class HurtDeepens extends EnchantmentBasic {
    public static final Formula DAMAGE_PER_PERCENT = new Formula("0.01");

    public static final Formula MAX_BONUS = new Formula("0.1*lvl");

    public HurtDeepens() {
        super("hurt_deepens");
        supported_items = new Type.TagType<>(ItemTags.BOW_ENCHANTABLE);
        max_cost = new Enchantment.Cost(90, 10);
        min_cost = new Enchantment.Cost(20, 5);
        slots = List.of(EquipmentSlotGroup.HAND);
        max_level = 4;
        weight = 2;
    }

    @Override
    protected void registerFormula(Map<String, Formula> formulas) {
        formulas.put("damage_per_percent", DAMAGE_PER_PERCENT);
        formulas.put("max_bonus", MAX_BONUS);
    }

    @Override
    public List<MutableComponent> getDesc(int level) {
        SimpleBindings bindings = buildBindings(level);
        return List.of(Component.translatable(getDescId(),
                DAMAGE_PER_PERCENT.toComponent(bindings, 100, 1),
                MAX_BONUS.toComponent(bindings, 100, 1)
        ).withStyle(ChatFormatting.GOLD));
    }

    /**
     * 箭命中时按双方「当前生命值百分比」的差距加伤：目标每比射手低 1 个百分点，就多打
     * {@link #DAMAGE_PER_PERCENT} 比例的伤害，封顶 {@link #MAX_BONUS}。
     * <p>
     * 比的是百分比而不是绝对血量，所以满血射手打残血目标，和残血射手打残血目标不是一回事。
     * 目标血量百分比更高时差值为负，此时不加伤 —— 这个附魔只在对方更惨的时候生效。
     * <p>
     * 弓从箭身上取（{@link AbstractArrow#getWeaponItem()}），而不是回头读射手主手：
     * 那是发射那一刻弓的副本，射手放完箭再换手也不影响判定。
     * <p>
     * 只认 {@link AbstractArrow}：附魔挂在弓上，射出的一定是箭。
     */
    @SubscribeEvent
    public static void onArrowHit(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getDirectEntity() instanceof AbstractArrow arrow)) {
            return;
        }
        if (!(arrow.getOwner() instanceof LivingEntity attacker)) {
            return;
        }
        ItemStack bow = arrow.getWeaponItem();
        if (bow == null || bow.isEmpty()) {
            return;
        }
        int lvl = UEEnchantments.HURT_DEEPENS.getEnchantmentLevel(bow, arrow.level());
        if (lvl <= 0) {
            return;
        }
        LivingEntity target = event.getEntity();
        float attackerPercent = attacker.getHealth() / attacker.getMaxHealth();
        float targetPercent = target.getHealth() / target.getMaxHealth();
        // 差值是 0~1 的比例，乘 100 换成「个百分点」再乘每点的加成比例
        float diffPoints = (attackerPercent - targetPercent) * 100.0F;
        SimpleBindings bindings = buildBindings(lvl);
        float bonus = Math.min(
                diffPoints * DAMAGE_PER_PERCENT.calculate(bindings),
                MAX_BONUS.calculate(bindings)
        );
        if (bonus > 0.0F) {
            event.setAmount(event.getAmount() * (1.0F + bonus));
        }
    }
}
