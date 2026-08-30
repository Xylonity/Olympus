package dev.xylonity.olympus.common.entity.ai.harpy.internal;

import dev.xylonity.olympus.common.entity.HarpyEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;

public final class HarpyFlightGoal extends Goal {

    private static final double MIN_HEIGHT = 3;
    private static final int RANGE = 6;
    private static final int MIN_HOVER_TICKS = 25;
    private static final int MIN_TRAVEL_TICKS = 30;

    private final HarpyEntity harpy;

    private @Nullable Vec3 destination;
    private int hoverTicks;
    private int travelTicks;

    public HarpyFlightGoal(final HarpyEntity harpy) {
        this.harpy = harpy;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return hasNoTarget() && isInFlightState();
    }

    @Override
    public boolean canContinueToUse() {
        return hasNoTarget() && isInFlightState();
    }

    @Override
    public void start() {
        hoverTicks = 0;
    }

    @Override
    public void tick() {
        if (destination != null) {
            if (canKeepTravelling()) {
                travelTicks--;
                return;
            }

            stopTravelling();

            hoverTicks = MIN_HOVER_TICKS + harpy.getRandom().nextInt(36);
        }

        hoverInPlace();

        final Vec3 liftDestination = findRequiredLiftDestination();
        if (liftDestination != null && startTravelling(liftDestination, MIN_TRAVEL_TICKS)) {
            return;
        }

        if (hoverTicks-- > 0) {
            return;
        }

        final Vec3 destination = findDestination();
        if (destination == null || !startTravelling(destination, MIN_TRAVEL_TICKS + harpy.getRandom().nextInt(31))) {
            hoverTicks = MIN_HOVER_TICKS;
        }

    }

    @Override
    public void stop() {
        stopTravelling();
        hoverTicks = 0;
    }

    private boolean isInFlightState() {
        final int state = harpy.getAttackState();
        return state == HarpyEntity.STATE_IDLE || state == HarpyEntity.STATE_FLY || state == HarpyEntity.STATE_DASH_ENDING;
    }

    private boolean hasNoTarget() {
        return harpy.getTarget() == null || !harpy.getTarget().isAlive();
    }

    private boolean canKeepTravelling() {
        return travelTicks > 0 && !harpy.getNavigation().isDone() && destination != null && harpy.distanceToSqr(destination) > 0.6;
    }

    private boolean startTravelling(final Vec3 target, final int duration) {
        if (!harpy.getNavigation().moveTo(target.x, target.y, target.z, 1)) {
            return false;
        }

        destination = target;
        travelTicks = duration;
        if (harpy.getAttackState() != HarpyEntity.STATE_FLY) {
            harpy.setAttackState(HarpyEntity.STATE_FLY);
        }

        return true;
    }

    private void hoverInPlace() {
        final int state = harpy.getAttackState();
        if (state == HarpyEntity.STATE_FLY || state == HarpyEntity.STATE_DASH_ENDING) {
            harpy.setAttackState(HarpyEntity.STATE_IDLE);
        }

        harpy.getMoveControl().setWait();
        harpy.setDeltaMovement(harpy.getDeltaMovement().scale(0.55D));
    }

    private void stopTravelling() {
        harpy.getNavigation().stop();
        destination = null;
        travelTicks = 0;
        hoverInPlace();
    }

    private @Nullable Vec3 findDestination() {
        final Level level = harpy.level();
        for (int attempt = 0; attempt < 8; attempt++) {
            final double x = harpy.getX() + harpy.getRandom().nextInt(RANGE * 2 + 1) - RANGE;
            final double z = harpy.getZ() + harpy.getRandom().nextInt(RANGE * 2 + 1) - RANGE;
            final int groundY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(x), Mth.floor(z));
            final double y = Mth.clamp(harpy.getY() + (harpy.getRandom().nextDouble() - 0.5D) * 4.0D, groundY + MIN_HEIGHT, groundY + 6);
            final Vec3 possibility = new Vec3(x, y, z);

            if (isValidDestination(possibility)) {
                return possibility;
            }

        }

        return null;
    }

    private @Nullable Vec3 findRequiredLiftDestination() {
        final int groundY = harpy.level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, harpy.blockPosition().getX(), harpy.blockPosition().getZ());
        final double minimumY = groundY + MIN_HEIGHT;
        if (harpy.getY() >= minimumY - 0.25D) {
            return null;
        }

        final Vec3 possibility = new Vec3(harpy.getX(), minimumY, harpy.getZ());
        return isValidDestination(possibility) ? possibility : null;
    }

    private boolean isValidDestination(final Vec3 candidate) {
        final Vec3 offset = candidate.subtract(harpy.position());
        return harpy.level().noCollision(harpy, harpy.getBoundingBox().move(offset));
    }

}