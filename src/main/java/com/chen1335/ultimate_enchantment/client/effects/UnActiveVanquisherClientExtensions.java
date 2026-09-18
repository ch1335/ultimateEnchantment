package com.chen1335.ultimate_enchantment.client.effects;

import com.chen1335.ultimate_enchantment.UltimateEnchantment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;

/**
 * "未激活的征服者" 效果图标的客户端扩展：在 HUD 图标上叠加显示等级数字。
 *
 * <p>26.1 迁移说明：原实现靠一个 Mixin 注入 {@code Gui.renderEffects} 内部循环
 * （依赖 {@code List.add} 调用点与 {@code l1}/{@code i1} 局部变量）。26.1 把该方法改名为
 * {@code extractEffects} 且重写了实现 —— 它不再构建列表，而是直接 blit 图标，
 * 旧的注入点与局部变量名都已不存在。
 *
 * <p>因此改用 NeoForge 为此场景提供的官方扩展点
 * {@link IClientMobEffectExtensions#extractGuiIcon}，(mod 源码)Gui#extractEffects 会主动调用它。
 * 这比注入内部循环更健壮：不依赖私有实现细节，也不会因原版重构而失效。
 *
 * <p>本类之所以独立于 common 侧的 {@code UnActiveVanquisher}（而非让它直接实现该接口）：
 * 该接口签名引用 {@code Gui}/{@code GuiGraphicsExtractor} 等纯客户端类型，
 * 若由 common 类实现，专用服务器加载时会 NoClassDefFoundError。
 */
public class UnActiveVanquisherClientExtensions implements IClientMobEffectExtensions {

    @Override
    public boolean extractGuiIcon(MobEffectInstance instance, net.minecraft.client.gui.Gui gui,
                                 GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color) {
        // x/y 是图标左上角，width/height 为 18×18。原实现把文字画在图标内偏右下一点。
        MutableComponent component = Component.translatable("enchantment.level." + (instance.getAmplifier() + 1));
        Font font = gui.getFont();
        int textWidth = font.width(component);
        graphics.text(font, component, x + width / 2 - textWidth / 2, y + height - 8, 16777215);
        // 返回 false：不阻止默认图标绘制，只是在它上面叠加文字。
        return false;
    }
}