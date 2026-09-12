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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
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
        min_cost = new Enchantment.Cost(50, 10);
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
     * 箭一进世界就把「出发时的状态」钉在这支箭上：当时弓上的附魔等级，以及发射点。
     * <p>
     * 之所以在这里一次性取好、而不是等命中时再回头读射手：箭飞行途中射手完全可能换手
     * 或切物品栏，命中那一刻再去问他要武器，拿到的已经不是射出这支箭的那把弓了。
     * <p>
     * 客户端也会走到这里，但只是给本地那份 {@code UEProjectileData} 写入同样的值，不产生
     * 效果；真正结算伤害的 {@link #onArrowHit} 只在服务端触发。
     * <p>
     * 这里读的是射手主手物品，与 {@code LethalTempo} 取弓的方式一致。
     */
    @SubscribeEvent
    public static void onProjectileJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Projectile projectile)
                || !(projectile.getOwner() instanceof LivingEntity shooter)) {
            return;
        }
        int lvl = UEEnchantments.ETHEREAL_ARROW.getEnchantmentLevel(shooter.getWeaponItem(), shooter.level());
        if (lvl <= 0) {
            return;
        }
        UEProjectileData data = projectile.getData(AttachmentTypes.PROJECTILE_DATA);
        data.etherealArrowLevel = lvl;
        data.etherealArrowOrigin = projectile.position();
    }

    /**
     * 命中时按「发射点到命中点的直线距离」追加伤害，超过上限就取上限。
     * <p>
     * 用直线距离而不是逐 tick 累加的路径长度：箭飞出的是近似抛物线，两者只差在高抛的
     * 弧度上；而累加路径得给每个投射物挂一个每 tick 的钩子，代价和收益不成比例。
     */
    @SubscribeEvent
    public static void onArrowHit(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getDirectEntity() instanceof Projectile projectile)) {
            return;
        }
        UEProjectileData data = projectile.getData(AttachmentTypes.PROJECTILE_DATA);
        if (data.etherealArrowLevel <= 0 || data.etherealArrowOrigin == null) {
            return;
        }
        float distance = (float) data.etherealArrowOrigin.distanceTo(projectile.position());
        SimpleBindings bindings = buildBindings(data.etherealArrowLevel);
        float bonus = Math.min(
                DAMAGE_PER_BLOCK.calculate(bindings) * distance,
                MAX_DAMAGE.calculate(bindings)
        );
        if (bonus > 0.0F) {
            event.setAmount(event.getAmount() + bonus);
        }
    }
}
