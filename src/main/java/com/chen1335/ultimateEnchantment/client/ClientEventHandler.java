package com.chen1335.ultimateEnchantment.client;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;

@EventBusSubscriber(modid = UltimateEnchantment.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientEventHandler {
    private static boolean a = true;
    @SubscribeEvent
    public static void RenderTooltipEvent(RenderTooltipEvent.Pre event){

    }
}
