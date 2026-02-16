package com.chen1335.ultimateEnchantment.ali;

import com.chen1335.ultimateEnchantment.UltimateEnchantment;
import com.chen1335.ultimateEnchantment.loot.predicates.CreeperIsPoweredCondition;
import com.yanny.ali.api.AliEntrypoint;
import com.yanny.ali.api.IPlugin;
import com.yanny.ali.api.IServerRegistry;
import com.yanny.ali.plugin.common.tooltip.LiteralTooltipNode;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@AliEntrypoint
public class Plugin implements IPlugin {
    @Override
    public String getModId() {
        return UltimateEnchantment.MODID;
    }

    @Override
    public void registerServer(IServerRegistry registry) {
        registry.registerConditionTooltip(CreeperIsPoweredCondition.class, (utils, creeperIsPoweredCondition) -> LiteralTooltipNode.translatable("ultimate_enchantment.type.condition.creeper_is_powered"));
    }
}
