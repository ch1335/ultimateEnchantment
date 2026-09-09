package com.chen1335.ultimateEnchantment.dynamicDataPack;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.google.gson.JsonElement;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;

/**
 * 动态数据包管理器
 * <p>
 * 基于 {@link DynamicPackContents} 实现的运行时数据包系统。
 * 通过 {@link AddPackFindersEvent} 将数据包注入到 Minecraft 的资源系统中，
 * 允许在运行时动态生成配方、标签等数据。
 * </p>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 添加一个动态配方
 * JsonObject recipe = new JsonObject();
 * recipe.addProperty("type", "minecraft:crafting_shaped");
 * // ... 填充配方数据
 * DynamicDataPackManager.addData(
 *     ResourceLocation.fromNamespaceAndPath("my_mod", "recipes/my_recipe.json"),
 *     recipe
 * );
 *
 * // 添加原始字节数据
 * DynamicDataPackManager.addData(
 *     ResourceLocation.fromNamespaceAndPath("my_mod", "tags/items/my_tag.json"),
 *     tagJsonString.getBytes(StandardCharsets.UTF_8)
 * );
 * }</pre>
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@EventBusSubscriber(modid = UltimateEnchantment.MODID)
public class DynamicDataPackManager {
    private static final DynamicPackContents CONTENTS = new DynamicPackContents();
    private static final DynamicPackResources RESOURCES = new DynamicPackResources(
            UltimateEnchantment.MODID + "_dynamic_data", CONTENTS
    );

    private static final Pack.ResourcesSupplier SUPPLIER = new Pack.ResourcesSupplier() {
        @Override
        public PackResources openPrimary(PackLocationInfo location) {
            return RESOURCES;
        }

        @Override
        public PackResources openFull(PackLocationInfo location, Pack.Metadata metadata) {
            return RESOURCES;
        }
    };

    /**
     * 获取底层的 {@link DynamicPackContents}，用于更细粒度的操作
     */
    public static DynamicPackContents getContents() {
        return CONTENTS;
    }

    /**
     * 获取包装后的 {@link DynamicPackResources}
     */
    public static DynamicPackResources getResources() {
        return RESOURCES;
    }

    /**
     * 添加原始字节数据
     *
     * @param location 资源位置，路径应相对于 data/&lt;namespace&gt;/ 目录
     *                 例如：{@code ResourceLocation.fromNamespaceAndPath("mymod", "recipes/test.json")}
     * @param data     资源内容
     */
    public static void addData(ResourceLocation location, byte[] data) {
        CONTENTS.addToData(location, data);
        RESOURCES.addNamespace(location.getNamespace());
    }

    /**
     * 添加 JSON 数据
     *
     * @param location 资源位置
     * @param json     JSON 数据，会自动序列化为 UTF-8 字节
     */
    public static void addData(ResourceLocation location, JsonElement json) {
        addData(location, json.toString().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 清除所有动态数据
     */
    public static void clearData() {
        CONTENTS.clearData();
    }

    private static final Path ENCHANTMENTS_DIRECTORY = FMLPaths.CONFIGDIR.get()
            .resolve(UltimateEnchantment.MODID)
            .resolve("enchantments");

    private static void loadConfiguredEnchantments() {
        try {
            Files.createDirectories(ENCHANTMENTS_DIRECTORY);
        } catch (IOException exception) {
            UltimateEnchantment.LOGGER.error(
                    "无法创建附魔配置目录 {}",
                    ENCHANTMENTS_DIRECTORY,
                    exception
            );
            return;
        }

        try (var paths = Files.list(ENCHANTMENTS_DIRECTORY)) {
            paths.filter(path -> Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS))
                    .filter(path -> path.getFileName().toString().endsWith(".json"))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .forEach(DynamicDataPackManager::loadConfiguredEnchantment);
        } catch (IOException exception) {
            UltimateEnchantment.LOGGER.error(
                    "无法读取附魔配置目录 {}",
                    ENCHANTMENTS_DIRECTORY,
                    exception
            );
        }
    }

    private static void loadConfiguredEnchantment(Path path) {
        String fileName = path.getFileName().toString();
        try {
            addData(
                    UltimateEnchantment.id("enchantment/" + fileName),
                    Files.readAllBytes(path)
            );
        } catch (IllegalArgumentException exception) {
            UltimateEnchantment.LOGGER.warn(
                    "跳过非法附魔配置文件名 {}",
                    fileName,
                    exception
            );
        } catch (IOException exception) {
            UltimateEnchantment.LOGGER.warn(
                    "无法读取附魔配置文件 {}",
                    path,
                    exception
            );
        }
    }

    /**
     * 将动态数据包注册到 Minecraft 的资源系统中
     */
    @SubscribeEvent
    public static void onAddPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.SERVER_DATA || event.isTrusted()) {
            return;
        }

        clearData();
        loadConfiguredEnchantments();
        Pack pack = Pack.readMetaAndCreate(
                RESOURCES.location(),
                SUPPLIER,
                PackType.SERVER_DATA,
                new PackSelectionConfig(true, Pack.Position.TOP, true)
        );

        if (pack != null) {
            event.addRepositorySource(packConsumer -> packConsumer.accept(pack));
        }
    }
}
