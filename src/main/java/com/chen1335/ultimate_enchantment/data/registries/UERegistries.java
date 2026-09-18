package com.chen1335.ultimate_enchantment.data.registries;

import com.chen1335.ultimate_enchantment.UltimateEnchantment;
import com.chen1335.ultimate_enchantment.data.lootTable.UELootTableProvider;
import com.chen1335.ultimate_enchantment.data.lootTableModifier.EntityLootModifierProvider;
import com.chen1335.ultimate_enchantment.data.recipe.UERecipesProvider;
import com.chen1335.ultimate_enchantment.data.tags.DamageTypeTagProvider;
import com.chen1335.ultimate_enchantment.data.tags.UEEnchantmentTagsProvider;
import com.chen1335.ultimate_enchantment.data.tags.UEItemTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@EventBusSubscriber(modid = UltimateEnchantment.MODID)
public class UERegistries {

    private static final Map<ResourceKey<?>, List<ICondition>> conditions = new HashMap<>();

    // 26.1：GatherDataEvent 是抽象类（仅有 Server / Client 两个子类），
    // 事件总线拒绝为抽象类注册监听器（运行时抛 IllegalArgumentException）。
    // 本模组的数据生成全是服务端产物，故监听其 Server 子类。
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Server event) {
        DataGenerator generator = event.getGenerator();

        DatapackBuiltinEntriesProvider ueLookupProvider = generator.addProvider(true, new DatapackBuiltinEntriesProvider(
                generator.getPackOutput(),
                event.getLookupProvider(),
                new RegistrySetBuilder()
                        .add(Registries.DAMAGE_TYPE, UEDamageType::bootstrapContext)
                ,
                conditions,
                Set.of(UltimateEnchantment.MODID)
        ));

        // 26.1 起 GatherDataEvent 不再提供 getExistingFileHelper() 与 includeServer()：
        // 所有 Provider 构造器都去掉了 ExistingFileHelper 参数，addProvider 也只需单参
        // （内部等价于原先的 includeServer=true）。
        generator.addProvider(true, new UEEnchantmentTagsProvider(
                generator.getPackOutput(),
                ueLookupProvider.getRegistryProvider()
        ));

        generator.addProvider(true, new DamageTypeTagProvider(
                generator.getPackOutput(),
                ueLookupProvider.getRegistryProvider()
        ));

        BlockTagsProvider blockTagsProvider = new BlockTagsProvider(generator.getPackOutput(), ueLookupProvider.getRegistryProvider(), UltimateEnchantment.MODID) {
            @Override
            protected void addTags(HolderLookup.@NotNull Provider provider) {

            }
        };

        generator.addProvider(true, blockTagsProvider);

        generator.addProvider(true, new UEItemTagsProvider(
                generator.getPackOutput(),
                ueLookupProvider.getRegistryProvider()
        ));

        generator.addProvider(true, new EntityLootModifierProvider(
                generator.getPackOutput(),
                ueLookupProvider.getRegistryProvider(),
                UltimateEnchantment.MODID
        ));

        // 配方：26.1 起 RecipeProvider 已不是 DataProvider，注册的是其 Runner。
        generator.addProvider(true, new UERecipesProvider.Runner(
                generator.getPackOutput(),
                event.getLookupProvider()
        ));

        generator.addProvider(true, new UELootTableProvider(
                generator.getPackOutput(),
                ueLookupProvider.getRegistryProvider()));

        generator.addProvider(true, new UESoundDefinitionsProvider(
                generator.getPackOutput()));
    }
}
