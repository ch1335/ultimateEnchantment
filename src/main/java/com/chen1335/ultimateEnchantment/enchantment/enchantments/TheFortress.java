package com.chen1335.ultimateEnchantment.enchantment.enchantments;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.common.EnchantmentLookup;
import com.chen1335.ultimateEnchantment.common.Formula;
import com.chen1335.ultimateEnchantment.enchantment.EnchantmentBasic;
import com.chen1335.ultimateEnchantment.enchantment.UEEnchantments;
import com.chen1335.ultimateEnchantment.tags.UEEnchantmentTags;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;

import javax.script.SimpleBindings;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class TheFortress extends EnchantmentBasic {

    /**
     * 注册名。构造函数与 {@link Handler} 共用一份，免得两边各写一遍对不上。
     */
    public static final String NAME = "the_fortress";

    /**
     * 复制多少比例的盔甲属性，由手持武器上的附魔等级决定。
     * <p>
     * 用小数写法（与项目 Formula 惯例一致）：{@code 0.1} 就是 10%，5 级即 50%。
     */
    public static final Formula COPY_RATIO = new Formula("0.1*lvl");

    public TheFortress() {
        super(NAME);
        supported_items = new Type.TagType<>(ItemTags.WEAPON_ENCHANTABLE);
        primary_items = supported_items;
        exclusive_set = new Type.TagType<>(UEEnchantmentTags.ULTIMATE_ENCHANTMENT_EXCLUSIVE);
        max_cost = new Enchantment.Cost(200, 0);
        min_cost = new Enchantment.Cost(200, 0);
        slots = List.of(EquipmentSlotGroup.HAND);
        max_level = 5;
        weight = 1;
    }

    @Override
    protected void registerFormula(Map<String, Formula> formulas) {
        formulas.put("copy_ratio", COPY_RATIO);
    }

    @Override
    public List<MutableComponent> getDesc(int level) {
        SimpleBindings bindings = buildBindings(level);
        List<MutableComponent> desc = new ArrayList<>(2);
        desc.add(Component.translatable(getDescId()).withStyle(ChatFormatting.LIGHT_PURPLE));
        desc.add(Component.translatable(getDescId() + ".ratio", COPY_RATIO.toComponent(bindings, 100))
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        return desc;
    }


    @EventBusSubscriber(modid = UltimateEnchantment.MODID)
    public static class Handler {

        private static final String ID_PREFIX = NAME + "/";

        private static final List<EquipmentSlot> ARMOR_SLOTS =
                List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);

        private static final ResourceKey<Enchantment> KEY =
                ResourceKey.create(Registries.ENCHANTMENT, UltimateEnchantment.id(NAME));

        private static final List<AttributeModifier> MODIFIERS = new ArrayList<>();

        private static final String HAIN_HAND = "ultimate_enchantment:the_fortress/mainhand";
        private static final String OFF_HAND = "ultimate_enchantment:the_fortress/offhand";

        @SubscribeEvent
        public static void LivingEquipmentChangeEvent(LivingEquipmentChangeEvent event) {
            LivingEntity entity = event.getEntity();
            if (!(event.getEntity() instanceof Player player)) return;
            if (player.level().isClientSide) return;
            if (!triggers(event.getSlot())) return;

            if (event.getSlot().equals(EquipmentSlot.MAINHAND)) {
                int toLvl = UEEnchantments.THE_FORTRESS.getEnchantmentLevel(event.getTo(), entity.level());
                if (UEEnchantments.THE_FORTRESS.getEnchantmentLevel(event.getFrom(), entity.level()) > 0) {
                    for (AttributeInstance value : entity.getAttributes().attributes.values()) {
                        for (AttributeModifier modifier : value.getModifiers()) {
                            if (modifier.id().toString().startsWith(HAIN_HAND)) {
                                MODIFIERS.add(modifier);
                            }
                        }
                        for (AttributeModifier attributeModifier : MODIFIERS) {
                            value.removeModifier(attributeModifier);
                        }
                        MODIFIERS.clear();
                    }
                }
                if (toLvl > 0) {
                    for (EquipmentSlot armorSlot : ARMOR_SLOTS) {
                        Multimap<Holder<Attribute>, AttributeModifier> modifier = getModifier(entity, ratioFor(event.getTo()), armorSlot, EquipmentSlot.MAINHAND);
                        entity.getAttributes().addTransientAttributeModifiers(modifier);
                    }
                }
            } else if (event.getSlot().equals(EquipmentSlot.OFFHAND)) {
                int toLvl = UEEnchantments.THE_FORTRESS.getEnchantmentLevel(event.getTo(), entity.level());
                if (UEEnchantments.THE_FORTRESS.getEnchantmentLevel(event.getFrom(), entity.level()) > 0) {
                    for (AttributeInstance value : entity.getAttributes().attributes.values()) {
                        for (AttributeModifier modifier : value.getModifiers()) {
                            if (modifier.id().toString().startsWith(OFF_HAND)) {
                                MODIFIERS.add(modifier);
                            }
                        }
                        for (AttributeModifier attributeModifier : MODIFIERS) {
                            value.removeModifier(attributeModifier);
                        }
                        MODIFIERS.clear();
                    }
                }
                if (toLvl > 0) {
                    for (EquipmentSlot armorSlot : ARMOR_SLOTS) {
                        Multimap<Holder<Attribute>, AttributeModifier> modifier = getModifier(entity, ratioFor(event.getTo()), armorSlot, EquipmentSlot.OFFHAND);
                        entity.getAttributes().addTransientAttributeModifiers(modifier);
                    }
                }
            } else if (event.getSlot().getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                ItemStack from = event.getFrom();
                if (!from.isEmpty()) {
                    from.getAttributeModifiers().forEach(event.getSlot(), (attributeHolder, attributeModifier) -> {
                        AttributeInstance instance = event.getEntity().getAttributes().getInstance(attributeHolder);
                        if (instance != null) {
                            instance.removeModifier(UltimateEnchantment.id("the_fortress/mainhand/" + attributeModifier.id().getPath()));
                            instance.removeModifier(UltimateEnchantment.id("the_fortress/offhand/" + attributeModifier.id().getPath()));
                        }
                    });
                }
                ItemStack eventTo = event.getTo();
                if (!eventTo.isEmpty()) {
                    int mainHandRatio = UEEnchantments.THE_FORTRESS.getEnchantmentLevel(entity.getMainHandItem(), entity.level());
                    int offHandRatio = UEEnchantments.THE_FORTRESS.getEnchantmentLevel(entity.getOffhandItem(), entity.level());
                    Multimap<Holder<Attribute>, AttributeModifier> mainHandModifier = getModifier(entity, mainHandRatio, event.getSlot(), EquipmentSlot.MAINHAND);
                    Multimap<Holder<Attribute>, AttributeModifier> offHandModifier = getModifier(entity, offHandRatio, event.getSlot(), EquipmentSlot.OFFHAND);
                    entity.getAttributes().addTransientAttributeModifiers(mainHandModifier);
                    entity.getAttributes().addTransientAttributeModifiers(offHandModifier);
                }
            }

        }

        private static Multimap<Holder<Attribute>, AttributeModifier> getModifier(LivingEntity entity, float ratio, EquipmentSlot armorSlot, EquipmentSlot handSlot) {
            ItemStack itemBySlot = entity.getItemBySlot(armorSlot);
            if (itemBySlot.isEmpty()) {
                return ImmutableMultimap.of();
            }

            Multimap<Holder<Attribute>, AttributeModifier> multimap = HashMultimap.create();
            itemBySlot.getAttributeModifiers().forEach(armorSlot, (attributeHolder, attributeModifier) -> {
                multimap.put(attributeHolder, new AttributeModifier(
                        UltimateEnchantment.id(ID_PREFIX + handSlot.getName() + "/" + attributeModifier.id().getPath()),
                        attributeModifier.amount() * ratio,
                        attributeModifier.operation()
                ));
            });
            return multimap;
        }



        private static boolean triggers(EquipmentSlot slot) {
            return slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND || slot.isArmor();
        }


        private static float ratioFor(ItemStack stack) {
            HolderLookup.RegistryLookup<Enchantment> lookup = EnchantmentLookup.getOrNull();
            if (lookup == null) return 0.0F;

            int lvl = levelOf(stack, lookup);
            if (lvl <= 0) return 0.0F;

            return COPY_RATIO.calculate(buildBindings(lvl));
        }

        private static int levelOf(ItemStack stack, HolderLookup.RegistryLookup<Enchantment> lookup) {
            return lookup.get(KEY).map(stack::getEnchantmentLevel).orElse(0);
        }
    }

}
