package dev.xylonity.olympus.common.entity.ai.harpy.internal;

import dev.xylonity.olympus.common.entity.HarpyEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HarpyProjectileDodgeGoal extends Goal {

    private static final double MIN_SPEED_SQR = 0.0025D;

    private final HarpyEntity harpy;

    private final Map<UUID, Long> projectiles = new HashMap<>();

    private @Nullable Vec3 targetProjectile;
    private Vec3 dodgeDirection = Vec3.ZERO;
    private int dodgeTicks;
    private long nextDodgeGameTime;

    public HarpyProjectileDodgeGoal(final HarpyEntity harpy) {
        this.harpy = harpy;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        targetProjectile = null;
        dodgeDirection = Vec3.ZERO;

        // If dashing
        final long gameTime = harpy.level().getGameTime();
        if (!harpy.isAlive() || gameTime < nextDodgeGameTime || isDashState()) {
            return false;
        }

        // Closest projectile with the lowest impact time
        projectiles.entrySet().removeIf(entry -> entry.getValue() <= gameTime);
        final Projectile projectile = findMostImmediateThreat();
        if (projectile == null) {
            return false;
        }

        // Dodges only on x chance
        projectiles.put(projectile.getUUID(), gameTime + 60);
        if (harpy.getRandom().nextFloat() >= 0.4) {
            return false;
        }

        // Searches for any projectile whose direction is the inverse of the harpy's look direction (within a correction margin)
        targetProjectile = findDodgeTarget(projectile);
        if (targetProjectile == null) {
            return false;
        }

        dodgeDirection = targetProjectile.subtract(harpy.position()).normalize();

        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return targetProjectile != null && dodgeTicks > 0 && !harpy.horizontalCollision && !harpy.verticalCollision && harpy.distanceToSqr(targetProjectile) > 2;
    }

    @Override
    public void start() {
        if (targetProjectile == null) {
            return;
        }

        harpy.getNavigation().stop();
        harpy.setAttackState(HarpyEntity.STATE_FLY);
        harpy.addDeltaMovement(dodgeDirection.scale(0.28));
        harpy.getMoveControl().setWantedPosition(targetProjectile.x, targetProjectile.y, targetProjectile.z, 1.5);
        dodgeTicks = 9;
        nextDodgeGameTime = harpy.level().getGameTime() + 12;
    }

    @Override
    public void tick() {
        dodgeTicks--;
        if (targetProjectile != null) {
            harpy.getMoveControl().setWantedPosition(targetProjectile.x, targetProjectile.y, targetProjectile.z, 1.5);
        }

    }

    @Override
    public void stop() {
        harpy.getMoveControl().setWait();
        harpy.setDeltaMovement(harpy.getDeltaMovement().scale(0.9D));
        if (harpy.getAttackState() == HarpyEntity.STATE_FLY) {
            harpy.setAttackState(HarpyEntity.STATE_IDLE);
        }

        targetProjectile = null;
        dodgeDirection = Vec3.ZERO;
        dodgeTicks = 0;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private @Nullable Projectile findMostImmediateThreat() {
        Projectile mostImmediateThreat = null;
        double shortestImpactTime = Double.POSITIVE_INFINITY;

        for (final Projectile projectile : harpy.level().getEntitiesOfClass(Projectile.class, harpy.getBoundingBox().inflate(30), this::canConsiderProjectile)) {
            if (projectiles.containsKey(projectile.getUUID())) {
                continue;
            }

            final double impactTime = getPredictedImpactTime(projectile);
            if (impactTime < shortestImpactTime) {
                shortestImpactTime = impactTime;
                mostImmediateThreat = projectile;
            }

        }

        return mostImmediateThreat;
    }

    private boolean canConsiderProjectile(final Projectile projectile) {
        if (!projectile.isAlive()) {
            return false;
        }

        final Entity owner = projectile.getOwner();
        return owner != harpy && !(owner instanceof HarpyEntity) && !harpy.isAlliedTo(owner);
    }

    private double getPredictedImpactTime(final Projectile projectile) {
        final Vec3 position = harpy.getBoundingBox().getCenter().subtract(projectile.getBoundingBox().getCenter());
        final Vec3 speed = projectile.getDeltaMovement().subtract(harpy.getDeltaMovement());
        final double speedSqr = speed.lengthSqr();
        if (speedSqr < MIN_SPEED_SQR) {
            return Double.POSITIVE_INFINITY;
        }

        final double timeToHit = position.dot(speed) / speedSqr;
        if (timeToHit <= 0 || timeToHit > 30) {
            return Double.POSITIVE_INFINITY;
        }

        final Vec3 closestOffset = position.subtract(speed.scale(timeToHit));
        final double radius = harpy.getBbWidth() * 0.5D + projectile.getBbWidth() * 0.5D + 1;
        return closestOffset.lengthSqr() <= radius * radius ? timeToHit : Double.POSITIVE_INFINITY;
    }

    private @Nullable Vec3 findDodgeTarget(final Projectile projectile) {
        Vec3 direction = projectile.getDeltaMovement().subtract(harpy.getDeltaMovement());
        direction = new Vec3(direction.x, 0.0D, direction.z);
        if (direction.lengthSqr() < MIN_SPEED_SQR) {
            final Vec3 lookDirection = harpy.getLookAngle();
            direction = new Vec3(lookDirection.x, 0.0D, lookDirection.z);
        }
        if (direction.lengthSqr() < MIN_SPEED_SQR) {
            direction = Vec3.Z_AXIS;
        }

        direction = direction.normalize();
        Vec3 lateralDirection = new Vec3(-direction.z, 0.0D, direction.x);
        if (harpy.getRandom().nextBoolean()) {
            lateralDirection = lateralDirection.reverse();
        }

        // Dodge distance
        for (int side = 0; side < 2; side++) {
            final Vec3 candidate = harpy.position().add(lateralDirection.scale(8));
            if (isClearDodgePath(candidate)) {
                return candidate;
            }

            lateralDirection = lateralDirection.reverse();
        }

        return null;
    }

    private boolean isClearDodgePath(final Vec3 candidate) {
        final Vec3 offset = candidate.subtract(harpy.position());
        if (!harpy.level().noCollision(harpy, harpy.getBoundingBox().move(offset))) {
            return false;
        }

        return harpy.level().clip(new ClipContext(harpy.getBoundingBox().getCenter(), candidate.add(0, harpy.getBbHeight() * 0.5, 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, harpy)).getType() == HitResult.Type.MISS;
    }

    private boolean isDashState() {
        final int state = harpy.getAttackState();
        return state == HarpyEntity.STATE_DASH_PREPARING || state == HarpyEntity.STATE_DASHING || state == HarpyEntity.STATE_DASH_ENDING;
    }

}