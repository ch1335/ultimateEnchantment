package com.chen1335.ultimate_enchantment.dynamicDataPack;

import com.chen1335.ultimate_enchantment.UltimateEnchantment;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.AddPackFindersEvent;
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
 *     Identifier.fromNamespaceAndPath("my_mod", "recipes/my_recipe.json"),
 *     recipe
 * );
 *
 * // 添加原始字节数据
 * DynamicDataPackManager.addData(
 *     Identifier.fromNamespaceAndPath("my_mod", "tags/items/my_tag.json"),
 *     tagJsonString.getBytes(StandardCharsets.UTF_8)
 * );
 * }</pre>
 */
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
     * 添加 JSON 数据。
     *
     * <p>这是本管理器<b>推荐且主要</b>的入口：数据以 {@link JsonElement} 形式存放，
     * 到读取时才序列化为 UTF-8 字节，因此动态包内容始终是可检视的结构化 JSON。
     *
     * @param location 资源位置，路径相对于 {@code data/<namespace>/}，
     *                 例如 {@code Identifier.fromNamespaceAndPath("mymod", "enchantment/test.json")}
     * @param json     JSON 数据
     */
    public static void addData(Identifier location, JsonElement json) {
        CONTENTS.addToData(location, json);
        RESOURCES.addNamespace(location.getNamespace());
    }

    /**
     * 添加原始字节数据（非 JSON 资源，如二进制文件）。
     *
     * @param location 资源位置
     * @param data     资源内容
     */
    public static void addData(Identifier location, byte[] data) {
        CONTENTS.addToData(location, data);
        RESOURCES.addNamespace(location.getNamespace());
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

    private static int loadedEnchantmentCount;

    private static void loadConfiguredEnchantments() {
        loadedEnchantmentCount = 0;
        try {
            Files.createDirectories(ENCHANTMENTS_DIRECTORY);
        } catch (IOException exception) {
            UltimateEnchantment.LOGGER.error(
                    "Failed to create enchantment config directory {}",
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
                    "Failed to list enchantment config directory {}",
                    ENCHANTMENTS_DIRECTORY,
                    exception
            );
        }
    }

    private static void loadConfiguredEnchantment(Path path) {
        String fileName = path.getFileName().toString();
        try {
            // 解析为 JsonElement 而非直接读字节：既让数据以结构化形式存放（本次改造的要点），
            // 也能在这里就发现「文件不是合法 JSON」这类问题，并给出带文件名的明确报错，
            // 而不是等到 Minecraft 的 codec 阶段才失败（那样只会得到一条难以定位的通用错误）。
            JsonElement json = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8));
            addData(UltimateEnchantment.id("enchantment/" + fileName), json);
            loadedEnchantmentCount++;
        } catch (RuntimeException exception) {
            // 文件名不是合法 Identifier 路径时（大写、空格、中文都算），UltimateEnchantment.id 抛的是
            // IdentifierException，它直接继承 RuntimeException 而**不是** IllegalArgumentException，
            // 所以这里必须接 RuntimeException。之前接 IllegalArgumentException 是接不住的，异常会冒出
            // AddPackFindersEvent，而事件总线记完日志仍会 rethrow，最终把服务器启动整个搞失败。
            // JsonParseException 同样继承 RuntimeException，也由这里兜住。
            UltimateEnchantment.LOGGER.warn(
                    "Skipping enchantment config file (invalid name or malformed JSON) {}",
                    fileName,
                    exception
            );
        } catch (IOException exception) {
            UltimateEnchantment.LOGGER.warn(
                    "Failed to read enchantment config file {}",
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
                // 26.1：readMetaAndCreate 的首参是 PackLocationInfo（不再是 Identifier），
                // 直接取本包已有的 location() 即可。
                RESOURCES.location(),
                SUPPLIER,
                PackType.SERVER_DATA,
                new PackSelectionConfig(true, Pack.Position.TOP, true)
        );

        if (pack != null) {
            // 这条 INFO 是刻意保留的运行期可观测性：动态包的加载此前是完全静默的，
            // 一旦元数据不被接受，附魔等数据会无声消失、只表现为「游戏里读不到附魔」，
            // 极难定位。此处至少在启动日志里留下「包已建立 + 载入了多少条数据」的证据。
            UltimateEnchantment.LOGGER.info(
                    "Dynamic data pack '{}' created with {} enchantment file(s) from {}",
                    RESOURCES.location().id(), loadedEnchantmentCount, ENCHANTMENTS_DIRECTORY
            );
            event.addRepositorySource(packConsumer -> packConsumer.accept(pack));
        } else {
            // 走到这里说明本包缺少可用的 pack.mcmeta —— readMetaAndCreate 会返回 null，
            // 动态包不会被加入资源系统，附魔等数据会全部滞留在内存里且不报错。
            // 详见 DynamicPackResources#getRootResource 的注释。
            UltimateEnchantment.LOGGER.error(
                    "Failed to create the dynamic data pack '{}': pack.mcmeta metadata was rejected. "
                            + "Dynamic data (enchants, etc.) will NOT be available in game.",
                    RESOURCES.location().id()
            );
        }
    }
}
