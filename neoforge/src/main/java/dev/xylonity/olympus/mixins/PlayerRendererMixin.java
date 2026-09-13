package dev.xylonity.olympus.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.olympus.client.event.OlympusClientEvents;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin {

    @Inject(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/ModelPart;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V", ordinal = 0, shift = At.Shift.AFTER), require = 0)
    private void olympus$renderFirstPersonBracers(final PoseStack poseStack, final MultiBufferSource buffers, final int packedLight, final AbstractClientPlayer player, final ModelPart armPart, final ModelPart sleevePart, final CallbackInfo callback) {
        final PlayerRenderer renderer = (PlayerRenderer) (Object) this;
        final HumanoidArm arm = armPart == renderer.getModel().leftArm ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
        OlympusClientEvents.renderFirstPersonBracers(player, arm, armPart, poseStack, buffers, packedLight);
    }

}
