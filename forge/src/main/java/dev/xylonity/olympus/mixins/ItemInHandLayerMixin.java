package dev.xylonity.olympus.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.olympus.client.item.SpearAttackTransforms;
import dev.xylonity.olympus.registry.OlympusItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public abstract class ItemInHandLayerMixin {

    @Inject(
            method = "renderArmWithItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
            )
    )
    private void olympus$applyAresSpearAttack(final LivingEntity entity, final ItemStack stack, final ItemDisplayContext context, final HumanoidArm arm, final PoseStack poseStack, final MultiBufferSource buffers, final int packedLight, final CallbackInfo callback) {
        if (!stack.is(OlympusItems.SPEAR_OF_ARES.get()) || arm != entity.getMainArm()) {
            return;
        }

        final float attackTime = entity.getAttackAnim(Minecraft.getInstance().getFrameTime());
        if (attackTime > 0) {
            SpearAttackTransforms.applyThirdPersonItem(poseStack, attackTime);
        }

    }

}
