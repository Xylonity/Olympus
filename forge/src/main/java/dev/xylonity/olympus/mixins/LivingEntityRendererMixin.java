package dev.xylonity.olympus.mixins;

import dev.xylonity.olympus.registry.OlympusMobEffects;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {

    @Unique
    private LivingEntity olympus$renderedEntity;

    @Inject(
            method = "getRenderType",
            at = @At("HEAD"),
            cancellable = true
    )
    private void olympus$useTranslucentRenderType(final LivingEntity entity, final boolean bodyVisible, final boolean translucent, final boolean glowing, final CallbackInfoReturnable<RenderType> callback) {
        this.olympus$renderedEntity = entity;
        if (entity.hasEffect(OlympusMobEffects.INVISIBILITY_OF_HADES.get())) {
            final LivingEntityRenderer renderer = (LivingEntityRenderer) (Object) this;
            callback.setReturnValue(RenderType.itemEntityTranslucentCull(renderer.getTextureLocation(entity)));
        }

    }

    @ModifyArg(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"
            ),
            index = 7
    )
    private float olympus$applyHadesInvisibilityAlpha(final float alpha) {
        return this.olympus$renderedEntity != null && this.olympus$renderedEntity.hasEffect(OlympusMobEffects.INVISIBILITY_OF_HADES.get()) ? 0.5F : alpha;
    }

}
