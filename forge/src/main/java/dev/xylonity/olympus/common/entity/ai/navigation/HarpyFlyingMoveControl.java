package dev.xylonity.olympus.common.entity.ai.navigation;

import dev.xylonity.olympus.common.entity.HarpyEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;

/// Based off vanilla's vex move control and companions! golden allays'
/// https://github.com/Xylonity/Companions/blob/v1.20.1/common/src/main/java/dev/xylonity/companions/common/entity/ai/mage/allay/control/GoldenAllayMoveControl.java
public final class HarpyFlyingMoveControl extends MoveControl {

    private static final double SLOWDOWN_DISTANCE = 1.5D;

    private final HarpyEntity harpy;

    public HarpyFlyingMoveControl(final HarpyEntity harpy) {
        super(harpy);
        this.harpy = harpy;
    }

    @Override
    public void tick() {
        if (!hasWanted()) {
            harpy.setYya(0.0F);
            harpy.setZza(0.0F);
            return;
        }

        final Vec3 offset = new Vec3(getWantedX() - harpy.getX(), getWantedY() - harpy.getY(), getWantedZ() - harpy.getZ());
        final double distance = offset.length();
        if (distance < 0.1) {
            setWait();
            harpy.setDeltaMovement(harpy.getDeltaMovement().scale(0.82));
            return;
        }

        final double slowdown = Mth.clamp(distance / SLOWDOWN_DISTANCE, 0.2, 1.0D);
        final double targetSpeed = getSpeedModifier() * harpy.getAttributeValue(Attributes.FLYING_SPEED) * slowdown;
        final Vec3 targetMovement = offset.scale(targetSpeed / distance);
        final double acceleration = distance < SLOWDOWN_DISTANCE ? 0.2 : 0.12;
        Vec3 movement = harpy.getDeltaMovement().lerp(targetMovement, acceleration);

        if (movement.lengthSqr() > distance * distance) {
            movement = movement.normalize().scale(distance);
        }

        harpy.setDeltaMovement(movement);
        rotateForCurrentFlight(movement);

        setWait();
    }

    private void rotateForCurrentFlight(final Vec3 movement) {
        Vec3 direction = movement;
        final LivingEntity target = harpy.getTarget();
        if (target != null && target.isAlive()) {
            direction = new Vec3(target.getX() - harpy.getX(), 0, target.getZ() - harpy.getZ());
        }

        if (direction.x * direction.x + direction.z * direction.z < 1.0E-6D) {
            return;
        }

        final float yaw = (float) (Mth.atan2(-direction.x, direction.z) * Mth.RAD_TO_DEG);
        harpy.setYRot(Mth.rotLerp(0.15f, harpy.getYRot(), yaw));
        harpy.yBodyRot = harpy.getYRot();
    }

}
