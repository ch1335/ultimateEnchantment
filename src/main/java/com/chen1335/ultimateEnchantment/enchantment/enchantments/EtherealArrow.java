package com.chen1335.ultimateEnchantment.enchantment.enchantments;

import com.chen1335.ultimateEnchantment.API.AttachmentTypes;
import com.chen1335.ultimateEnchantment.attachmentDatas.UEProjectileData;
import com.chen1335.ultimateEnchantment.common.Formula;
import com.chen1335.ultimateEnchantment.enchantment.EnchantmentBasic;
import com.chen1335.ultimateEnchantment.enchantment.UEEnchantments;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import javax.script.SimpleBindings;
import java.util.List;
import java.util.Map;

@EventBusSubscriber()
public class EtherealArrow extends EnchantmentBasic {
    public static final Formula DAMAGE_PER_BLOCK = new Formula("0.1*lvl");
    public static final Formula MAX_DAMAGE = new Formula("4*lvl");

    public EtherealArrow() {
        super("ethereal_arrow");
        supported_items = new Type.TagType<>(ItemTags.BOW_ENCHANTABLE);
        max_cost = new Enchantment.Cost(90, 10);
        min_cost = new Enchantment.Cost(25, 3);
        slots = List.of(EquipmentSlotGroup.HAND);
        max_level = 5;
        weight = 2;
    }

    @Override
    protected void registerFormula(Map<String, Formula> formulas) {
        formulas.put("damage_per_block", DAMAGE_PER_BLOCK);
        formulas.put("max_damage", MAX_DAMAGE);
    }

    @Override
    public List<MutableComponent> getDesc(int level) {
        SimpleBindings bindings = buildBindings(level);
        return List.of(Component.translatable(getDescId(),
                DAMAGE_PER_BLOCK.toComponent(bindings, 1, 1),
                MAX_DAMAGE.toComponent(bindings, 1, 0)
        ).withStyle(ChatFormatting.GOLD));
    }

    /**
     * 箭一进世界就把发射点记在这支箭上，命中时拿它量直线距离。
     * <p>
     * 这里只记发射点，附魔等级留到命中时从箭自己带着的那把弓上读（见
     * {@link #onArrowHit}）。{@link AbstractArrow#getWeaponItem()} 给的是发射那一刻
     * 弓的副本，所以玩家放完箭再把弓丢掉、换格子或换成别的武器都不影响判定 ——
     * 这也是它比回头读射手主手更准的原因。
     * <p>
     * 客户端也会走到这里，但只是给本地那份 {@code UEProjectileData} 写同样的值，不产生
     * 效果；真正结算伤害的 {@link #onArrowHit} 只在服务端触发。
     */
    @SubscribeEvent
    public static void onProjectileJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof AbstractArrow arrow) || arrow.getOwner() == null) {
            return;
        }
        arrow.getData(AttachmentTypes.PROJECTILE_DATA).etherealArrowOrigin = arrow.position();
    }

    /**
     * 命中时按「发射点到命中点的直线距离」追加伤害，超过上限就取上限。
     * <p>
     * 用直线距离而不是逐 tick 累加的路径长度：箭飞出的是近似抛物线，两者只差在高抛的
     * 弧度上；而累加路径得给每个投射物挂一个每 tick 的钩子，代价和收益不成比例。
     * <p>
     * 只认 {@link AbstractArrow}：附魔挂在弓上，射出的一定是箭，用 {@code Projectile}
     * 会把三叉戟之类也放进来。
     */
    @SubscribeEvent
    public static void onArrowHit(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getDirectEntity() instanceof AbstractArrow arrow)) {
            return;
        }
        ItemStack bow = arrow.getWeaponItem();
        if (bow == null || bow.isEmpty()) {
            return;
        }
        int lvl = UEEnchantments.ETHEREAL_ARROW.getEnchantmentLevel(bow, arrow.level());
        UEProjectileData data = arrow.getData(AttachmentTypes.PROJECTILE_DATA);
        if (lvl <= 0 || data.etherealArrowOrigin == null) {
            return;
        }
        float distance = (float) data.etherealArrowOrigin.distanceTo(arrow.position());
        SimpleBindings bindings = buildBindings(lvl);
        float bonus = Math.min(
                DAMAGE_PER_BLOCK.calculate(bindings) * distance,
                MAX_DAMAGE.calculate(bindings)
        );
        if (bonus > 0.0F) {
            event.setAmount(event.getAmount() + bonus);
        }
    }
}
