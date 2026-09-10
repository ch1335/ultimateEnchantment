package com.chen1335.ultimateEnchantment.enchantment;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.common.Formula;
import com.chen1335.ultimateEnchantment.enchantment.effectComponents.UEEnchantmentEffectComponents;
import com.chen1335.ultimateEnchantment.enchantment.effectComponents.UltimateEnchantment.FormulaComponent;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

import javax.script.SimpleBindings;
import java.util.*;

public class EnchantmentBasic {
    protected String requiredMod = UltimateEnchantment.MODID;
    private final ResourceKey<Enchantment> key;
    public final Map<String, Formula> formulas = new LinkedHashMap<>();
    protected final ResourceLocation id;
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
    }

    public EnchantmentBasic(ResourceLocation id) {
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

    public int getEnchantmentLevel(ItemStack itemStack, Level level) {
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

    public MutableComponent getDesc(int level) {
        return Component.translatable(getDescId());
    }

    public String getDescId() {
        return descId;
    }

    public ResourceLocation getId() {
        return id;
    }


    public static abstract class Type<S> {
        protected static final Encoder<Type<?>> ENCODER = new Encoder<>() {
            @Override
            public <T> DataResult<T> encode(Type<?> input, DynamicOps<T> ops, T prefix) {
                if (input instanceof TagType<?> tag) {
                    return ops.mergeToPrimitive(prefix, ops.createString("#" + tag.key.location()));
                }
                if (input instanceof ItemType<?> item) {
                    return ops.mergeToList(prefix, item.list.stream().map(key -> ops.createString(key.location().toString())).toList());
                }
                return DataResult.error(() -> "未知的 Type 子类: " + input.getClass().getName());
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

    private record CapturedEnchantment(Optional<Holder.Reference<Enchantment>> optional) {
        public static final CapturedEnchantment EMPTY = new CapturedEnchantment(Optional.empty());
    }
}
