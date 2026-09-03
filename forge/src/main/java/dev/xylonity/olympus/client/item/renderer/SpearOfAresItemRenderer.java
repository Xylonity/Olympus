package dev.xylonity.olympus.client.item.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xylonity.knightlib.client.animation.KnightLibAnimationSource;
import dev.xylonity.knightlib.client.animation.KnightLibModelSource;
import dev.xylonity.knightlib.client.animation.layer.KnightLibRenderLayer;
import dev.xylonity.knightlib.client.animation.layer.KnightLibRenderLayerContext;
import dev.xylonity.knightlib.client.animation.model.KnightLibModel;
import dev.xylonity.knightlib.client.animation.renderer.KnightLibItemRenderer;
import dev.xylonity.olympus.client.util.ItemRenderUtils;
import dev.xylonity.olympus.client.item.model.SpearOfAresModel;
import dev.xylonity.olympus.common.item.SpearOfAresItem;
import dev.xylonity.olympus.registry.OlympusRenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public final class SpearOfAresItemRenderer extends KnightLibItemRenderer {

    public static final ModelResourceLocation INVENTORY_MODEL = new ModelResourceLocation("olympus", "spear_of_ares_item", "inventory");
    public static final ModelResourceLocation CHARGED_INVENTORY_MODEL = new ModelResourceLocation("olympus", "spear_of_ares_item_charged", "inventory");

    private static final String OUTLINE_BONE = "cube_outline";
    private static final Set<String> OUTLINE_BONES = Set.of(OUTLINE_BONE);

    public SpearOfAresItemRenderer() {
        addRenderLayer(new KnightLibRenderLayer<>() {
            @Override
            public boolean shouldRender(final KnightLibRenderLayerContext<ItemStack> context) {
                return SpearOfAresItem.isSpecialAbilityCharged(context.target());
            }

            @Override
            public void render(final KnightLibRenderLayerContext<ItemStack> context) {
                final ItemDisplayContext displayContext = context.itemDisplayContext();
                final boolean firstPerson = displayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || displayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
                final RenderType renderType = firstPerson ? OlympusRenderTypes.firstPersonInvertedCubesGlow(SpearOfAresModel.CHARGED_TEXTURE) : OlympusRenderTypes.invertedCubesGlow(SpearOfAresModel.CHARGED_TEXTURE);

                context.model().setBoneVisible(OUTLINE_BONE, true);
                try {
                    context.renderBones(OUTLINE_BONES, renderType, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
                }
                finally {
                    context.model().setBoneVisible(OUTLINE_BONE, false);
                }

            }

        });

    }

    @Override
    protected KnightLibModelSource defineModel(final ItemStack stack) {
        return KnightLibModelSource.geo(SpearOfAresItem.isSpecialAbilityCharged(stack) ? SpearOfAresModel.CHARGED_MODEL : SpearOfAresModel.BASE_MODEL);
    }

    @Override
    protected KnightLibAnimationSource defineAnimations(final ItemStack stack) {
        return KnightLibAnimationSource.geo(SpearOfAresModel.ANIMATIONS);
    }

    @Override
    public ResourceLocation getTextureLocation(final ItemStack stack) {
        return SpearOfAresItem.isSpecialAbilityCharged(stack) ? SpearOfAresModel.CHARGED_TEXTURE : SpearOfAresModel.BASE_TEXTURE;
    }

    @Override
    protected RenderType getRenderType(final ItemStack stack, final ResourceLocation texture) {
        return RenderType.entityCutout(texture);
    }

    @Override
    protected void setupBone(final ItemStack stack, final ItemDisplayContext displayContext, final KnightLibModel model, final String boneName, final float partialTicks) {
        if (OUTLINE_BONE.equals(boneName)) {
            model.setBoneVisible(OUTLINE_BONE, false);
        }

    }

    @Override
    public void renderByItem(final ItemStack stack, final ItemDisplayContext context, final PoseStack poseStack, final MultiBufferSource buffers, final int packedLight, final int packedOverlay) {
        if (!ItemRenderUtils.isHandDisplay(context)) {
            ItemRenderUtils.renderIcon(stack, context, poseStack, buffers, packedLight, packedOverlay, SpearOfAresItem.isSpecialAbilityCharged(stack) ? CHARGED_INVENTORY_MODEL : INVENTORY_MODEL);
            return;
        }

        poseStack.pushPose();

        final LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && player.isUsingItem() && player.getUseItem() == stack) {
            if (context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND || context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
                poseStack.mulPose(Axis.XP.rotationDegrees(180));
                poseStack.translate(0, -0.75, -1);
            }
            else if (context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
                poseStack.translate(0.5, -0.9, 0.15);
                poseStack.mulPose(Axis.XP.rotationDegrees(-25));
            }
            else if (context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
                poseStack.translate(-0.5, -0.9, 0.15);
                poseStack.mulPose(Axis.XP.rotationDegrees(-25));
            }

        }

        super.renderByItem(stack, context, poseStack, buffers, packedLight, packedOverlay);

        poseStack.popPose();
    }

}
