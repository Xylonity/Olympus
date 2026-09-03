package dev.xylonity.olympus.mixins;

import dev.xylonity.olympus.common.item.SpearOfAresItem;
import dev.xylonity.olympus.registry.OlympusItems;
import dev.xylonity.olympus.registry.OlympusSounds;
import dev.xylonity.olympus.registry.OlympusMobEffects;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(
            method = "getCurrentSwingDuration",
            at = @At("HEAD"),
            cancellable = true
    )
    private void olympus$useAresSpearSwingDuration(final CallbackInfoReturnable<Integer> callback) {
        final LivingEntity entity = (LivingEntity) (Object) this;
        final InteractionHand hand = entity.swingingArm == null ? InteractionHand.MAIN_HAND : entity.swingingArm;
        if (entity.getItemInHand(hand).is(OlympusItems.SPEAR_OF_ARES.get())) {
            callback.setReturnValue(SpearOfAresItem.SWING_DURATION);
        }

    }

    @Inject(
            method = "swing(Lnet/minecraft/world/InteractionHand;Z)V",
            at = @At("TAIL")
    )
    private void olympus$playAresSpearAttackSound(final InteractionHand hand, final boolean updateSelf, final CallbackInfo callback) {
        final LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.level().isClientSide && entity.swingTime == -1 && entity.swingingArm == hand && entity.getItemInHand(hand).is(OlympusItems.SPEAR_OF_ARES.get())) {
            entity.level().playLocalSound(entity.getX(), entity.getY(), entity.getZ(), OlympusSounds.ARES_SPEAR_ATTACK.get(), entity.getSoundSource(), 1.0F, 1.0F, false);
        }

    }

    @Inject(
            method = "canBeSeenAsEnemy",
            at = @At("HEAD"),
            cancellable = true
    )
    private void olympus$hideEntitiesWithHadesInvisibility(final CallbackInfoReturnable<Boolean> callback) {
        final LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.hasEffect(OlympusMobEffects.INVISIBILITY_OF_HADES.get())) {
            callback.setReturnValue(false);
        }

    }

    @ModifyVariable(
            method = "travel",
            at = @At("HEAD"),
            argsOnly = true
    )
    private Vec3 olympus$stopMovementWhileStunned(final Vec3 input) {
        final LivingEntity entity = (LivingEntity) (Object) this;
        if (!entity.hasEffect(OlympusMobEffects.LIGHTNING_STUN.get())) {
            return input;
        }

        final Vec3 movement = entity.getDeltaMovement();
        entity.setDeltaMovement(0.0D, movement.y, 0.0D);

        return Vec3.ZERO;
    }

}