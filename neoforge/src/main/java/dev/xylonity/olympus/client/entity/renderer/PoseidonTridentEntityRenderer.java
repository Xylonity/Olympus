package dev.xylonity.olympus.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xylonity.olympus.common.entity.PoseidonTridentEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ThrownItemRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;

public final class PoseidonTridentEntityRenderer extends EntityRenderer<PoseidonTridentEntity, PoseidonTridentEntityRenderer.RenderState> {

    private final ItemModelResolver itemModelResolver;

    public PoseidonTridentEntityRenderer(final EntityRendererProvider.Context context) {
        super(context);
        itemModelResolver = context.getItemModelResolver();
    }

    @Override
    public void submit(final RenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
        poseStack.pushPose();

        poseStack.mulPose(Axis.YP.rotationDegrees(state.yRot + 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F - state.xRot));
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));

        // The pivot is located basically in the middle of the model, so this is done to close the gap between the bbox and the tip of the model
        poseStack.translate(0, -26/16f, 0);

        state.item.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);

        poseStack.popPose();

        super.submit(state, poseStack, submitNodeCollector, camera);
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(final PoseidonTridentEntity entity, final RenderState state, final float partialTick) {
        super.extractRenderState(entity, state, partialTick);

        state.yRot = entity.getYRot(partialTick);
        state.xRot = entity.getXRot(partialTick);

        itemModelResolver.updateForNonLiving(state.item, entity.getWeaponItem(), ItemDisplayContext.NONE, entity);
    }

    public static final class RenderState extends ThrownItemRenderState {
        private float xRot;
        private float yRot;
    }

}