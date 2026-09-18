package com.chen1335.ultimate_enchantment.enchantment.enchantments;

import com.chen1335.ultimate_enchantment.UltimateEnchantment;
import com.chen1335.ultimate_enchantment.client.UEClient;
import com.chen1335.ultimate_enchantment.common.EnchantmentLookup;
import com.chen1335.ultimate_enchantment.common.Formula;
import com.chen1335.ultimate_enchantment.enchantment.EnchantmentBasic;
import com.chen1335.ultimate_enchantment.enchantment.UEEnchantments;
import com.chen1335.ultimate_enchantment.tags.UEEnchantmentTags;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
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
import net.neoforged.neoforge.event.AddAttributeTooltipsEvent;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.util.AttributeUtil;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;

import javax.script.SimpleBindings;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class TheFortress extends EnchantmentBasic {
    public static final String NAME = "the_fortress";

    public static final Formula COPY_RATIO = new Formula("0.1*lvl");

    public TheFortress() {
        super(NAME);
        supported_items = new Type.TagType<>(Tags.Items.TOOLS_SHIELD);
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
            if (player.level().isClientSide()) return;
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
                    float mainHandRatio = ratioFor(entity.getMainHandItem());
                    float offHandRatio = ratioFor(entity.getOffhandItem());
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

        /**
         * 汇总四件盔甲在当前比例下会被复制出来的属性。
         * <p>
         * 只给 tooltip 用：调用方拿到的是「如果施加会是什么样」，谁也不会写进玩家属性。
         * id 里的手部前缀在这里没有意义（工具提示不分主副手），传主手只为凑出一个稳定的 id。
         */
        static Multimap<Holder<Attribute>, AttributeModifier> copy(LivingEntity entity, float ratio) {
            Multimap<Holder<Attribute>, AttributeModifier> multimap = HashMultimap.create();
            for (EquipmentSlot armorSlot : ARMOR_SLOTS) {
                multimap.putAll(getModifier(entity, ratio, armorSlot, EquipmentSlot.MAINHAND));
            }
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

    /**
     * 客户端：把复制来的属性追加到 tooltip 上，只显示，不施加。
     * <p>
     * {@link AddAttributeTooltipsEvent} 在原版属性行渲染完之后触发，位置正好接在物品自己的
     * 属性下面。行文本交给 {@link AttributeUtil#applyTextFor} 生成 —— 它和原版共用
     * {@code neoforge.modifier.*} 文案与属性配色，同一属性有多个来源时还会自动合并成一行。
     * <p>
     * 不能再用 {@code ItemAttributeModifierEvent}：{@code ItemStack#getAttributeModifiers}
     * 会 fire 它，而客户端算装备属性时也走这条路，加进去的东西会真的落到玩家身上。
     * <p>
     * 限定 {@link Dist#CLIENT}：要读 {@code Minecraft}，且服务端那份实效已由 {@link Handler} 挂上。
     */
    @EventBusSubscriber(modid = UltimateEnchantment.MODID, value = Dist.CLIENT)
    public static class ClientHandler {

        @SubscribeEvent
        public static void AddAttributeTooltipsEvent(AddAttributeTooltipsEvent event) {
            // 物品把属性藏起来时（HIDE_ATTRIBUTES）就跟着藏。
            if (!event.shouldShow()) return;

            ItemStack stack = event.getStack();
            // 只看物品自己有没有本附魔，不管它在谁手里。
            float ratio = Handler.ratioFor(stack);
            if (ratio <= 0.0F) return;

            // 能复制出什么取决于玩家穿着什么 —— 事件没有玩家上下文，只能这样反查。
            Player player = UEClient.getPlayer();
            if (player == null) return;

            Multimap<Holder<Attribute>, AttributeModifier> modifiers = Handler.copy(player, ratio);
            if (modifiers.isEmpty()) return;

            event.addTooltipLines(Component.empty());
            event.addTooltipLines(Component.translatable("item.modifiers.hand").withStyle(ChatFormatting.GRAY));
            AttributeUtil.applyTextFor(stack, event::addTooltipLines, modifiers, event.getContext());
        }
    }
}
