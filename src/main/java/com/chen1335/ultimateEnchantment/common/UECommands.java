package com.chen1335.ultimateEnchantment.common;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = UltimateEnchantment.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class UECommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<net.minecraft.commands.CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("ultimate_enchantment")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("reload_config")
                        .executes(context -> {
                            UltimateEnchantment.loadConfig();
                            context.getSource().sendSuccess(() -> Component.translatable("commands.ultimate_enchantment.reload_config.success"), true);
                            return 1;
                        })
                )
        );
    }
}
