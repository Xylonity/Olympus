package dev.xylonity.olympus.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.client.model.BracersOfZeusModel;
import dev.xylonity.olympus.client.registry.OlympusRenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public final class BracersOfZeusRenderer implements ICurioRenderer.HumanoidRender {

    private static final Identifier TEXTURE = Olympus.of("textures/entity/curio/bracers_of_zeus.png");
    private static final Identifier GLOW_TEXTURE = Olympus.of("textures/entity/curio/bracers_of_zeus_glow.png");
    
    private final BracersOfZeusModel slimModel;
    private final BracersOfZeusModel wideModel;

    public BracersOfZeusRenderer() {
        this.slimModel = new BracersOfZeusModel(Minecraft.getInstance().getEntityModels().bakeLayer(BracersOfZeusModel.SLIM_LAYER_LOCATION));
        this.wideModel = new BracersOfZeusModel(Minecraft.getInstance().getEntityModels().bakeLayer(BracersOfZeusModel.WIDE_LAYER_LOCATION));
    }

    @Override
    public EntityModel<HumanoidRenderState> getModel(final ItemStack stack, final SlotContext slotContext) {
        return wideModel;
    }

    @Override
    public Identifier getModelTexture(final ItemStack stack, final SlotContext slotContext) {
        return TEXTURE;
    }

    @Override
    public void renderFirstPersonHand(
            final ItemStack stack,
            final SlotContext slotContext,
            final HumanoidArm arm,
            final PoseStack poseStack,
            final SubmitNodeCollector submitNodeCollector,
            final AvatarRenderState avatarRenderState,
            final AbstractClientPlayer clientPlayer,
            final int packedLight
    ) {
        final BracersOfZeusModel model = modelFor(clientPlayer);
        model.resetPose();

        // Extra tilt so the bracers and the hand point at the same rotation
        model.arm(arm).zRot = arm == HumanoidArm.LEFT ? -0.1F : 0.1F;

        submitArm(model, stack, arm, poseStack, submitNodeCollector, packedLight, avatarRenderState.outlineColor);
    }

    @Override
    public void prepareModel(
            final ItemStack stack,
            final SlotContext slotContext,
            final PoseStack poseStack,
            final SubmitNodeCollector submitNodeCollector,
            final int packedLight,
            final HumanoidRenderState renderState,
            final RenderLayerParent<HumanoidRenderState, EntityModel<HumanoidRenderState>> renderLayerParent,
            final EntityRendererProvider.Context context,
            final float yRotation,
            final float xRotation
    ) {
        final BracersOfZeusModel model = modelFor(renderState);
        if (renderLayerParent.getModel() instanceof HumanoidModel<?> parentModel) {
            model.arm(HumanoidArm.LEFT).loadPose(parentModel.leftArm.storePose());
            model.arm(HumanoidArm.RIGHT).loadPose(parentModel.rightArm.storePose());
        }
        else {
            ICurioRenderer.setupHumanoidAnimations(model, renderState);
        }

    }

    @Override
    public void renderModel(
            final ItemStack stack,
            final SlotContext slotContext,
            final PoseStack poseStack,
            final SubmitNodeCollector submitNodeCollector,
            final int packedLight,
            final HumanoidRenderState renderState,
            final RenderLayerParent<HumanoidRenderState, EntityModel<HumanoidRenderState>> renderLayerParent,
            final EntityRendererProvider.Context context,
            final float yRotation,
            final float xRotation
    ) {
        final BracersOfZeusModel model = modelFor(renderState);
        submitArm(model, stack, HumanoidArm.LEFT, poseStack, submitNodeCollector, packedLight, renderState.outlineColor);
        submitArm(model, stack, HumanoidArm.RIGHT, poseStack, submitNodeCollector, packedLight, renderState.outlineColor);
    }

    private void submitArm(
            final BracersOfZeusModel model,
            final ItemStack stack,
            final HumanoidArm arm,
            final PoseStack poseStack,
            final SubmitNodeCollector submitNodeCollector,
            final int packedLight,
            final int outlineColor
    ) {
        final ModelPart armPart = model.arm(arm);
        poseStack.pushPose();
        armPart.translateAndRotate(poseStack);

        submitNodeCollector.order(1).submitModelPart(
                model.bracer(arm),
                poseStack,
                RenderTypes.armorCutoutNoCull(TEXTURE),
                packedLight,
                OverlayTexture.NO_OVERLAY,
                null,
                false,
                stack.hasFoil(),
                -1,
                null,
                outlineColor
        );
        submitNodeCollector.order(3).submitModelPart(
                model.outline(arm),
                poseStack,
                OlympusRenderTypes.invertedCubeGlow(GLOW_TEXTURE),
                LightCoordsUtil.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                null,
                false,
                false,
                -1,
                null,
                outlineColor
        );

        poseStack.popPose();
    }

    private BracersOfZeusModel modelFor(final HumanoidRenderState renderState) {
        if (
            renderState instanceof AvatarRenderState avatarRenderState
            && avatarRenderState.skin != null
            && avatarRenderState.skin.model() == PlayerModelType.SLIM
        ) {
            return slimModel;
        }

        return wideModel;
    }

    private BracersOfZeusModel modelFor(final AbstractClientPlayer player) {
        return player.getSkin().model() == PlayerModelType.SLIM ? slimModel : wideModel;
    }

}