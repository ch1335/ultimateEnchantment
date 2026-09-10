package com.chen1335.ultimateEnchantment.enchantment.enchantments;

import com.chen1335.ultimateEnchantment.API.AttachmentTypes;
import com.chen1335.ultimateEnchantment.AttachmentDatas.UEProjectileData;
import com.chen1335.ultimateEnchantment.common.Formula;
import com.chen1335.ultimateEnchantment.dataComponentType.UEDataComponentTypes;
import com.chen1335.ultimateEnchantment.enchantment.EnchantmentBasic;
import com.chen1335.ultimateEnchantment.enchantment.UEEnchantments;
import com.chen1335.ultimateEnchantment.mixinsAPI.minecraft.IItemStackMixin;
import com.chen1335.ultimateEnchantment.mixinsAPI.minecraft.IUEEntityExtension;
import com.chen1335.ultimateEnchantment.tags.UEEnchantmentTags;
import com.chen1335.ultimateEnchantment.utils.SimpleSchedule;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import javax.script.SimpleBindings;
import java.util.List;
import java.util.Map;

@EventBusSubscriber()
public class LethalTempo extends EnchantmentBasic {
    public static final Formula DAMAGE_MUL = new Formula("0.2");
    public static final Formula CHANCE_PER_HIT = new Formula("0.2");
    public static final Formula MAX_CHANCE = new Formula("0.4*lvl");
    public static final Formula KEEP_TIME = new Formula("200");

    public LethalTempo() {
        super("lethal_tempo");
        supported_items = new Type.TagType<>(ItemTags.BOW_ENCHANTABLE);
        exclusive_set = new Type.TagType<>(UEEnchantmentTags.ULTIMATE_ENCHANTMENT_EXCLUSIVE);
        max_cost = new Enchantment.Cost(150, 0);
        min_cost = new Enchantment.Cost(80, 0);
        slots = List.of(EquipmentSlotGroup.HAND);
        max_level = 5;
        weight = 3;
    }

    @Override
    protected void registerFormula(Map<String, Formula> formulas) {
        formulas.put("damageMul", DAMAGE_MUL);
        formulas.put("chancePerHit", CHANCE_PER_HIT);
        formulas.put("maxChance", MAX_CHANCE);
        formulas.put("keepTime", KEEP_TIME);
    }

    @Override
    public MutableComponent getDesc(int level) {
        SimpleBindings simpleBindings = buildBindings(level);
        return Component.translatable(getDescId(),
                        LethalTempo.DAMAGE_MUL.toComponent(simpleBindings, 100),
                        LethalTempo.CHANCE_PER_HIT.toComponent(simpleBindings, 100),
                        LethalTempo.MAX_CHANCE.toComponent(simpleBindings, 100),
                        LethalTempo.KEEP_TIME.toComponent(simpleBindings, 0.05F))
                .withStyle(ChatFormatting.LIGHT_PURPLE);
    }

    @SubscribeEvent
    public static void onShoot(LivingEntityUseItemEvent.Stop event) {

        ItemStack itemStack = event.getItem();
        int lvl = UEEnchantments.LETHAL_TEMPO.getEnchantmentLevel(itemStack, event.getEntity().level());
        if (lvl > 0) {
            SimpleBindings bindings = LethalTempo.buildBindings(lvl);
            IItemStackMixin iItemStackMixin = (IItemStackMixin) (Object) itemStack;
            IUEEntityExtension iEntityMixin = (IUEEntityExtension) event.getEntity();
            float additionShootChance = itemStack.getOrDefault(UEDataComponentTypes.ADDITION_SHOOT_CHANCE, 0).floatValue();
            long timeRecord = itemStack.getOrDefault(UEDataComponentTypes.LETHAL_TEMPO_TIME_RECORD, 0L);
            if (event.getEntity().level().getGameTime() - timeRecord > LethalTempo.KEEP_TIME.calculate(bindings)) {
                itemStack.set(UEDataComponentTypes.ADDITION_SHOOT_CHANCE, 0F);
                return;
            }
            int additionShootCount = (int) Math.floor(additionShootChance);
            if (event.getEntity().getRandom().nextFloat() < (additionShootChance - additionShootCount)) {
                additionShootCount++;
            }

            for (int i = 0; i < additionShootCount; i++) {
                SimpleSchedule.addSchedule(event.getEntity().level(), new SimpleSchedule.Wait(() -> {
                    iItemStackMixin.ue$setLethalTempoShoot(true);
                    iEntityMixin.ue$setLethalTempoShooting(true);
                    itemStack.releaseUsing(event.getEntity().level(), event.getEntity(), event.getDuration());
                    iItemStackMixin.ue$setLethalTempoShoot(false);
                    iEntityMixin.ue$setLethalTempoShooting(false);
                }, 2 * (i + 1)));
            }
        }
    }

    @SubscribeEvent
    public static void onArrowHit(LivingIncomingDamageEvent event) {
        if (event.getSource().getDirectEntity() instanceof Projectile projectile && projectile.getOwner() instanceof LivingEntity living) {
            ItemStack weapon = living.getWeaponItem();
            int lvl = UEEnchantments.LETHAL_TEMPO.getEnchantmentLevel(weapon, event.getEntity().level());
            if (lvl > 0) {
                SimpleBindings bindings = LethalTempo.buildBindings(lvl);
                UEProjectileData data = projectile.getData(AttachmentTypes.PROJECTILE_DATA);

                if (!data.isLethalTempoAdditionArrow) {
                    float oldChance = weapon.getOrDefault(UEDataComponentTypes.ADDITION_SHOOT_CHANCE, 0).floatValue();
                    weapon.set(UEDataComponentTypes.ADDITION_SHOOT_CHANCE, Math.min(LethalTempo.MAX_CHANCE.calculate(bindings), oldChance + LethalTempo.CHANCE_PER_HIT.calculate(bindings)));
                    weapon.set(UEDataComponentTypes.LETHAL_TEMPO_TIME_RECORD, event.getEntity().level().getGameTime());
                }


                if (data.isLethalTempoAdditionArrow) {
                    event.getEntity().invulnerableTime = 0;
                    event.setAmount(event.getAmount() * LethalTempo.DAMAGE_MUL.calculate(bindings));
                }
            }
        }
    }
}
