package com.chen1335.ultimateEnchantment.client;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.enchantment.enchatments.UEEnchantments;
import com.chen1335.ultimateEnchantment.utils.UEEnchantmentHelper;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = UltimateEnchantment.MODID, value = Dist.CLIENT)
public class ClientEventHandler {
    @SubscribeEvent
    public static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (Minecraft.getInstance().getSingleplayerServer() != null) {

        }
    }

    @SubscribeEvent
    public static void LivingEntityUseItemEvent$Start(LivingEntityUseItemEvent.Start event) {
        UEEnchantmentHelper.runIfEnchantmentExist(UEEnchantments.QUICK_LATCH, holder -> {
            if (event.getItem().getEnchantmentLevel(holder) > 0) {
                Minecraft.getInstance().rightClickDelay = 0;
            }
        });
    }
}
