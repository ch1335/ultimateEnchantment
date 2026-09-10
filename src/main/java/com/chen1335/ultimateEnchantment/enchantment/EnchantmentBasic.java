package com.chen1335.ultimateEnchantment.enchantment;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.common.Arg;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantmentBasic {
    private final List<ICondition> conditions = new ArrayList<>();

    public final Map<String, Arg> args = new HashMap<>();
    protected final ResourceLocation id;

    protected int anvil_cost = 1;
    protected int max_level = 1;
    protected final Component component;
    protected Enchantment.Cost max_cost = new Enchantment.Cost(1, 1);
    protected Enchantment.Cost min_cost = new Enchantment.Cost(1, 1);
    protected List<EquipmentSlotGroup> slots = List.of();
    protected Supported supported_items = null;

    public EnchantmentBasic(String name) {
        this(UltimateEnchantment.id(name));
    }

    public EnchantmentBasic(ResourceLocation id) {
        this.id = id;
        component = Component.translatable("enchantment.%s.%s".formatted(id.getNamespace(), id.getPath()));
    }

    protected void registerArg(String name, Arg arg) {

    }

    public ResourceKey<Enchantment> createKey() {
        return ResourceKey.create(Registries.ENCHANTMENT, id);
    }

    protected static class Supported {
        protected static class Tag {
            private final TagKey<Enchantment> key;

            protected Tag(TagKey<Enchantment> key) {
                this.key = key;
            }
        }

        protected static class Item {
            private final List<ResourceKey<Enchantment>> list;

            protected Item(ResourceKey<Enchantment>... items) {
                list = List.of(items);
            }
        }
    }
}
