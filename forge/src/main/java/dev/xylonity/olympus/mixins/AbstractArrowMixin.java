package dev.xylonity.olympus.mixins;

import dev.xylonity.olympus.common.util.ArtemisArrow;
import dev.xylonity.olympus.config.OlympusConfig;
import dev.xylonity.olympus.registry.OlympusAttachments;
import dev.xylonity.olympus.registry.OlympusParticles;
import dev.xylonity.knightlib.api.entity.data.Attachments;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/// Computes mutations on thrown arrows when using the artemis bow
@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin implements ArtemisArrow {

    @Shadow
    private double baseDamage;

    @Shadow
    protected boolean inGround;

    @Override
    public boolean olympus$isArtemisArrow() {
        return Attachments.get((AbstractArrow) (Object) this, OlympusAttachments.ARTEMIS_ARROW);
    }

    @Override
    public void olympus$setArtemisArrow(final boolean artemisArrow) {
        final AbstractArrow arrow = (AbstractArrow) (Object) this;
        if (artemisArrow) {
            Attachments.set(arrow, OlympusAttachments.ARTEMIS_ARROW, true);
        }
        else {
            Attachments.remove(arrow, OlympusAttachments.ARTEMIS_ARROW);
        }

    }

    @Inject(method = "isCritArrow", at = @At("HEAD"), cancellable = true)
    private void olympus$hideCriticalParticles(final CallbackInfoReturnable<Boolean> callback) {
        // Not showing critical particles (max bow charge) if it's shot by the artemis bow
        final AbstractArrow arrow = (AbstractArrow) (Object) this;
        if (arrow.level().isClientSide && olympus$isArtemisArrow()) {
            callback.setReturnValue(false);
        }

    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void olympus$spawnArtemisTrail(final CallbackInfo callback) {
        final AbstractArrow arrow = (AbstractArrow) (Object) this;
        if (!arrow.level().isClientSide || !olympus$isArtemisArrow() || !arrow.isAlive() || inGround) {
            return;
        }

        // Additional particles
        final Vec3 movement = arrow.getDeltaMovement();
        if (movement.lengthSqr() <= 1.0E-8D) {
            return;
        }

        final RandomSource random = arrow.level().getRandom();
        for (int i = 0; i < 3; i++) {
            final ParticleOptions particle = (i & 1) == 0 ? OlympusParticles.ARTEMIS_ARROW_TRACE.get() : OlympusParticles.ARTEMIS_ARROW_TRACE_SMALL.get();
            olympus$spawnLeaf(arrow, particle, movement, (i + random.nextDouble()) / 4D);
        }

    }

    @Inject(method = "onHitEntity", at = @At("HEAD"), cancellable = true)
    private void olympus$healOwnedPet(final EntityHitResult hitResult, final CallbackInfo callback) {
        // Whether if the arrow comes from the artemis bow
        final AbstractArrow arrow = (AbstractArrow) (Object) this;
        if (!olympus$isArtemisArrow() || !(hitResult.getEntity() instanceof TamableAnimal pet) || !(arrow.getOwner() instanceof Player player) || !pet.isOwnedBy(player)) {
            return;
        }

        // Heals tameable entities
        if (arrow.level() instanceof ServerLevel level) {
            final DamageSource damageSource = arrow.damageSources().arrow(arrow, player);
            int damage = Mth.ceil(Mth.clamp(arrow.getDeltaMovement().length() * baseDamage, 0, Integer.MAX_VALUE));
            if (arrow.isCritArrow()) {
                damage = (int) Math.min((long) damage + arrow.level().getRandom().nextInt(damage / 2 + 2), Integer.MAX_VALUE);
            }

            // Heal and sound
            pet.heal(damage * (float) OlympusConfig.ARTEMIS_BOW_TAMED_HEALING_MULTIPLIER);
            player.level().playSound(null, pet.blockPosition(), SoundEvents.AMETHYST_BLOCK_FALL, SoundSource.AMBIENT, 1, 1.5F);

            // Particles
            final double y = pet.getY() + pet.getBbHeight() * 0.5D;
            level.sendParticles(OlympusParticles.ARTEMIS_ARROW_TRACE.get(), pet.getX(), y, pet.getZ(), 16, pet.getBbWidth() * 0.08D, pet.getBbHeight() * 0.08D, pet.getBbWidth() * 0.08D, 0.14D);
            level.sendParticles(OlympusParticles.ARTEMIS_ARROW_TRACE_SMALL.get(), pet.getX(), y, pet.getZ(), 20, pet.getBbWidth() * 0.08D, pet.getBbHeight() * 0.08D, pet.getBbWidth() * 0.08D, 0.11D);
        }

        arrow.discard();
        callback.cancel();
    }

    @Unique
    private static void olympus$spawnLeaf(final AbstractArrow arrow, final ParticleOptions particle, final Vec3 movement, final double trailProgress) {
        // Randomized position
        final RandomSource random = arrow.level().getRandom();
        final Vec3 origin = arrow.position().subtract(movement.scale(trailProgress));
        final Vec3 speed = movement.normalize().scale(-(0.008D + random.nextDouble() * 0.01D)).add(
                (random.nextDouble() - 0.5D) * 0.012,
                (random.nextDouble() - 0.5D) * 0.008,
                (random.nextDouble() - 0.5D) * 0.012
        );
        final double radius = Math.cbrt(random.nextDouble()) * 0.28D;
        final double direction = random.nextDouble() * 2 - 1;
        final double horizontalRadius = Math.sqrt(1 - direction * direction) * radius;
        final double angle = random.nextDouble() * Math.PI * 2;
        arrow.level().addParticle(particle, origin.x + Math.cos(angle) * horizontalRadius, origin.y + direction * radius, origin.z + Math.sin(angle) * horizontalRadius, speed.x, speed.y, speed.z);
    }

}
