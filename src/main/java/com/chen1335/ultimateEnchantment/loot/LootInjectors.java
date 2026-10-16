package com.chen1335.ultimateEnchantment.loot;

import com.chen1335.arcanumlib.modules.loot.lootInjector.event.LootInjectorRegisterEvent;
import com.chen1335.arcanumlib.modules.loot.lootInjector.operator.TableOperator;
import com.chen1335.arcanumlib.modules.loot.lootInjector.selector.TableSelector;
import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.config.CommonConfig;
import com.chen1335.ultimateEnchantment.enchantment.UEEnchantments;
import com.chen1335.ultimateEnchantment.loot.predicates.CreeperIsPoweredCondition;
import com.chen1335.ultimateEnchantment.utils.UEEnchantmentHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 战利品表注入。
 * <p>
 * 全部注册在这里，统一走 arcanumlib 的 {@code LootInjector}（由
 * {@code LootInjectorApplier} 在战利品表加载后应用），不再用 NeoForge 的
 * {@code LootTableLoadEvent} —— 后者要在事件里手动往 {@code table.pools} 塞池子，
 * 选择器与操作器都得自己写；这里改成声明式：选表用 {@link TableSelector}，
 * 改动用 {@link TableOperator}。
 * <p>
 * 原版 Boss 与生物的战利品表用 {@link TableSelector.ByEntityType} 选，它内部就是拿
 * {@code EntityType#getDefaultLootTable()} 比对表 ID，与原先直接比
 * {@code ...getDefaultLootTable().location()} 等价。
 */
@EventBusSubscriber(modid = UltimateEnchantment.MODID)
public class LootInjectors {
    @SubscribeEvent
    public static void LootInjectorRegisterEvent(LootInjectorRegisterEvent event) {
        HolderLookup.Provider registries = event.getRegistries();

        if (CommonConfig.willEndCityTreasureLootUltimateEnchant.getAsBoolean()) {
            event.register(UltimateEnchantment.id("end_city_treasure"), new TableSelector.ById(BuiltInLootTables.END_CITY_TREASURE.location()), () -> {
                List<Holder<Enchantment>> holders = new ArrayList<>();
                UEEnchantmentHelper.getEnchantment(registries, UEEnchantments.LETHAL_TEMPO.getKey()).ifPresent(holders::add);
                UEEnchantmentHelper.getEnchantment(registries, UEEnchantments.VANQUISHER.getKey()).ifPresent(holders::add);
                UEEnchantmentHelper.getEnchantment(registries, UEEnchantments.TEAR.getKey()).ifPresent(holders::add);
                UEEnchantmentHelper.getEnchantment(registries, UEEnchantments.ULTIMATE_SLAYER.getKey()).ifPresent(holders::add);
                UEEnchantmentHelper.getEnchantment(registries, UEEnchantments.THE_FORTRESS.getKey()).ifPresent(holders::add);
                LootPool.Builder builder = LootPool.lootPool();
                for (Holder<Enchantment> holder : holders) {
                    LootPoolSingletonContainer.Builder<?> item = LootItem.lootTableItem(Items.ENCHANTED_BOOK);

                    if (holder.is(UEEnchantments.VANQUISHER.getKey())) {
                        item.apply(new SetEnchantmentsFunction.Builder().withEnchantment(holder, ConstantValue.exactly(1))).setWeight(3);
                    } else {
                        item.apply(new SetEnchantmentsFunction.Builder().withEnchantment(holder, UniformGenerator.between(2, 3))).setWeight(10);
                    }

                    builder.add(item).build();
                }
                if (!holders.isEmpty()) {
                    builder.add(EmptyLootItem.emptyItem().setWeight(30));
                    return List.<TableOperator>of(new TableOperator.AddPool(builder.build()));
                }

                return List.of();
            });

        }

        event.register(UltimateEnchantment.id("ender_dragon_ultimate"),
                new TableSelector.ByEntityType(EntityType.ENDER_DRAGON),
                () -> enchantedBookPool(registries, UEEnchantments.ULTIMATE.getKey(),
                        book -> book.apply(enchantedBook(registries, UEEnchantments.ULTIMATE.getKey(), ConstantValue.exactly(2)))
                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0, 0.2F)))));

        event.register(UltimateEnchantment.id("wither_legend"),
                new TableSelector.ByEntityType(EntityType.WITHER),
                () -> enchantedBookPool(registries, UEEnchantments.LEGEND.getKey(),
                        book -> book.apply(enchantedBook(registries, UEEnchantments.LEGEND.getKey(), ConstantValue.exactly(1)))
                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0, 0.2F)))));

        event.register(UltimateEnchantment.id("warden_last_stand"),
                new TableSelector.ByEntityType(EntityType.WARDEN),
                () -> enchantedBookPool(registries, UEEnchantments.LAST_STAND.getKey(),
                        book -> book.apply(enchantedBook(registries, UEEnchantments.LAST_STAND.getKey(), ConstantValue.exactly(1)))
                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0, 0.2F)))));

        // 苦力怕掉「雷霆」，且只在充能状态下 —— 条件挂在条目上，与原先的 .when(...) 等价。
        event.register(UltimateEnchantment.id("creeper_thunder_bolt"),
                new TableSelector.ByEntityType(EntityType.CREEPER),
                () -> enchantedBookPool(registries, UEEnchantments.THUNDER_BOLT.getKey(),
                        book -> book.apply(enchantedBook(registries, UEEnchantments.THUNDER_BOLT.getKey(), ConstantValue.exactly(1)))
                                .when(CreeperIsPoweredCondition.creeperIsPowered())));
    }

    /**
     * 把「给条目挂函数与条件」的配置逻辑收成一次调用：造出附魔书条目、交给 {@code configure}
     * 加工，再单独裹成一个池子追加到目标表。
     * <p>
     * 表与附魔的选取在这里就确定了（{@code enchantment} 只用于生成注入器 ID 的可读性），
     * 真正的注册动作延迟到 arcanumlib 应用注入器时执行。
     */
    private static List<TableOperator> enchantedBookPool(
            HolderLookup.Provider registries,
            ResourceKey<Enchantment> enchantment,
            Consumer<LootPoolSingletonContainer.Builder<?>> configure
    ) {
        LootPoolSingletonContainer.Builder<?> book = LootItem.lootTableItem(Items.ENCHANTED_BOOK);
        configure.accept(book);
        return List.of(new TableOperator.AddPool(LootPool.lootPool().add(book).build()));
    }

    /** 固定等级（或数量区间）的附魔书函数。 */
    private static SetEnchantmentsFunction enchantedBook(
            HolderLookup.Provider registries,
            ResourceKey<Enchantment> enchantment,
            net.minecraft.world.level.storage.loot.providers.number.NumberProvider count
    ) {
        return new SetEnchantmentsFunction.Builder()
                .withEnchantment(registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantment), count)
                .build();
    }
}
