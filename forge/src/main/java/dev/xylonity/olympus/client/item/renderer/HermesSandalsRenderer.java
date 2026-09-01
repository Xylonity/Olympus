package dev.xylonity.olympus.client.item.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.client.item.model.HermesSandalsModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public final class HermesSandalsRenderer implements ICurioRenderer.HumanoidRender {

    private static final Identifier TEXTURE = Olympus.of("textures/entity/curio/hermes_sandals.png");

    private final HermesSandalsModel model;

    public HermesSandalsRenderer() {
        model = new HermesSandalsModel(Minecraft.getInstance().getEntityModels().bakeLayer(HermesSandalsModel.LAYER_LOCATION));
    }

    @Override
    public EntityModel<HumanoidRenderState> getModel(final ItemStack stack, final SlotContext slotContext) {
        return model;
    }

    @Override
    public Identifier getModelTexture(final ItemStack stack, final SlotContext slotContext) {
        return TEXTURE;
    }

    @Override
    public void prepareModel(final ItemStack stack, final SlotContext slotContext, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int packedLight, final HumanoidRenderState renderState, final RenderLayerParent<HumanoidRenderState, EntityModel<HumanoidRenderState>> renderLayerParent, final EntityRendererProvider.Context context, final float yRotation, final float xRotation) {
        if (renderLayerParent.getModel() instanceof HumanoidModel<?> parentModel) {
            model.leftLegPart().loadPose(parentModel.leftLeg.storePose());
            model.rightLegPart().loadPose(parentModel.rightLeg.storePose());
        }
        else {
            ICurioRenderer.setupHumanoidAnimations(model, renderState);
        }
        
    }

    @Override
    public void renderFirstPersonHand(final ItemStack stack, final SlotContext slotContext, final HumanoidArm arm, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final AvatarRenderState avatarRenderState, final AbstractClientPlayer clientPlayer, final int packedLight) {
       ;;
    }

}
