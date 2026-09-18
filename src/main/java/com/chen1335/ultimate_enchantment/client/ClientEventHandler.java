package com.chen1335.ultimate_enchantment.client;

import com.chen1335.ultimate_enchantment.UltimateEnchantment;
import com.chen1335.ultimate_enchantment.common.EnchantmentLookup;
import com.chen1335.ultimate_enchantment.enchantment.UEEnchantments;
import com.chen1335.ultimate_enchantment.utils.UEEnchantmentHelper;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = UltimateEnchantment.MODID, value = Dist.CLIENT)
public class ClientEventHandler {
    @SubscribeEvent
    public static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (Minecraft.getInstance().getSingleplayerServer() != null) {

        }
    }

    /**
     * 缓存客户端这份附魔注册表。
     * <p>
     * 只在客户端没有本地服务端时才会被读到（多人游戏）。单人游戏里
     * {@code EnchantmentLookup#getOrNull} 会优先走服务端槽位，用不到这份 ——
     * 两份都留着是为了让端的优先级与 {@code CommonHooks#resolveLookup} 一致。
     */
    @SubscribeEvent
    public static void loggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        EnchantmentLookup.refreshClient(event.getPlayer().level().registryAccess());
    }

    /**
     * 断开时清掉客户端那份，避免重连到别的服务器还拿着上一条连接的注册表。
     */
    @SubscribeEvent
    public static void loggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        EnchantmentLookup.clearClient();
    }

    @SubscribeEvent
    public static void LivingEntityUseItemEvent$Start(LivingEntityUseItemEvent.Start event) {
        UEEnchantmentHelper.runIfEnchantmentExist(UEEnchantments.QUICK_LATCH.getKey(), holder -> {
            if (event.getItem().getEnchantmentLevel(holder) > 0) {
                Minecraft.getInstance().rightClickDelay = 0;
            }
        });
    }

    @SubscribeEvent
    public static void ModifyBakingResult(ModelEvent.ModifyBakingResult event) {
//        for (Item item : BuiltInRegistries.ITEM) {
//            ModelResourceLocation key = ModelResourceLocation.inventory(item.builtInRegistryHolder().getKey().identifier());
//            BakedModel original = event.getModels().get(key);
//            if (original == null || original instanceof UEBakedModelWrapper) {
//                return;
//            }
//            event.getModels().put(key, new UEBakedModelWrapper<>(original));
//        }
    }
}
