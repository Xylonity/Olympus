package dev.xylonity.olympus.client.item.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.client.item.model.HermesSandalsModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public final class HermesSandalsRenderer implements ICurioRenderer.HumanoidRender {

    private static final ResourceLocation TEXTURE = Olympus.of("textures/entity/curio/hermes_sandals.png");

    private final HermesSandalsModel model;

    public HermesSandalsRenderer() {
        model = new HermesSandalsModel(Minecraft.getInstance().getEntityModels().bakeLayer(HermesSandalsModel.LAYER_LOCATION));
    }

    @Override
    public void prepareModel(final ItemStack stack, final SlotContext slotContext, final PoseStack poseStack, final RenderLayerParent<LivingEntity, EntityModel<LivingEntity>> renderLayerParent, final float limbSwing, final float limbSwingAmount, final float partialTicks, final float ageInTicks, final float netHeadYaw, final float headPitch) {
        ICurioRenderer.HumanoidRender.super.prepareModel(stack, slotContext, poseStack, renderLayerParent, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        if (renderLayerParent.getModel() instanceof HumanoidModel<LivingEntity> humanoidModel) {
            humanoidModel.copyPropertiesTo(getModel(stack, slotContext));
        }

    }

    @Override
    public HumanoidModel<LivingEntity> getModel(final ItemStack stack, final SlotContext slotContext) {
        return model;
    }

    @Override
    public ResourceLocation getModelTexture(final ItemStack stack, final SlotContext slotContext) {
        return TEXTURE;
    }

}