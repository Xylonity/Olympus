package dev.xylonity.olympus.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xylonity.knightlib.client.animation.KnightLibAnimationSource;
import dev.xylonity.knightlib.client.animation.KnightLibAnimator;
import dev.xylonity.knightlib.client.animation.KnightLibModelSource;
import dev.xylonity.knightlib.client.animation.model.KnightLibModel;
import dev.xylonity.olympus.client.item.model.SpearOfAresModel;
import dev.xylonity.olympus.client.texture.SpearDissolveTextures;
import dev.xylonity.olympus.common.entity.projectile.SpearOfAresEntity;
import dev.xylonity.olympus.common.item.SpearOfAresItem;
import dev.xylonity.olympus.registry.OlympusRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.NonNull;

import java.util.Set;

public final class SpearOfAresEntityRenderer extends EntityRenderer<SpearOfAresEntity> {

    private static final String OUTLINE_BONE = "cube_outline";
    private static final Set<String> OUTLINE_BONES = Set.of(OUTLINE_BONE);

    private final KnightLibModelSource.InstanceCache modelCache = new KnightLibModelSource.InstanceCache();
    private final KnightLibAnimationSource animations = KnightLibAnimationSource.geo(SpearOfAresModel.ANIMATIONS);

    public SpearOfAresEntityRenderer(final EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(final SpearOfAresEntity entity, final float entityYaw, final float partialTick, final PoseStack poseStack, final MultiBufferSource buffers, final int packedLight) {
        final boolean charged = SpearOfAresItem.isSpecialAbilityCharged(entity.getSpearStack());
        final ResourceLocation modelLocation = charged ? SpearOfAresModel.CHARGED_MODEL : SpearOfAresModel.BASE_MODEL;
        final ResourceLocation texture = charged ? SpearOfAresModel.CHARGED_TEXTURE : SpearOfAresModel.BASE_TEXTURE;
        final ResourceLocation renderTexture = SpearDissolveTextures.textureFor(texture, entity.getDissolveVisibility(partialTick));

        final KnightLibModel model = modelCache.resolve(KnightLibModelSource.geo(modelLocation));
        KnightLibAnimator.animate(entity.getAnimationHandler(), model, animations::get, entity.level().getGameTime() + partialTick);

        model.setBoneVisible(OUTLINE_BONE, false);

        poseStack.pushPose();

        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) + 90));
        poseStack.mulPose(Axis.ZP.rotationDegrees(90 - Mth.lerp(partialTick, entity.xRotO, entity.getXRot())));

        final float shake = entity.shakeTime - partialTick;
        if (shake > 0) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(-Mth.sin(shake * 3.0F) * shake));
        }

        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(0, -22 / 16F, 0);

        model.render(poseStack, buffers.getBuffer(RenderType.entityCutoutNoCull(renderTexture)), packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        if (charged) {
            model.setBoneVisible(OUTLINE_BONE, true);
            model.renderBones(poseStack, buffers.getBuffer(OlympusRenderTypes.invertedCubesGlow(renderTexture)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1, OUTLINE_BONES);
            model.setBoneVisible(OUTLINE_BONE, false);
        }

        poseStack.popPose();

        super.render(entity, entityYaw, partialTick, poseStack, buffers, packedLight);
    }

    @Override
    protected int getBlockLightLevel(final SpearOfAresEntity entity, final @NonNull BlockPos blockPos) {
        // No black overlay
        return entity.isInWall() ? 15 : super.getBlockLightLevel(entity, blockPos);
    }

    @Override
    public ResourceLocation getTextureLocation(final SpearOfAresEntity entity) {
        return SpearOfAresItem.isSpecialAbilityCharged(entity.getSpearStack()) ? SpearOfAresModel.CHARGED_TEXTURE : SpearOfAresModel.BASE_TEXTURE;
    }

}