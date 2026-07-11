package com.chen1335.ultimateEnchantment.client;

import net.minecraft.client.Minecraft;

public class UEClient {
    public static void submit(Runnable runnable) {
        Minecraft.getInstance().submit(runnable);
    }
}
