package dev.xylonity.olympus.client.item.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.client.item.model.BracersOfZeusModel;
import dev.xylonity.olympus.registry.OlympusRenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public final class BracersOfZeusRenderer implements ICurioRenderer.HumanoidRender {

    private static final ResourceLocation TEXTURE = Olympus.of("textures/entity/curio/bracers_of_zeus.png");
    private static final ResourceLocation GLOW_TEXTURE = Olympus.of("textures/entity/curio/bracers_of_zeus_glow.png");

    private static final float THIRD_PERSON_Y_OFFSET = 2 / 16F;
    
    private final BracersOfZeusModel slimModel;
    private final BracersOfZeusModel wideModel;

    public BracersOfZeusRenderer() {
        this.slimModel = new BracersOfZeusModel(Minecraft.getInstance().getEntityModels().bakeLayer(BracersOfZeusModel.SLIM_LAYER_LOCATION));
        this.wideModel = new BracersOfZeusModel(Minecraft.getInstance().getEntityModels().bakeLayer(BracersOfZeusModel.WIDE_LAYER_LOCATION));
    }

    @Override
    public HumanoidModel<LivingEntity> getModel(final ItemStack stack, final SlotContext slotContext) {
        return modelFor(slotContext.entity());
    }

    @Override
    public ResourceLocation getModelTexture(final ItemStack stack, final SlotContext slotContext) {
        return TEXTURE;
    }

    @Override
    public void renderModel(final ItemStack stack, final SlotContext slotContext, final PoseStack poseStack, final RenderLayerParent<LivingEntity, EntityModel<LivingEntity>> renderLayerParent, final MultiBufferSource buffers, final int packedLight) {
        final BracersOfZeusModel model = modelFor(slotContext.entity());
        renderArm(model, stack, HumanoidArm.LEFT, poseStack, buffers, packedLight, THIRD_PERSON_Y_OFFSET, false);
        renderArm(model, stack, HumanoidArm.RIGHT, poseStack, buffers, packedLight, THIRD_PERSON_Y_OFFSET, false);
    }

    public void renderFirstPersonHand(final ItemStack stack, final HumanoidArm arm, final PoseStack poseStack, final MultiBufferSource buffers, final int packedLight, final AbstractClientPlayer player) {
        final BracersOfZeusModel model = modelFor(player);
        model.arm(arm).getAllParts().forEach(ModelPart::resetPose);

        // Extra tilt so the bracers and the hand point at the same rotation
        model.arm(arm).zRot = arm == HumanoidArm.LEFT ? -0.1F : 0.1F;

        renderArm(model, stack, arm, poseStack, buffers, packedLight, 0, true);
    }

    private void renderArm(final BracersOfZeusModel model, final ItemStack stack, final HumanoidArm arm, final PoseStack poseStack, final MultiBufferSource buffers, final int packedLight, final float yOffset, final boolean firstPerson) {
        final ModelPart armPart = model.arm(arm);
        poseStack.pushPose();
        armPart.translateAndRotate(poseStack);
        poseStack.translate(0.0F, yOffset, 0.0F);

        model.bracer(arm).render(
                poseStack, ItemRenderer.getArmorFoilBuffer(buffers, RenderType.armorCutoutNoCull(TEXTURE), false, stack.hasFoil()),
                packedLight, OverlayTexture.NO_OVERLAY
        );
        model.outline(arm).render(
                poseStack, buffers.getBuffer(firstPerson ? OlympusRenderTypes.firstPersonInvertedCubesGlow(GLOW_TEXTURE) : OlympusRenderTypes.invertedCubesGlow(GLOW_TEXTURE)),
                LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY
        );

        poseStack.popPose();
    }

    private BracersOfZeusModel modelFor(final LivingEntity entity) {
        return entity instanceof AbstractClientPlayer player && "slim".equals(player.getModelName()) ? slimModel : wideModel;
    }

}