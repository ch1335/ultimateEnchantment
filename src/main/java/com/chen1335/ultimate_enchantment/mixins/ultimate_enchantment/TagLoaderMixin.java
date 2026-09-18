package com.chen1335.ultimate_enchantment.mixins.ultimate_enchantment;

import com.chen1335.ultimate_enchantment.config.CommonConfig;
import com.chen1335.ultimate_enchantment.tags.UEEnchantmentTags;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.TagLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(TagLoader.class)
public class TagLoaderMixin {
    @Shadow
    @Final
    private String directory;

    @ModifyReturnValue(method = "load", at = @At("RETURN"))
    private Map<Identifier, List<TagLoader.EntryWithSource>> load(Map<Identifier, List<TagLoader.EntryWithSource>> original) {
        if (directory.equals("tags/enchantment")) {
            List<TagLoader.EntryWithSource> oldOrder = original.get(EnchantmentTags.TOOLTIP_ORDER.location());
            // 数据包可以用 {"replace": true} 整体替换掉这个标签，甚至干脆不提供它，
            // 那时这里取到的是 null。没有可重排的原列表就整个跳过，让原版按原样处理，
            // 别在这里把数据包加载搞崩。
            if (oldOrder != null) {
                List<TagLoader.EntryWithSource> newOrder = new ArrayList<>(oldOrder.size());
                TagLoader.EntryWithSource ultimateEnchantments = null;
                TagLoader.EntryWithSource legendaryEnchantments = null;
                List<TagLoader.EntryWithSource> otherEnchantments = new ArrayList<>();
                for (TagLoader.EntryWithSource entryWithSource : oldOrder) {
                    if (entryWithSource.entry().getId().equals(UEEnchantmentTags.ULTIMATE_ENCHANTMENT.location())) {
                        ultimateEnchantments = entryWithSource;
                    } else if (entryWithSource.entry().getId().equals(UEEnchantmentTags.LEGENDARY_ENCHANTMENT.location())) {
                        legendaryEnchantments = entryWithSource;
                    } else {
                        otherEnchantments.add(entryWithSource);
                    }
                }

                // 只加真正找到的那两条。把 null 塞进列表的话，原版 TagLoader.build 迭代元素时会 NPE，
                // 异常抛在标签构建阶段，进世界和 /reload 会整体失败 —— 而这两条缺席只是数据包
                // 没带本模组的标签，属于正常情况，不该升级成崩溃。
                if (ultimateEnchantments != null) {
                    newOrder.add(ultimateEnchantments);
                }
                if (legendaryEnchantments != null) {
                    newOrder.add(legendaryEnchantments);
                }
                newOrder.addAll(otherEnchantments);
                original.put(EnchantmentTags.TOOLTIP_ORDER.location(), newOrder);
            }

            if (!CommonConfig.isUltimateEnchantmentExclusiveEachOther.getAsBoolean()) {
                original.put(UEEnchantmentTags.ULTIMATE_ENCHANTMENT_EXCLUSIVE.location(), List.of());
            }
        }
        return original;
    }

}
