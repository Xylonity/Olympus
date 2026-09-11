package dev.xylonity.olympus.client.item.renderer;

import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.client.item.model.HermesSandalsModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public final class HermesSandalsRenderer implements ICurioRenderer {

    private static final ResourceLocation TEXTURE = Olympus.of("textures/entity/curio/hermes_sandals.png");

    private final HermesSandalsModel model;

    public HermesSandalsRenderer() {
        model = new HermesSandalsModel(Minecraft.getInstance().getEntityModels().bakeLayer(HermesSandalsModel.LAYER_LOCATION));
    }

    public HumanoidModel<LivingEntity> getModel(final ItemStack stack, final SlotContext slotContext) {
        return model;
    }

    public ResourceLocation getModelTexture(final ItemStack stack, final SlotContext slotContext) {
        return TEXTURE;
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(final ItemStack stack, final SlotContext slotContext, final PoseStack poseStack, final RenderLayerParent<T, M> renderLayerParent, final MultiBufferSource buffers, final int packedLight, final float limbSwing, final float limbSwingAmount, final float partialTicks, final float ageInTicks, final float netHeadYaw, final float headPitch) {
        ICurioRenderer.followBodyRotations(slotContext.entity(), model);
        model.prepareMobModel(slotContext.entity(), limbSwing, limbSwingAmount, partialTicks);
        model.setupAnim(slotContext.entity(), limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        model.renderToBuffer(poseStack, ItemRenderer.getArmorFoilBuffer(buffers, RenderType.armorCutoutNoCull(TEXTURE), stack.hasFoil()), packedLight, OverlayTexture.NO_OVERLAY);
    }

}
