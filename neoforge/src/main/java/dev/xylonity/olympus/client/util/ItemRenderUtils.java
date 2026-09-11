package dev.xylonity.olympus.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class ItemRenderUtils {

    public static boolean isHandDisplay(final ItemDisplayContext context) {
        return context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
                || context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                || context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND
                || context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
    }

    public static void renderIcon(final ItemStack stack, final ItemDisplayContext context, final PoseStack poseStack, final MultiBufferSource buffers, final int packedLight, final int packedOverlay, final ModelResourceLocation modelLocation) {
        final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        final BakedModel model = itemRenderer.getItemModelShaper().getModelManager().getModel(modelLocation);

        poseStack.pushPose();

        poseStack.translate(0.5D, 0.5D, 0.5D);
        itemRenderer.render(stack, context, false, poseStack, buffers, packedLight, packedOverlay, model);

        poseStack.popPose();
    }

}
