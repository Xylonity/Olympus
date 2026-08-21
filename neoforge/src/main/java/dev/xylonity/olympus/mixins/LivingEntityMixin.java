package dev.xylonity.olympus.mixins;

import dev.xylonity.olympus.registry.OlympusMobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "canBeSeenAsEnemy", at = @At("HEAD"), cancellable = true)
    private void olympus$hideEntitiesWithHadesInvisibility(final CallbackInfoReturnable<Boolean> callback) {
        final LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.hasEffect(OlympusMobEffects.INVISIBILITY_OF_HADES)) {
            callback.setReturnValue(false);
        }

    }

    @ModifyVariable(method = "travel", at = @At("HEAD"), argsOnly = true)
    private Vec3 olympus$stopMovementWhileStunned(final Vec3 input) {
        final LivingEntity entity = (LivingEntity) (Object) this;
        if (!entity.hasEffect(OlympusMobEffects.LIGHTNING_STUN)) {
            return input;
        }

        final Vec3 movement = entity.getDeltaMovement();
        entity.setDeltaMovement(0.0D, movement.y, 0.0D);

        return Vec3.ZERO;
    }

}
