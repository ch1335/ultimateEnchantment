package com.chen1335.ultimateEnchantment.mixins.ultimate_enchantment.client;

import com.chen1335.ultimateEnchantment.mixinsAPI.minecraft.ItemRendererHooks;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {


    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderModelLists(Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/item/ItemStack;IILcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V"))
    private void render(ItemStack itemStack,
                        ItemDisplayContext displayContext,
                        boolean leftHand,
                        PoseStack poseStack,
                        MultiBufferSource bufferSource,
                        int combinedLight,
                        int combinedOverlay,
                        BakedModel p_model,
                        CallbackInfo ci,
                        @Local(name = "model") BakedModel model,
                        @Local VertexConsumer vertexconsumer
    ) {
        ItemRendererHooks.render(itemStack, displayContext, leftHand, poseStack, bufferSource, combinedLight, combinedOverlay, p_model,model, (ItemRenderer) (Object) this,vertexconsumer);
    }
}
