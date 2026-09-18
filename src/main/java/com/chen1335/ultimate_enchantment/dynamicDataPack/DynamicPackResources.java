package com.chen1335.ultimate_enchantment.dynamicDataPack;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.JsonOps;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.InclusiveRange;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * 运行时动态数据包（见 {@link DynamicDataPackManager}）。
 *
 * <p><b>26.1 迁移说明</b>：本类原先直接实现 {@code PackResources} 并覆写
 * {@code getMetadataSection(MetadataSectionSerializer)}。26.1 上这两处都变了：
 * <ul>
 *   <li>{@code MetadataSectionSerializer} 已移除，改为 {@code MetadataSectionType}；</li>
 *   <li>{@code getMetadataSection} 已从 {@code PackResources} 接口<b>下移到</b>
 *       {@link AbstractPackResources} 实现类，接口本身不再声明它。</li>
 * </ul>
 * 因此改为继承 {@link AbstractPackResources} —— 它已按新机制实现元数据读取
 * （从 {@code pack.mcmeta} 根资源解析，本类 {@code getRootResource} 返回 null 即视为无元数据）。
 * 本类只需覆写资源的读取与枚举三项。
 *
 * <p>另外 {@code PackMetadataSection} 现为 record
 * {@code (Component description, InclusiveRange<PackFormat> supportedFormats)}，
 * 且 {@code WorldVersion#getPackVersion(PackType)} 已改名为 {@code packVersion(PackType)}
 * 并返回 {@code PackFormat}。
 */
public class DynamicPackResources extends AbstractPackResources {
    private final String packId;
    private final PackLocationInfo locationInfo;
    private final DynamicPackContents contents;
    private final Set<String> namespaces = new HashSet<>();

    public DynamicPackResources(String packId, DynamicPackContents contents) {
        this(packId, contents, new PackLocationInfo(
                packId,
                Component.literal(packId),
                PackSource.BUILT_IN,
                Optional.empty()
        ));
    }

    private DynamicPackResources(String packId, DynamicPackContents contents, PackLocationInfo locationInfo) {
        super(locationInfo);
        this.packId = packId;
        this.contents = contents;
        this.locationInfo = locationInfo;
    }

    public void addNamespace(String namespace) {
        namespaces.add(namespace);
    }

    public DynamicPackContents getContents() {
        return contents;
    }

    @Override
    public PackLocationInfo location() {
        return locationInfo;
    }

    /**
     * 提供 {@code pack.mcmeta} 的内容。
     *
     * <p><b>这是本类在 26.1 上最关键的一处改动。</b>
     * 1.21.1 时本类直接<b>覆写</b> {@code getMetadataSection(MetadataSectionSerializer)} 返回
     * {@link #serverMetadata()}，因此不需要真实的 {@code pack.mcmeta} 文件。
     *
     * <p>26.1 把 {@code getMetadataSection} 下移到了 {@link AbstractPackResources}，其实现为：
     * <pre>{@code
     * IoSupplier<InputStream> metadata = packResources.getRootResource("pack.mcmeta");
     * if (metadata == null) return ResourceMetadata.EMPTY;   // ← 关键分支
     * }</pre>
     * 一旦这里返回 {@code null}，元数据就是 {@code EMPTY}，
     * {@code getMetadataSection(PackMetadataSection)} 得到 {@code null}，
     * 于是 {@code Pack.readPackMetadata} 打印 {@code Missing metadata in pack ...} 并返回 {@code null}，
     * {@code Pack.readMetaAndCreate} 随之返回 {@code null} —— <b>整个动态包根本不会被创建</b>，
     * 附魔数据全部滞留在内存里，表现为「附魔读不到」，且不产生任何显式异常。
     *
     * <p>因此这里必须真正产出 {@code pack.mcmeta} 字节。
     * 内容不手写 JSON，而是用官方 codec 编码 {@link #serverMetadata()}，
     * 以免把 26.1 的 {@code min_format}/{@code max_format} 规则写错
     * （26.1 要求这两个字段；服务端 {@code lastPreMinorVersion} 为 81，
     * 低于等于它时还额外强制要求 {@code supported_formats}）。
     * 包格式号从 {@code SharedConstants.getCurrentVersion().packVersion(SERVER_DATA)} 动态取，
     * 保证与当前游戏版本一致。
     */
    @Nullable
    @Override
    public IoSupplier<InputStream> getRootResource(String... elements) {
        if (elements.length != 1 || !"pack.mcmeta".equals(elements[0])) {
            return null;
        }
        return () -> {
            JsonObject root = new JsonObject();
            root.add(
                    PackMetadataSection.SERVER_TYPE.name(),
                    PackMetadataSection.SERVER_TYPE.codec()
                            .encodeStart(JsonOps.INSTANCE, serverMetadata())
                            .getOrThrow()
            );

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            try (JsonWriter writer = new JsonWriter(new OutputStreamWriter(bytes, StandardCharsets.UTF_8))) {
                writer.setIndent("  ");
                GsonHelper.writeValue(writer, root, null);
            }
            return new ByteArrayInputStream(bytes.toByteArray());
        };
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getResource(PackType packType, Identifier location) {
        return contents.getResource(location);
    }

    @Override
    public void listResources(PackType packType, String namespace, String path, ResourceOutput output) {
        contents.listResources(namespace, path, output);
    }

    @Override
    public Set<String> getNamespaces(PackType type) {
        return Collections.unmodifiableSet(namespaces);
    }

    /** 构造本包声明的数据包格式元数据（供需要时使用）。 */
    public static PackMetadataSection serverMetadata() {
        return new PackMetadataSection(
                Component.literal("Immersive Mechanical Dynamic Data"),
                new InclusiveRange<>(SharedConstants.getCurrentVersion().packVersion(PackType.SERVER_DATA))
        );
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    public void close() {
    }
}
