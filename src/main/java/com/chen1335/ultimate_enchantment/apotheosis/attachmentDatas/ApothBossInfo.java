package com.chen1335.ultimate_enchantment.apotheosis.attachmentDatas;

import dev.shadowsoffire.apotheosis.loot.LootRarity;
import dev.shadowsoffire.apotheosis.tiers.GenContext;
import dev.shadowsoffire.apotheosis.tiers.WorldTier;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * 神化 Boss 生成参数的快照，挂在 Boss 的 attachment 上。
 * <p>
 * 神化只把最终摇出的那一个稀有度写进了 Boss 的持久 NBT（{@code apoth.boss.rarity}），
 * 生成用的上下文和它能出的稀有度范围本身用完就丢。额外掉落要给出「同档次」的装备就得
 * 复刻它们，所以 {@code Invader#initBoss} 收尾时抄一份到这里、死亡时读回来，替代了原先
 * 直接往持久 NBT 里塞复合标签的做法。
 * <p>
 * <b>本类直接持有神化类型</b>，所以它的 {@code AttachmentType} 只能在神化加载时注册 ——
 * 见 {@link ApothBossAttachmentTypes} 里关于类加载隔离的说明。这也是不去手写
 * 「稀有度 ↔ ID」转换的代价：能直接用 {@code GenContext} 往返，转换与兜底逻辑都收在本类里，
 * 调用方只剩一行。
 */
public class ApothBossInfo implements ValueIOSerializable {

    private static final String TIER_KEY = "tier";
    private static final String LUCK_KEY = "luck";
    private static final String DIMENSION_KEY = "dimension";
    private static final String BIOME_KEY = "biome";
    private static final String STAGES_KEY = "stages";
    private static final String RARITIES_KEY = "rarities";

    private WorldTier tier = WorldTier.HAVEN;

    private float luck;

    private ResourceKey<Level> dimension = Level.OVERWORLD;

    /** 生成时所在的生物群系；取不到 key 就是 {@code null}，还原时退回实体当前位置。 */
    @Nullable
    private ResourceKey<Biome> biome;

    private Set<String> stages = Set.of();

    /** 该 Boss 能出的稀有度，即 {@code Invader#stats} 的 key 集合。 */
    private Set<LootRarity> rarities = Set.of();

    /** 给 {@code AttachmentType.serializable} 的默认值工厂用。 */
    public ApothBossInfo() {
    }

    /**
     * 把一份生成参数快照下来。
     *
     * @param ctx      该 Boss 生成时用的上下文
     * @param rarities 该 Boss 能出的稀有度，即 {@code Invader#stats} 的 key 集合
     */
    public ApothBossInfo(GenContext ctx, Set<LootRarity> rarities) {
        this.tier = ctx.tier();
        this.luck = ctx.luck();
        this.dimension = ctx.dimension();
        // 直接持有对象的 Holder 取不到 key（正常不会出现在这里），取不到就存 null。
        this.biome = ctx.biome().unwrapKey().orElse(null);
        // 复制一份：调用方传进来的可能是注册表或别的对象的活视图，不该被这里持有。
        this.stages = Set.copyOf(ctx.stages());
        this.rarities = Set.copyOf(rarities);
    }

    /**
     * 还原成 {@link GenContext}。
     * <p>
     * {@code rand} 用现场的 —— 生成过程中推进的那个随机源在死亡时早已没有意义。
     */
    public GenContext toGenContext(ServerLevel level, LivingEntity entity) {
        return new GenContext(
                level.getRandom(),
                this.tier,
                this.luck,
                this.dimension,
                this.resolveBiome(level, entity),
                this.stages
        );
    }

    public Set<LootRarity> rarities() {
        return this.rarities;
    }

    /**
     * 还原生物群系。存档里的群系被数据包换掉时会取不到，这时退回实体的当前位置，
     * 至少不让整条掉落流程崩掉。
     */
    private Holder<Biome> resolveBiome(ServerLevel level, LivingEntity entity) {
        if (this.biome != null) {
            // 26.1 起 RegistryAccess 只有 lookupOrThrow（registryOrThrow 已移除），
            // 且按 key 取 Holder 走 RegistryLookup#get（Registry#getHolder 已移除）。
            Holder.Reference<Biome> holder = level.registryAccess()
                    .lookupOrThrow(Registries.BIOME)
                    .get(this.biome)
                    .orElse(null);
            if (holder != null) {
                return holder;
            }
        }
        return level.getBiome(entity.blockPosition());
    }

    @Override
    public void serialize(ValueOutput output) {
        // 枚举与稀有度都交给各自的 CODEC，由 ValueOutput 直接编码。
        // 26.1 起 ValueOutput.store 对编码失败的处理与旧版 NbtOps 不同：
        // 这里用 storeNullable 之外的形式时失败会直接抛，故对可空字段做显式判空。
        output.store(TIER_KEY, WorldTier.CODEC, this.tier);
        output.putFloat(LUCK_KEY, this.luck);
        output.putString(DIMENSION_KEY, this.dimension.identifier().toString());
        if (this.biome != null) {
            output.putString(BIOME_KEY, this.biome.identifier().toString());
        }

        ValueOutput.TypedOutputList<String> stages = output.list(STAGES_KEY, Codec.STRING);
        for (String stage : this.stages) {
            stages.add(stage);
        }

        ValueOutput.TypedOutputList<LootRarity> rarityList = output.list(RARITIES_KEY, LootRarity.CODEC);
        for (LootRarity rarity : this.rarities) {
            rarityList.add(rarity);
        }
    }

    @Override
    public void deserialize(ValueInput input) {
        this.tier = input.read(TIER_KEY, WorldTier.CODEC).orElse(WorldTier.HAVEN);
        this.luck = input.getFloatOr(LUCK_KEY, 0.0F);
        this.dimension = readDimension(input.getStringOr(DIMENSION_KEY, Level.OVERWORLD.identifier().toString()));
        this.biome = readBiomeKey(input.getString(BIOME_KEY).orElse(null));

        Set<String> stages = new HashSet<>();
        for (String stage : input.listOrEmpty(STAGES_KEY, Codec.STRING)) {
            stages.add(stage);
        }
        this.stages = Set.copyOf(stages);

        Set<LootRarity> pool = new HashSet<>();
        for (LootRarity rarity : input.listOrEmpty(RARITIES_KEY, LootRarity.CODEC)) {
            pool.add(rarity);
        }
        this.rarities = Set.copyOf(pool);
    }

    /**
     * 还原维度。存档里的 ID 缺失或被数据包换掉时退回主世界 —— attachment 是在实体加载时
     * 反序列化的，这里抛异常会让整个实体加载失败，代价太大。
     */
    private static ResourceKey<Level> readDimension(String id) {
        Identifier location = Identifier.tryParse(id);
        return location == null ? Level.OVERWORLD : ResourceKey.create(Registries.DIMENSION, location);
    }

    @Nullable
    private static ResourceKey<Biome> readBiomeKey(String id) {
        if (id == null) {
            return null;
        }
        Identifier location = Identifier.tryParse(id);
        return location == null ? null : ResourceKey.create(Registries.BIOME, location);
    }
}
