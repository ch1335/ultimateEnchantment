package com.chen1335.ultimate_enchantment.tooltip;

import com.chen1335.ultimate_enchantment.client.EnchantmentSpecialDesc;
import com.chen1335.ultimate_enchantment.dataComponentType.UEDataComponentTypes;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.tooltip.TooltipAppender;
import net.neoforged.neoforge.event.RegisterTooltipAppendersEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

/**
 * 附魔书 tooltip 里追加「特殊说明」。
 *
 * <h3>26.1 迁移说明（重要）</h3>
 * 1.21.1 的实现是在 {@code ItemStackMixin} 里注入
 * {@code ItemStack#addToTooltip} 的 {@code RETURN}。
 *
 * <p>26.1 的 NeoForge <b>彻底重构了组件 tooltip 机制</b>：
 * 组件说明不再由 {@code ItemStack#getTooltipLines} 逐个调用 {@code addToTooltip} 产生
 * （那段代码仍保留在 {@code addDetailsToTooltipComponents} 里，但<b>已不在渲染路径上</b>），
 * 而是由 {@code net.neoforged.neoforge.common.tooltip.ItemTooltipHandler} 在
 * {@code GameData} 期间收集 appender 后统一派发。
 *
 * <p>后果：注入 {@code addToTooltip} <b>永远不会被触发</b> —— 它编译得过、mixin 也报「注入成功」、
 * 运行时却不执行，且没有任何报错。表现为「附魔说明在游戏里看不到」。
 * 这正是本项目实际踩到的坑。
 *
 * <p>正确做法是 NeoForge 为此提供的官方扩展点
 * {@link RegisterTooltipAppendersEvent}。本类注册在
 * {@link DataComponents#STORED_ENCHANTMENTS} 的 appender <b>之后</b>，
 * 与原先 {@code @At("RETURN")} 的语义一致：先让原版画完附魔行，再补上说明。
 *
 * <p><b>为何不放在 {@code client} 包</b>：{@code ItemTooltipHandler.init()} 由 {@code GameData}
 * 在 common setup 中调用，本事件是<b>双端</b>的（服务端取 tooltip 也会走这条路径），
 * 因此注册类不能触及纯客户端类型。{@link EnchantmentSpecialDesc} 虽然放在 {@code client} 包下，
 * 但其依赖（{@code Component}/{@code Style}/{@code Holder}）全部是双端安全的。
 */
@EventBusSubscriber(modid = com.chen1335.ultimate_enchantment.UltimateEnchantment.MODID)
public final class EnchantmentDescriptionAppender {

    private EnchantmentDescriptionAppender() {
    }

    /**
     * 与原先一致：只在「附魔书 + 恰好一条附魔」时补说明。
     * 多条附魔时逐条铺开会把 tooltip 撑得非常长，故刻意不加。
     */
    @SubscribeEvent
    public static void registerAppenders(RegisterTooltipAppendersEvent event) {
        // 注册顺序图的锚点：把「本模组的说明 appender」排在原版附魔行之后。
        // 挂载点用自有的空组件类型 —— registerComponentAppender 内部是 putIfAbsent，
        // 直接复用 STORED_ENCHANTMENTS 会因为原版已注册而抛 IllegalStateException。
        event.registerComponentAppenderAfter(
                UEDataComponentTypes.ENCHANTMENT_DESCRIPTION_APPENDER.get(),
                DataComponents.STORED_ENCHANTMENTS,
                EnchantmentDescriptionAppender::append
        );
    }

    private static void append(
            ItemStack stack,
            Item.TooltipContext context,
            TooltipDisplay display,
            @Nullable Player player,
            TooltipFlag tooltipFlag,
            Consumer<Component> builder
    ) {
        // 与服务端行为保持一致：原版组件 appender 受 TooltipDisplay 控制。
        if (!display.shows(DataComponents.STORED_ENCHANTMENTS)) {
            return;
        }
        if (stack.getItem() != Items.ENCHANTED_BOOK) {
            return;
        }

        ItemEnchantments enchantments = stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        if (enchantments.size() != 1) {
            return;
        }

        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            List<MutableComponent> lines = EnchantmentSpecialDesc.getNewDescription(entry.getKey(), entry.getIntValue());
            // 逐行加：说明里一行一个组件，拼成一个组件就分不出行了。
            lines.forEach(builder);
        }
    }
}
