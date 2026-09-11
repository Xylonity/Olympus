package dev.xylonity.olympus.client.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xylonity.knightlib.api.util.KnightLibEasings;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

/// Backport of vanilla's mc26+ spear attack mutations
public final class SpearAttackTransforms {

    public static void applyFirstPersonBase(final PoseStack poseStack, final HumanoidArm arm, final float equipProgress) {
        final int side = arm == HumanoidArm.RIGHT ? 1 : -1;
        poseStack.translate(side * 0.56F, -0.52F + equipProgress * -0.6F, -0.72F);
    }

    public static void applyFirstPersonAttack(final PoseStack poseStack, final HumanoidArm arm, final float attackTime) {
        final float start = KnightLibEasings.EASE_IN_OUT_SINE.apply(progress(attackTime, 0.0F, 0.05F));
        final float thrust = KnightLibEasings.EASE_OUT_BACK.apply(progress(attackTime, 0.05F, 0.2F));
        final float recover = KnightLibEasings.EASE_IN_OUT_EXPO.apply(progress(attackTime, 0.4F, 1.0F));
        final int side = arm == HumanoidArm.RIGHT ? 1 : -1;

        poseStack.translate(side * 0.1F * (start - thrust), -0.075F * (start - recover), 0.65F * (start - thrust));
        poseStack.mulPose(Axis.XP.rotationDegrees(-70.0F * (start - recover)));
        poseStack.translate(0.0D, 0.0D, -0.25D * (recover - thrust));
    }

    public static void applyThirdPersonItem(final PoseStack poseStack, final float attackTime) {
        final float thrust = KnightLibEasings.EASE_IN_QUAD.apply(progress(attackTime, 0.05F, 0.2F));
        final float recover = KnightLibEasings.EASE_IN_OUT_EXPO.apply(progress(attackTime, 0.4F, 1.0F));
        final float delta = thrust - recover;

        poseStack.rotateAround(Axis.XN.rotationDegrees(70.0F * delta), 0.0F, -0.125F, 0.125F);
        poseStack.translate(0.0F, 0.38F * delta, 0.0F);
    }

    public static void applyThirdPersonAttackArm(final HumanoidModel<?> model, final LivingEntity entity) {
        final float start = KnightLibEasings.EASE_IN_OUT_SINE.apply(progress(model.attackTime, 0.0F, 0.05F));
        final float thrust = KnightLibEasings.EASE_IN_QUAD.apply(progress(model.attackTime, 0.05F, 0.2F));
        final float recover = KnightLibEasings.EASE_IN_OUT_EXPO.apply(progress(model.attackTime, 0.4F, 1.0F));

        model.rightArm.yRot -= model.body.yRot;
        model.leftArm.yRot -= model.body.yRot;
        model.leftArm.xRot -= model.body.yRot;

        final ModelPart attackingArm = attackArm(entity) == HumanoidArm.RIGHT ? model.rightArm : model.leftArm;
        attackingArm.xRot += (90.0F * start - 120.0F * thrust + 30.0F * recover) * Mth.DEG_TO_RAD;
    }

    public static HumanoidArm attackArm(final LivingEntity entity) {
        final InteractionHand hand = entity.swingingArm == null ? InteractionHand.MAIN_HAND : entity.swingingArm;
        return hand == InteractionHand.MAIN_HAND ? entity.getMainArm() : entity.getMainArm().getOpposite();
    }

    private static float progress(final float value, final float start, final float end) {
        return Mth.clamp(Mth.inverseLerp(value, start, end), 0.0F, 1.0F);
    }

}
