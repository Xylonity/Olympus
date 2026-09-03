package dev.xylonity.olympus.client.item.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xylonity.knightlib.client.animation.KnightLibAnimationSource;
import dev.xylonity.knightlib.client.animation.KnightLibModelSource;
import dev.xylonity.knightlib.client.animation.renderer.KnightLibItemRenderer;
import dev.xylonity.olympus.client.util.ItemRenderUtils;
import dev.xylonity.olympus.client.item.model.PoseidonTridentModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class PoseidonTridentItemRenderer extends KnightLibItemRenderer {

    public static final ModelResourceLocation INVENTORY_MODEL = new ModelResourceLocation("olympus", "poseidon_trident_item", "inventory");

    @Override
    protected KnightLibModelSource defineModel(final ItemStack stack) {
        return KnightLibModelSource.geo(PoseidonTridentModel.MODEL);
    }

    @Override
    protected KnightLibAnimationSource defineAnimations(final ItemStack stack) {
        return KnightLibAnimationSource.geo(PoseidonTridentModel.ANIMATIONS);
    }

    @Override
    public ResourceLocation getTextureLocation(final ItemStack stack) {
        return PoseidonTridentModel.TEXTURE;
    }

    @Override
    protected String getAmbientAnimation(final ItemStack stack) {
        return "idle";
    }

    @Override
    public void renderByItem(final ItemStack stack, final ItemDisplayContext context, final PoseStack poseStack, final MultiBufferSource buffers, final int packedLight, final int packedOverlay) {
        if (!ItemRenderUtils.isHandDisplay(context)) {
            ItemRenderUtils.renderIcon(stack, context, poseStack, buffers, packedLight, packedOverlay, INVENTORY_MODEL);
            return;
        }

        poseStack.pushPose();

        final LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && player.isUsingItem() && player.getUseItem() == stack) {
            if (context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND || context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
                poseStack.mulPose(Axis.XP.rotationDegrees(180));
                poseStack.translate(0, -1.25, -1);
            }
            else if (context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
                poseStack.translate(0.5, -1, 0.55);
                poseStack.mulPose(Axis.XP.rotationDegrees(-35));
            }
            else if (context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
                poseStack.translate(-0.5, -1, 0.55);
                poseStack.mulPose(Axis.XP.rotationDegrees(-35));
            }

        }

        super.renderByItem(stack, context, poseStack, buffers, packedLight, packedOverlay);

        poseStack.popPose();
    }

}