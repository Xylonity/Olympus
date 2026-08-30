package dev.xylonity.olympus.common.entity.ai.harpy.internal;

import dev.xylonity.olympus.common.entity.HarpyEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;

public class HarpyRetreatGoal extends Goal {

    private static final double INNER_ORBIT_DISTANCE = 4.5D;
    private static final double NORMAL_ORBIT_DISTANCE = 7.5D;
    private static final double OUTER_ORBIT_DISTANCE = 8.5D;

    private final HarpyEntity harpy;

    private @Nullable Vec3 destination;
    private int steeringTicks;
    private int orbitDirectionTicks;
    private boolean clockwise;

    public HarpyRetreatGoal(final HarpyEntity harpy) {
        this.harpy = harpy;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return hasLiveTarget() && isInFlightState();
    }

    @Override
    public boolean canContinueToUse() {
        return hasLiveTarget() && isInFlightState();
    }

    @Override
    public void start() {
        destination = null;
        steeringTicks = 0;
        clockwise = harpy.getRandom().nextBoolean();
        resetOrbitDirectionTimer();
    }

    @Override
    public void tick() {
        final LivingEntity target = harpy.getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }

        harpy.getLookControl().setLookAt(target, 45.0F, 45.0F);

        if (--orbitDirectionTicks <= 0) {
            clockwise = !clockwise;
            resetOrbitDirectionTimer();
            destination = null;
        }

        if (destination != null && steeringTicks-- > 0 && !harpy.horizontalCollision && !harpy.verticalCollision && !harpy.getNavigation().isDone()) {
            setFlyingState();
            return;
        }

        steerAround(target);
    }

    @Override
    public void stop() {
        destination = null;
        steeringTicks = 0;
        orbitDirectionTicks = 0;
        stopMovement();
    }

    private void steerAround(final LivingEntity target) {
        for (int attempt = 0; attempt < 3; attempt++) {
            final Vec3 destination = findCombatDestination(target, attempt);
            final double horizontalDistance = horizontalDistanceTo(target);
            final double speedModifier = horizontalDistance < INNER_ORBIT_DISTANCE ? 1.8 : horizontalDistance > OUTER_ORBIT_DISTANCE ? 1 : 0.82;
            if (steerTowards(destination, speedModifier)) {
                return;
            }

            clockwise = !clockwise;
        }

        coastInPlace();
    }

    private Vec3 findCombatDestination(final LivingEntity target, final int attempt) {
        // Direction to the target
        Vec3 direction = new Vec3(harpy.getX() - target.getX(), 0, harpy.getZ() - target.getZ());
        if (direction.lengthSqr() < 1.0E-6D) {
            final double angle = harpy.getRandom().nextDouble() * Mth.TWO_PI;
            direction = new Vec3(Mth.cos((float) angle), 0, Mth.sin((float) angle));
        }
        else {
            direction = direction.normalize();
        }

        // Proper radial distnace
        Vec3 orbitDirection = clockwise ? new Vec3(-direction.z, 0, direction.x) : new Vec3(direction.z, 0.0D, -direction.x);
        final double horizontalDistance = horizontalDistanceTo(target);
        final double correction = getRadialCorrection(horizontalDistance);

        if (attempt == 1) {
            orbitDirection = orbitDirection.scale(0.45D);
        }
        else if (attempt == 2) {
            orbitDirection = correction >= 0.0D ? direction : direction.reverse();
        }

        Vec3 flightDirection = orbitDirection.add(direction.scale(correction));
        if (flightDirection.lengthSqr() < 1.0E-6D) {
            flightDirection = orbitDirection;
        }

        flightDirection = flightDirection.normalize();

        final double look = horizontalDistance > OUTER_ORBIT_DISTANCE ? 5 + 2 : 5;
        final double x = harpy.getX() + flightDirection.x * look;
        final double z = harpy.getZ() + flightDirection.z * look;

        final Level level = harpy.level();
        final int groundY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(x), Mth.floor(z));
        final double heightWave = Mth.sin((harpy.tickCount + getHarpyPhase()) * 0.09F) * 0.8;
        final double desiredY = target.getEyeY() + 2.25 + heightWave;
        // Min/max altitude distance
        final double y = Mth.clamp(Mth.clamp(desiredY, harpy.getY() - 2.0, harpy.getY() + 2.0), groundY + 3, groundY + 8);

        return new Vec3(x, y, z);
    }

    private double getRadialCorrection(final double horizontalDistance) {
        if (horizontalDistance < INNER_ORBIT_DISTANCE) {
            return Mth.clamp((NORMAL_ORBIT_DISTANCE - horizontalDistance) / NORMAL_ORBIT_DISTANCE, 0.45D, 1.15D);
        }

        if (horizontalDistance > OUTER_ORBIT_DISTANCE) {
            return -Mth.clamp((horizontalDistance - NORMAL_ORBIT_DISTANCE) / NORMAL_ORBIT_DISTANCE, 0.45D, 1.25D);
        }

        return Mth.clamp((NORMAL_ORBIT_DISTANCE - horizontalDistance) / 8.0, -0.25D, 0.25D);
    }

    private double horizontalDistanceTo(final LivingEntity target) {
        return Math.sqrt(Mth.square(harpy.getX() - target.getX()) + Mth.square(harpy.getZ() - target.getZ()));
    }

    private int getHarpyPhase() {
        return harpy.getId() * 13;
    }

    private boolean hasLiveTarget() {
        return harpy.getTarget() != null && harpy.getTarget().isAlive();
    }

    private boolean isInFlightState() {
        final int state = harpy.getAttackState();
        return state == HarpyEntity.STATE_IDLE || state == HarpyEntity.STATE_FLY || state == HarpyEntity.STATE_DASH_ENDING;
    }

    private void setFlyingState() {
        if (harpy.getAttackState() != HarpyEntity.STATE_FLY) {
            harpy.setAttackState(HarpyEntity.STATE_FLY);
        }

    }

    private boolean steerTowards(final Vec3 target, final double speedModifier) {
        if (!harpy.getNavigation().moveTo(target.x, target.y, target.z, speedModifier)) {
            return false;
        }

        destination = target;
        steeringTicks = 8 + harpy.getRandom().nextInt(7);
        setFlyingState();

        return true;
    }

    private void resetOrbitDirectionTimer() {
        orbitDirectionTicks = 45 + harpy.getRandom().nextInt(56);
    }

    private void coastInPlace() {
        destination = null;
        steeringTicks = 0;

        harpy.getNavigation().stop();
        harpy.getMoveControl().setWait();
        harpy.setDeltaMovement(harpy.getDeltaMovement().scale(0.96));

        if (isActiveFlightState()) {
            harpy.setAttackState(HarpyEntity.STATE_IDLE);
        }

    }

    private void stopMovement() {
        harpy.getNavigation().stop();
        harpy.getMoveControl().setWait();
        harpy.setDeltaMovement(harpy.getDeltaMovement().scale(0.96));

        if (isActiveFlightState()) {
            harpy.setAttackState(HarpyEntity.STATE_IDLE);
        }

    }

    private boolean isActiveFlightState() {
        final int state = harpy.getAttackState();
        return state == HarpyEntity.STATE_FLY || state == HarpyEntity.STATE_DASH_ENDING;
    }

}
