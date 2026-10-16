package com.chen1335.ultimateEnchantment.loot;

import com.chen1335.arcanumlib.modules.loot.lootInjector.event.LootInjectorRegisterEvent;
import com.chen1335.arcanumlib.modules.loot.lootInjector.operator.TableOperator;
import com.chen1335.arcanumlib.modules.loot.lootInjector.selector.TableSelector;
import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.config.CommonConfig;
import com.chen1335.ultimateEnchantment.enchantment.UEEnchantments;
import com.chen1335.ultimateEnchantment.loot.predicates.CreeperIsPoweredCondition;
import com.chen1335.ultimateEnchantment.utils.UEEnchantmentHelper;
import net.minecraft.Util;
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
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.ArrayList;
import java.util.List;

/**
 * 战利品表注入。
 * <p>
 * 全部注册在这里，统一走 arcanumlib 的 {@code LootInjector}（由
 * {@code LootInjectorApplier} 在战利品表加载后应用），不再用 NeoForge 的
 * {@code LootTableLoadEvent} —— 后者要在事件里手动往 {@code table.pools} 塞池子；
 * 这里改成声明式：选表用 {@link TableSelector}，改动用 {@link TableOperator}。
 * <p>
 * 原版 Boss 与生物的表用 {@link TableSelector.ByEntityType} 选，它内部就是拿
 * {@code EntityType#getDefaultLootTable()} 比对表 ID，与原先直接比
 * {@code ...getDefaultLootTable().location()} 等价。
 */
@EventBusSubscriber(modid = UltimateEnchantment.MODID)
public class LootInjectors {
    @SubscribeEvent
    public static void LootInjectorRegisterEvent(LootInjectorRegisterEvent event) {
        HolderLookup.Provider registries = event.getRegistries();
        HolderLookup.RegistryLookup<Enchantment> enchantmentLookup = registries.lookupOrThrow(Registries.ENCHANTMENT);
        if (CommonConfig.willEndCityTreasureLootUltimateEnchant.getAsBoolean()) {
            event.register(UltimateEnchantment.id("end_city_treasure"), new TableSelector.ById(BuiltInLootTables.END_CITY_TREASURE.location()), Util.make(() -> {
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
                    return List.of(new TableOperator.AddPool(builder.build()));
                }

                return List.of();
            }));

        }

        // 末影龙掉「终极」。
        event.register(UltimateEnchantment.id("ender_dragon_ultimate"),
                new TableSelector.ByEntityType(EntityType.ENDER_DRAGON),
                Util.make(() -> bossEnchantedBook(registries, enchantmentLookup, UEEnchantments.ULTIMATE.getKey(), ConstantValue.exactly(2))));

        // 凋灵掉「传奇」。
        event.register(UltimateEnchantment.id("wither_legend"),
                new TableSelector.ByEntityType(EntityType.WITHER),
                Util.make(() -> bossEnchantedBook(registries, enchantmentLookup, UEEnchantments.LEGEND.getKey(), ConstantValue.exactly(1))));

        // 监守者掉「背水一战」。
        event.register(UltimateEnchantment.id("warden_last_stand"),
                new TableSelector.ByEntityType(EntityType.WARDEN),
                Util.make(() -> bossEnchantedBook(registries, enchantmentLookup, UEEnchantments.LAST_STAND.getKey(), ConstantValue.exactly(1))));

        // 苦力怕掉「雷霆」，且只在充能状态下 —— 条件挂在条目上，与原先的 .when(...) 等价。
        event.register(UltimateEnchantment.id("creeper_thunder_bolt"),
                new TableSelector.ByEntityType(EntityType.CREEPER),
                Util.make(() -> {
                    LootPoolSingletonContainer.Builder<?> item = LootItem.lootTableItem(Items.ENCHANTED_BOOK);
                    item.apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchantmentLookup.getOrThrow(UEEnchantments.THUNDER_BOLT.getKey()), ConstantValue.exactly(1)))
                            .when(CreeperIsPoweredCondition.creeperIsPowered());
                    return List.of(new TableOperator.AddPool(LootPool.lootPool().add(item).build()));
                }));
    }

    /**
     * 原版 Boss 的统一掉落形态：一本固定等级的附魔书，数量再乘上抢夺加成。
     * <p>
     * {@code EnchantedCountIncreaseFunction.lootingMultiplier} 让掉落数量随抢夺等级提高，
     * 与原先 {@code LootTableLoadEvent} 里的写法一致。
     * <p>
     * 附魔缺席时 {@code getOrThrow} 会抛异常 —— 与原先 {@code ifPresent} 的净效果一致：
     * 那个附魔不存在就什么都不掉。异常由 {@code LootInjectorApplier} 逐表捕获并记日志，
     * 不会让整轮注入失败。
     */
    private static List<TableOperator> bossEnchantedBook(
            HolderLookup.Provider registries,
            HolderLookup.RegistryLookup<Enchantment> enchantmentLookup,
            ResourceKey<Enchantment> enchantment,
            NumberProvider levelCount
    ) {
        LootPoolSingletonContainer.Builder<?> item = LootItem.lootTableItem(Items.ENCHANTED_BOOK);
        item.apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchantmentLookup.getOrThrow(enchantment), levelCount))
                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0, 0.2F)));
        return List.of(new TableOperator.AddPool(LootPool.lootPool().add(item).build()));
    }
}
