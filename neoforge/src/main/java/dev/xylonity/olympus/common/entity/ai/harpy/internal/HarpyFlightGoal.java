package dev.xylonity.olympus.common.entity.ai.harpy.internal;

import dev.xylonity.olympus.common.entity.HarpyEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;

public final class HarpyFlightGoal extends Goal {

    private static final double MIN_HEIGHT = 2.75D;
    private static final int RANGE = 6;
    private static final int MIN_HOVER_TICKS = 25;
    private static final int MIN_TRAVEL_TICKS = 30;

    private final HarpyEntity harpy;

    private @Nullable Vec3 destination;
    private int hoverTicks;
    private int travelTicks;

    public HarpyFlightGoal(final HarpyEntity harpy) {
        this.harpy = harpy;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return isInFlightState();
    }

    @Override
    public boolean canContinueToUse() {
        return isInFlightState();
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
                flyTowards(destination);
                return;
            }

            stopTravelling();

            hoverTicks = MIN_HOVER_TICKS + harpy.getRandom().nextInt(36);
        }

        hoverInPlace();

        final Vec3 destination = findRequiredLiftDestination();
        if (destination != null) {
            this.destination = destination;
            travelTicks = MIN_TRAVEL_TICKS;
            flyTowards(this.destination);
            return;
        }

        if (hoverTicks-- > 0) {
            return;
        }

        this.destination = findDestination();
        if (this.destination == null) {
            hoverTicks = MIN_HOVER_TICKS;
            return;
        }

        travelTicks = MIN_TRAVEL_TICKS + harpy.getRandom().nextInt(31);

        flyTowards(this.destination);
    }

    @Override
    public void stop() {
        stopTravelling();
        hoverTicks = 0;
    }

    private boolean isInFlightState() {
        final int state = harpy.getAttackState();
        return state == HarpyEntity.STATE_IDLE || state == HarpyEntity.STATE_FLY;
    }

    private boolean canKeepTravelling() {
        return travelTicks > 0 && !harpy.horizontalCollision && !harpy.verticalCollision && harpy.distanceToSqr(destination) > 0.6;
    }

    private void flyTowards(final Vec3 target) {
        if (harpy.getAttackState() != HarpyEntity.STATE_FLY) {
            harpy.setAttackState(HarpyEntity.STATE_FLY);
        }

        harpy.getMoveControl().setWantedPosition(target.x, target.y, target.z, 1.0D);
        harpy.getLookControl().setLookAt(target.x, target.y, target.z, 20.0F, 20.0F);
    }

    private void hoverInPlace() {
        if (harpy.getAttackState() == HarpyEntity.STATE_FLY) {
            harpy.setAttackState(HarpyEntity.STATE_IDLE);
        }

        harpy.getMoveControl().setWait();
        harpy.setDeltaMovement(harpy.getDeltaMovement().scale(0.55D));
    }

    private void stopTravelling() {
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
            final double y = Mth.clamp(harpy.getY() + (harpy.getRandom().nextDouble() - 0.5D) * 4.0D, groundY + MIN_HEIGHT, groundY + 7);
            final Vec3 candidate = new Vec3(x, y, z);

            if (isClearPath(candidate)) {
                return candidate;
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

        final Vec3 candidate = new Vec3(harpy.getX(), minimumY, harpy.getZ());
        return isClearPath(candidate) ? candidate : null;
    }

    private boolean isClearPath(final Vec3 candidate) {
        final Level level = harpy.level();
        final Vec3 offset = candidate.subtract(harpy.position());

        return level.noCollision(harpy, harpy.getBoundingBox().move(offset)) && level.clip(new ClipContext(harpy.getBoundingBox().getCenter(), candidate.add(0.0D, harpy.getBbHeight() * 0.5D, 0.0D),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, harpy)).getType() == HitResult.Type.MISS;
    }

}