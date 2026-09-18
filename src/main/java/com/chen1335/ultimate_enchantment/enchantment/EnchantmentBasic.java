package com.chen1335.ultimate_enchantment.enchantment;

import com.chen1335.ultimate_enchantment.UltimateEnchantment;
import com.chen1335.ultimate_enchantment.common.EnchantmentLookup;
import com.chen1335.ultimate_enchantment.common.Formula;
import com.chen1335.ultimate_enchantment.common.conditions.EnchantmentEnableCondition;
import com.chen1335.ultimate_enchantment.enchantment.effectComponents.UEEnchantmentEffectComponents;
import com.chen1335.ultimate_enchantment.enchantment.effectComponents.FormulaComponent;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

import javax.annotation.Nullable;
import javax.script.SimpleBindings;
import java.util.*;

public class EnchantmentBasic {
    protected String requiredMod = UltimateEnchantment.MODID;
    private final ResourceKey<Enchantment> key;
    public final Map<String, Formula> formulas = new LinkedHashMap<>();
    protected final Identifier id;
    protected DataComponentMap effects;
    protected int anvil_cost = 1;
    protected int max_level = 1;
    protected final Component description;
    protected Enchantment.Cost max_cost = new Enchantment.Cost(1, 1);
    protected Enchantment.Cost min_cost = new Enchantment.Cost(1, 1);
    protected List<EquipmentSlotGroup> slots = List.of();
    protected Type<Item> supported_items = null;
    protected Type<Item> primary_items = null;
    protected int weight = 1;
    protected Type<Enchantment> exclusive_set = null;
    protected List<ICondition> conditions = new ArrayList<>();
    protected final String descId;


    public EnchantmentBasic(String name) {
        this(name, UltimateEnchantment.MODID);
    }

    protected EnchantmentBasic(String name, String requiredMod) {
        this(UltimateEnchantment.id(name));
        this.requiredMod = requiredMod;
        conditions.add(new ModLoadedCondition(requiredMod));
        conditions.add(new EnchantmentEnableCondition(true));
    }

    public EnchantmentBasic(Identifier id) {
        this.id = id;
        description = Component.translatable("enchantment.%s.%s".formatted(id.getNamespace(), id.getPath()));
        descId = "enchantment.%s.%s.specialDesc".formatted(id.getNamespace(), id.getPath());
        key = ResourceKey.create(Registries.ENCHANTMENT, id);
        DataComponentMap.Builder effectMapBuilder = DataComponentMap.builder();
        registerFormula(formulas);
        if (!formulas.isEmpty()) {
            effectMapBuilder.set(UEEnchantmentEffectComponents.FORMULA, new FormulaComponent(formulas));
        }
        registerEffect(effectMapBuilder);
        effects = effectMapBuilder.build();
    }


    /**
     * 走 {@link EnchantmentLookup} 的缓存 lookup，不再自己从 {@code level} 解析。
     * <p>
     * {@code level} 只在缓存还没填上时兜底：客户端没有世界（主菜单、断开连接后）、
     * 数据生成这类时机 {@link EnchantmentLookup#getOrNull()} 返回 {@code null}，
     * 而调用方显式传进来的 {@code level} 一定是可用的。正常游戏里走不到这条分支。
     * <p>
     * 保留这个重载而不是删掉：调用点有二十来处，且签名是公开 API。
     */
    public int getEnchantmentLevel(ItemStack itemStack, Level level) {
        HolderLookup.RegistryLookup<Enchantment> lookup = EnchantmentLookup.getOrNull();
        if (lookup != null) {
            return getEnchantmentLevel(itemStack, lookup);
        }
        return getEnchantmentLevel(itemStack, level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT));
    }

    public int getEnchantmentLevel(ItemStack itemStack, HolderLookup.RegistryLookup<Enchantment> lookup) {
        Optional<Holder.Reference<Enchantment>> enchantment = getEnchantment(lookup);
        return enchantment.map(itemStack::getEnchantmentLevel).orElse(0);
    }

    public Optional<Holder.Reference<Enchantment>> getEnchantment(HolderLookup.RegistryLookup<Enchantment> lookup) {
        return lookup.get(getKey());
    }

    public static SimpleBindings buildBindings(int lvl) {
        SimpleBindings bindings = new SimpleBindings();
        bindings.put("lvl", lvl);
        return bindings;
    }

    protected void registerFormula(Map<String, Formula> formulas) {

    }

    protected void registerEffect(DataComponentMap.Builder builder) {

    }

    public ResourceKey<Enchantment> getKey() {
        return key;
    }

    public JsonObject toJson() {
        JsonOps ops = JsonOps.INSTANCE;
        JsonObject object = new JsonObject();
        object.addProperty("version", ModList.get().getModFileById(UltimateEnchantment.MODID).versionString());
        object.add("neoforge:conditions", ICondition.LIST_CODEC.encodeStart(ops, conditions).getOrThrow());
        object.addProperty("anvil_cost", anvil_cost);
        object.add("description", ComponentSerialization.CODEC.encodeStart(ops, description).getOrThrow());


        object.add("effects", EnchantmentEffectComponents.CODEC.encodeStart(ops, effects).getOrThrow());

        if (exclusive_set != null) {
            object.add("exclusive_set", Type.ENCODER.encodeStart(ops, exclusive_set).getOrThrow());
        }

        object.add("max_cost", Enchantment.Cost.CODEC.encodeStart(ops, max_cost).getOrThrow());
        object.addProperty("max_level", max_level);
        object.add("min_cost", Enchantment.Cost.CODEC.encodeStart(ops, min_cost).getOrThrow());
        object.add("slots", EquipmentSlotGroup.CODEC.listOf().encodeStart(ops, slots).getOrThrow());
        if (supported_items != null) {
            object.add("supported_items", Type.ENCODER.encodeStart(ops, supported_items).getOrThrow());
        }
        if (primary_items != null) {
            object.add("primary_items", Type.ENCODER.encodeStart(ops, primary_items).getOrThrow());
        }
        object.addProperty("weight", weight);
        return object;
    }

    /**
     * 该附魔在 tooltip 里显示的说明，一行一个组件，按顺序排。
     * <p>
     * 返回列表而不是单个组件是为了能分行：一条 lang 条目里写 {@code \n} 会被 tooltip
     * 当成普通字符，要断行只能靠多个组件。
     * <p>
     * 默认实现只有一行，即 {@link #getDescId()} 对应的 lang 条目。子类要加行就往返回的
     * 列表里多塞几个组件，顺序即显示顺序。
     */
    public List<MutableComponent> getDesc(int level) {
        return List.of(Component.translatable(getDescId()));
    }

    public String getDescId() {
        return descId;
    }

    public Identifier getId() {
        return id;
    }

    public void addModifier(ItemAttributeModifierEvent event, int lvl,@Nullable EquipmentSlot equipmentSlot) {

    }


    public static abstract class Type<S> {
        protected static final Encoder<Type<?>> ENCODER = new Encoder<>() {
            @Override
            public <T> DataResult<T> encode(Type<?> input, DynamicOps<T> ops, T prefix) {
                if (input instanceof TagType<?> tag) {
                    return ops.mergeToPrimitive(prefix, ops.createString("#" + tag.key.location()));
                }
                if (input instanceof ItemType<?> item) {
                    return ops.mergeToList(prefix, item.list.stream().map(key -> ops.createString(key.identifier().toString())).toList());
                }
                return DataResult.error(() -> "Unknown Type subclass: " + input.getClass().getName());
            }
        };


        public static class TagType<S> extends Type<S> {
            private final TagKey<S> key;

            public TagType(TagKey<S> key) {
                this.key = key;
            }
        }

        public static class ItemType<S> extends Type<S> {
            private final List<ResourceKey<S>> list;

            @SafeVarargs
            public ItemType(ResourceKey<S>... items) {
                list = List.of(items);
            }
        }
    }
}
