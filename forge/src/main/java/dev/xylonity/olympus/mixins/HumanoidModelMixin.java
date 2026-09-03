package dev.xylonity.olympus.mixins;

import dev.xylonity.olympus.client.item.SpearAttackTransforms;
import dev.xylonity.olympus.registry.OlympusItems;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/// Mutates the player arm when using the spear attacks
@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin<T extends LivingEntity> {

    @Inject(method = "setupAttackAnimation", at = @At("HEAD"), cancellable = true)
    private void olympus$applyAresSpearAttack(final T entity, final float ageInTicks, final CallbackInfo callback) {
        if (!entity.getMainHandItem().is(OlympusItems.SPEAR_OF_ARES.get())) {
            return;
        }

        SpearAttackTransforms.applyThirdPersonAttackArm((HumanoidModel<?>) (Object) this, entity);

        callback.cancel();
    }

}
