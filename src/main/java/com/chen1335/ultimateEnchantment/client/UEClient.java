package com.chen1335.ultimateEnchantment.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class UEClient {
    public static void submit(Runnable runnable) {
        Minecraft.getInstance().submit(runnable);
    }

    public static Player getPlayer() {
        return Minecraft.getInstance().player;
    }
}
