package dev.xylonity.olympus.common.entity.ai.harpy.internal;

import dev.xylonity.olympus.common.entity.HarpyEntity;
import dev.xylonity.olympus.common.entity.ai.harpy.AbstractHarpyGoal;
import dev.xylonity.olympus.common.entity.projectile.HarpyProjectileEntity;
import dev.xylonity.olympus.registry.OlympusParticles;
import dev.xylonity.olympus.registry.OlympusSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class HarpyProjectileGoal extends AbstractHarpyGoal {

    private final int projectileReleaseTick;

    private @Nullable LivingEntity target;
    private int strafeTicks;
    private boolean strafeClockwise;
    private boolean projectileReleased;

    public HarpyProjectileGoal(final HarpyEntity harpy, final int attackDuration, final int projectileReleaseTick, final int cooldown) {
        super(harpy, attackDuration, cooldown);
        this.projectileReleaseTick = projectileReleaseTick;
    }

    @Override
    protected boolean canStartAttack() {
        final LivingEntity currentTarget = harpy.getTarget();
        return currentTarget != null && currentTarget.isAlive() && harpy.canStartSpecialAttack() && harpy.distanceToSqr(currentTarget) >= 16 && harpy.hasLineOfSight(currentTarget);
    }

    @Override
    protected void onAttackStarted() {
        target = harpy.getTarget();
        strafeTicks = 0;
        strafeClockwise = harpy.getRandom().nextBoolean();
        projectileReleased = false;
    }

    @Override
    protected void tickAttack() {
        if (target != null && target.isAlive()) {
            harpy.getLookControl().setLookAt(target, 45.0F, 45.0F);

            if (strafeTicks-- <= 0 || harpy.getNavigation().isDone()) {
                startAttackStrafe(target);
            }

        }

    }

    @Override
    protected void onAttackStopped() {
        harpy.getNavigation().stop();
        if (projectileReleased) {
            harpy.delaySpecialAttacks();
        }

        target = null;
        strafeTicks = 0;
        projectileReleased = false;
    }

    @Override
    protected int attackState() {
        return HarpyEntity.STATE_SHOT;
    }

    @Override
    protected int attackTick() {
        return projectileReleaseTick;
    }

    @Override
    protected boolean startsCooldownOnAttack() {
        return true;
    }

    @Override
    protected boolean performAttack() {
        if (!(harpy.level() instanceof ServerLevel serverLevel) || target == null || !target.isAlive()) {
            return false;
        }

        // Front position
        final Vec3 origin = new Vec3(harpy.getX(), harpy.getEyeY() - 0.2D, harpy.getZ());
        final Vec3 direction = target.getBoundingBox().getCenter().subtract(origin).normalize();
        Vec3 lateral = new Vec3(-direction.z, 0.0D, direction.x);
        if (lateral.lengthSqr() < 1.0E-6D) {
            lateral = Vec3.X_AXIS;
        }
        else {
            lateral = lateral.normalize();
        }

        // Elite harpy shots 3 projectiles instead of 1
        final int firstProjectileIdx = harpy.isElite() ? -1 : 0;
        final int lastProjectileIdx = harpy.isElite() ? 1 : 0;
        boolean spawnedProjectile = false;
        for (int projectileIndex = firstProjectileIdx; projectileIndex <= lastProjectileIdx; projectileIndex++) {
            final Vec3 projectileOrigin = origin.add(lateral.scale((double) projectileIndex * 0.45));
            final Vec3 projectileDirection = direction
                    .add(lateral.scale((double) projectileIndex * 0.28))
                    .normalize();

            final HarpyProjectileEntity projectile = new HarpyProjectileEntity(serverLevel, harpy, target);

            projectile.setPos(projectileOrigin.add(projectileDirection.scale(0.65D)));
            projectile.setDeltaMovement(projectileDirection.scale(HarpyProjectileEntity.SPEED));

            spawnedProjectile |= serverLevel.addFreshEntity(projectile);
        }

        if (!spawnedProjectile) {
            return false;
        }

        serverLevel.sendParticles(OlympusParticles.HARPY_MAGIC.get(), origin.x, origin.y, origin.z, 20, 0.12, 0.12, 0.12, 0.18);
        harpy.playSound(OlympusSounds.HARPY_SHOT.get(), 2, 1);

        projectileReleased = true;
        return true;
    }

    private void startAttackStrafe(final LivingEntity currentTarget) {
        Vec3 away = new Vec3(harpy.getX() - currentTarget.getX(), 0, harpy.getZ() - currentTarget.getZ());
        if (away.lengthSqr() < 1.0E-6D) {
            away = new Vec3(1, 0, 0);
        }
        else {
            away = away.normalize();
        }

        final Vec3 tangent = strafeClockwise ? new Vec3(-away.z, 0.0D, away.x) : new Vec3(away.z, 0.0D, -away.x);
        final Vec3 horizontalStrafe = harpy.position().add(tangent.scale(4.0D));
        final Vec3 strafe = harpy.findFreeCombatPosition(currentTarget, horizontalStrafe.x, currentTarget.getEyeY() + 1.25D, horizontalStrafe.z);
        if (strafe == null || !harpy.getNavigation().moveTo(strafe.x, strafe.y, strafe.z, 0.65D)) {
            strafeClockwise = !strafeClockwise;
        }

        strafeTicks = 8;
    }

}
