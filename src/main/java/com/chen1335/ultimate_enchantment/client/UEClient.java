package com.chen1335.ultimate_enchantment.client;

import com.chen1335.ultimate_enchantment.UltimateEnchantment;
import com.chen1335.ultimate_enchantment.client.effects.UnActiveVanquisherClientExtensions;
import com.chen1335.ultimate_enchantment.mobEffect.MobEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

// 26.1 起 @EventBusSubscriber 不再有 bus 属性（EventBusSubscriber.Bus 枚举已移除），
// 因此监听 mod 事件总线不靠参数声明，只按事件类型分发。
@EventBusSubscriber(modid = UltimateEnchantment.MODID, value = Dist.CLIENT)
public class UEClient {

    /**
     * 注册药水效果的客户端渲染扩展。
     * <p>
     * 26.1 起「未激活的征服者」等级数字改由该扩展点绘制，替代原先注入
     * {@code Gui.renderEffects} 内部循环的 Mixin —— 那个注入点依赖的 {@code List.add}
     * 调用与 {@code l1}/{@code i1} 局部变量在 26.1 的 {@code extractEffects} 中都已不存在。
     */
    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerMobEffect(new UnActiveVanquisherClientExtensions(), MobEffects.UN_ACTIVE_VANQUISHER);
    }

    public static void submit(Runnable runnable) {
        Minecraft.getInstance().submit(runnable);
    }

    public static Player getPlayer() {
        return Minecraft.getInstance().player;
    }
}
