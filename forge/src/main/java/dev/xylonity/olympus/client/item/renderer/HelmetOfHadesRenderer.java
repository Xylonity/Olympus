package dev.xylonity.olympus.client.item.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.client.item.model.HelmetOfHadesModel;
import dev.xylonity.olympus.registry.OlympusRenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public final class HelmetOfHadesRenderer implements ICurioRenderer.HumanoidRender {

    private static final ResourceLocation TEXTURE = Olympus.of("textures/entity/curio/helmet_of_hades.png");
    private static final ResourceLocation OUTLINE_TEXTURE = Olympus.of("textures/entity/curio/helmet_of_hades_luminous_outline.png");

    private final HelmetOfHadesModel model;

    public HelmetOfHadesRenderer() {
        model = new HelmetOfHadesModel(Minecraft.getInstance().getEntityModels().bakeLayer(HelmetOfHadesModel.LAYER_LOCATION));
    }

    @Override
    public HumanoidModel<LivingEntity> getModel(final ItemStack stack, final SlotContext slotContext) {
        return model;
    }

    @Override
    public ResourceLocation getModelTexture(final ItemStack stack, final SlotContext slotContext) {
        return TEXTURE;
    }

    @Override
    public void renderModel(final ItemStack stack, final SlotContext slotContext, final PoseStack poseStack, final RenderLayerParent<LivingEntity, EntityModel<LivingEntity>> renderLayerParent, final MultiBufferSource buffers, final int packedLight) {
        poseStack.pushPose();
        model.headPart().translateAndRotate(poseStack);

        model.helmet().render(poseStack, ItemRenderer.getArmorFoilBuffer(buffers, RenderType.entityTranslucent(TEXTURE), false, stack.hasFoil()), packedLight, OverlayTexture.NO_OVERLAY);
        model.outline().render(poseStack, buffers.getBuffer(OlympusRenderTypes.invertedCubesGlow(OUTLINE_TEXTURE)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
    }

}