package dev.xylonity.olympus.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xylonity.knightlib.client.animation.KnightLibAnimationSource;
import dev.xylonity.knightlib.client.animation.KnightLibAnimator;
import dev.xylonity.knightlib.client.animation.KnightLibModelSource;
import dev.xylonity.knightlib.client.animation.model.KnightLibModel;
import dev.xylonity.olympus.client.item.model.PoseidonTridentModel;
import dev.xylonity.olympus.common.entity.projectile.PoseidonTridentEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jspecify.annotations.NonNull;

public final class PoseidonTridentEntityRenderer extends EntityRenderer<PoseidonTridentEntity> {

    private final KnightLibModelSource.InstanceCache modelCache = new KnightLibModelSource.InstanceCache();
    private final KnightLibAnimationSource animations = KnightLibAnimationSource.geo(PoseidonTridentModel.ANIMATIONS);

    public PoseidonTridentEntityRenderer(final EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(final PoseidonTridentEntity entity, final float entityYaw, final float partialTick, final PoseStack poseStack, final MultiBufferSource buffers, final int packedLight) {
        final KnightLibModel model = modelCache.resolve(KnightLibModelSource.geo(PoseidonTridentModel.MODEL));
        KnightLibAnimator.animate(entity.getAnimationHandler(), model, animations::get, entity.level().getGameTime() + partialTick);

        poseStack.pushPose();

        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) + 90F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(90F - Mth.lerp(partialTick, entity.xRotO, entity.getXRot())));

        final float shake = entity.shakeTime - partialTick;
        if (shake > 0) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(-Mth.sin(shake * 3.0F) * shake));
        }

        poseStack.mulPose(Axis.YP.rotationDegrees(90F));

        // The pivot is located basically in the middle of the model, so this is done to close the gap between the bbox and the tip of the model
        poseStack.translate(0, -26/16f, 0);

        model.render(poseStack, buffers.getBuffer(RenderType.entityCutoutNoCull(PoseidonTridentModel.TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);

        poseStack.popPose();

        super.render(entity, entityYaw, partialTick, poseStack, buffers, packedLight);
    }

    @Override
    protected int getBlockLightLevel(final PoseidonTridentEntity entity, final BlockPos blockPos) {
        return entity.isInWall() ? 15 : super.getBlockLightLevel(entity, blockPos);
    }

    @Override
    public @NonNull ResourceLocation getTextureLocation(final PoseidonTridentEntity entity) {
        return PoseidonTridentModel.TEXTURE;
    }

}