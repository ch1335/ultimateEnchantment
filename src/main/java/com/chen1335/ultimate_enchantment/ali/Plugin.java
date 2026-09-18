package com.chen1335.ultimate_enchantment.ali;

import com.chen1335.ultimate_enchantment.UltimateEnchantment;
import com.chen1335.ultimate_enchantment.loot.predicates.CreeperIsPoweredCondition;
import com.yanny.aci.tooltip.TooltipBuilder;
import com.yanny.ali.api.AliEntrypoint;
import com.yanny.ali.api.IPlugin;
import com.yanny.ali.api.IServerRegistry;

/**
 * Advanced Loot Info (ALI) plugin entrypoint: registers this mod's custom loot condition
 * with ALI so it renders a proper tooltip.
 *
 * <p>ALI has a 26.1.2 release (26.1.2-2.2.0), so this integration is kept rather than removed.
 *
 * <p><b>26.1 migration note</b>: ALI 26.1.2 restructured its API:
 * <ul>
 *   <li>{@code IPlugin} / {@code IServerRegistry} now extend Advanced Core Info (ACI) interfaces
 *       ({@code com.yanny.aci.api.*}); ACI is a separate mod, so it is declared in the build script.</li>
 *   <li>The old {@code com.yanny.ali.plugin.common.tooltip.LiteralTooltipNode} was removed.
 *       {@code registerConditionTooltip}'s callback now returns a {@link TooltipBuilder}
 *       ({@code BiFunction<IServerUtils, T, TooltipBuilder>}).</li>
 * </ul>
 * Hence {@code LiteralTooltipNode.translatable(key)} became {@link TooltipBuilder#keyOnly(String)} --
 * both mean "display the text of this translation key".
 */
@AliEntrypoint
public class Plugin implements IPlugin {
    @Override
    public String getModId() {
        return UltimateEnchantment.MODID;
    }

    @Override
    public void registerServer(IServerRegistry registry) {
        registry.registerConditionTooltip(CreeperIsPoweredCondition.class,
                (utils, creeperIsPoweredCondition) ->
                        TooltipBuilder.keyOnly("ultimate_enchantment.type.condition.creeper_is_powered"));
    }
}
