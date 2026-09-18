package com.chen1335.ultimate_enchantment.apotheosis.attachmentDatas;

import com.chen1335.ultimate_enchantment.UltimateEnchantment;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

/**
 * 神化专属的 attachment 注册。
 * <p>
 * <b>整个类只在神化加载时才会被触碰</b>：{@link ApothBossInfo} 直接持有 {@code GenContext}、
 * {@code LootRarity} 等神化类型，神化缺席时加载它会 {@code NoClassDefFoundError}。
 * 所以注册从这里独立出来，由 {@code UltimateEnchantment} 构造器里的
 * {@code ModList.get().isLoaded("apotheosis")} 判断兜住。
 * <p>
 * <b>隔离成立的前提：本类对 {@link ApothBossInfo} 的引用只出现在泛型参数和 lambda 体里，
 * 不出现在字段描述符里。</b>字段声明类型是 {@code Supplier}，泛型编译期就擦除了，所以加载
 * 本类不会顺带加载 {@link ApothBossInfo}；{@code ApothBossInfo::new} 那句在 lambda 体内部，
 * 只有 lambda 被调用时才解析，而 lambda 只在 {@code AttachmentType.serializable} 构建默认值
 * 工厂时执行 —— 那已经是注册事件里的事了。改这个类时请守住这条。
 */
public final class ApothBossAttachmentTypes {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, UltimateEnchantment.MODID);

    /**
     * 神化 Boss 的生成参数快照，见 {@link ApothBossInfo}。
     */
    public static final Supplier<AttachmentType<ApothBossInfo>> APOTH_BOSS_INFO = ATTACHMENT_TYPES.register(
            "apoth_boss_info", () -> AttachmentType.serializable(ApothBossInfo::new).build()
    );

    private ApothBossAttachmentTypes() {
    }
}
