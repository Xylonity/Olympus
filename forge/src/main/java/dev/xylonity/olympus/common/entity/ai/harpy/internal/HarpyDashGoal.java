package dev.xylonity.olympus.common.entity.ai.harpy.internal;

import dev.xylonity.olympus.common.entity.HarpyEntity;
import dev.xylonity.olympus.registry.OlympusSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;

public class HarpyDashGoal extends Goal {

    private static final double MAX_DASH_SPEED = 1.1;
    private static final double ENDING_START_PROGRESS = 0.78;

    private static final float BASE_DASH_DAMAGE = 6;

    private final HarpyEntity harpy;
    private final int preparationDuration;
    private final int endingDuration;

    private @Nullable LivingEntity target;

    private Phase phase = Phase.PREPARING;

    private Vec3 inboundControl = Vec3.ZERO;
    private Vec3 outboundControl = Vec3.ZERO;

    private Vec3 curveStart = Vec3.ZERO;
    private Vec3 curveBottom = Vec3.ZERO;
    private Vec3 curveEnd = Vec3.ZERO;
    private double curveLength = 1.0D;
    private double curveProgress;

    private int phaseTicks;
    private int movementTicks;

    private int preparationTicks;
    private long cooldownEnd;

    private boolean dashLaunched;
    private boolean movementFinished;
    private boolean targetHit;
    private boolean goalFinished;

    public HarpyDashGoal(final HarpyEntity harpy, final int preparationDuration, final int endingDuration) {
        this.harpy = harpy;
        this.preparationDuration = preparationDuration;
        this.endingDuration = endingDuration;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        final LivingEntity currentTarget = harpy.getTarget();
        if (currentTarget == null || !currentTarget.isAlive() || harpy.level().getGameTime() < cooldownEnd || !harpy.canStartSpecialAttack() || !isInFlightState() || !harpy.hasLineOfSight(currentTarget)) {
            return false;
        }

        final double distanceSqr = harpy.distanceToSqr(currentTarget);
        return distanceSqr >= 25 && distanceSqr <= 225;
    }

    @Override
    public boolean canContinueToUse() {
        return harpy.isAlive() && !goalFinished && (dashLaunched || target != null && target.isAlive());
    }

    @Override
    public void start() {
        target = harpy.getTarget();
        phase = Phase.PREPARING;
        phaseTicks = 0;
        movementTicks = 0;
        preparationTicks = 0;
        curveProgress = 0.0D;
        dashLaunched = false;
        movementFinished = false;
        targetHit = false;
        goalFinished = false;
        harpy.noPhysics = false;
        harpy.setAttackState(HarpyEntity.STATE_DASH_PREPARING);
    }

    @Override
    public void tick() {
        if (phase == Phase.PREPARING) {
            tickPreparation();
        }
        else if (phase == Phase.DASHING) {
            tickDashMovement();
        }
        else {
            tickEnding();
        }

    }

    @Override
    public void stop() {
        harpy.getNavigation().stop();
        harpy.getMoveControl().setWait();
        harpy.setDeltaMovement(Vec3.ZERO);
        harpy.noPhysics = false;
        harpy.escapeFromBlocks();

        final boolean waitingForPostDash = dashLaunched && phase == Phase.ENDING;
        if (isDashState() && !waitingForPostDash) {
            harpy.setAttackState(HarpyEntity.STATE_IDLE);
        }

        if (dashLaunched) {
            cooldownEnd = harpy.level().getGameTime() + 6 * 20;
            harpy.delaySpecialAttacks();
        }
        else {
            cooldownEnd = harpy.level().getGameTime() + preparationDuration;
        }

        target = null;
        goalFinished = false;
        movementFinished = true;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    private void tickPreparation() {
        if (target == null || !target.isAlive()) {
            goalFinished = true;
            return;
        }

        phaseTicks++;
        harpy.getLookControl().setLookAt(target, 45, 45);
        if (preparationTicks-- <= 0 || harpy.getNavigation().isDone()) {
            prepareTowardsTarget(target);
        }

        if (phaseTicks >= preparationDuration) {
            initialiseDashCurve(target);
            harpy.getNavigation().stop();
            harpy.getMoveControl().setWait();
            harpy.setDeltaMovement(Vec3.ZERO);
            harpy.horizontalCollision = false;
            harpy.verticalCollision = false;
            harpy.noPhysics = true;
            harpy.setAttackState(HarpyEntity.STATE_DASHING);
            phase = Phase.DASHING;
            phaseTicks = 0;
            dashLaunched = true;
        }

    }

    private void prepareTowardsTarget(final LivingEntity currentTarget) {
        Vec3 difference = new Vec3(harpy.getX() - currentTarget.getX(), 0, harpy.getZ() - currentTarget.getZ());
        if (difference.lengthSqr() < 1.0E-6D) {
            difference = new Vec3(harpy.getLookAngle().x, 0.0D, harpy.getLookAngle().z).reverse();
        }

        if (difference.lengthSqr() < 1.0E-6D) {
            difference = Vec3.Z_AXIS;
        }
        else {
            difference = difference.normalize();
        }

        final double x = currentTarget.getX() + difference.x * 4.0D;
        final double z = currentTarget.getZ() + difference.z * 4.0D;
        final Vec3 destination = harpy.findFreeCombatPosition(currentTarget, x, currentTarget.getEyeY() + 1.5D, z);
        if (destination != null) {
            harpy.getNavigation().moveTo(destination.x, destination.y, destination.z, 0.55D);
        }

        preparationTicks = 4;
    }

    private void initialiseDashCurve(final LivingEntity currentTarget) {
        curveStart = harpy.position();
        final Vec3 targetCenter = currentTarget.getBoundingBox().getCenter();
        final double lowestY = findLowestY(targetCenter.x, targetCenter.z, currentTarget.getBoundingBox().minY + 0.2);
        curveBottom = new Vec3(targetCenter.x, lowestY, targetCenter.z);

        Vec3 horizontalDistance = new Vec3(curveBottom.x - curveStart.x, 0, curveBottom.z - curveStart.z);
        if (horizontalDistance.lengthSqr() < 1.0E-6D) {
            horizontalDistance = new Vec3(harpy.getLookAngle().x, 0.0D, harpy.getLookAngle().z);
        }

        if (horizontalDistance.lengthSqr() < 1.0E-6D) {
            horizontalDistance = Vec3.Z_AXIS;
        }
        else {
            horizontalDistance = horizontalDistance.normalize();
        }

        // Altitude depends on the target's position rather than the height map
        final double horizontalDistance2 = Math.max(1.0D, horizontalDistance(curveStart, curveBottom));
        final double x = curveBottom.x + horizontalDistance.x * horizontalDistance2;
        final double z = curveBottom.z + horizontalDistance.z * horizontalDistance2;
        final double y = currentTarget.getEyeY() + 1.75D;
        final Vec3 curveEnd = harpy.findFreeCombatPosition(currentTarget, x, y, z);
        this.curveEnd = curveEnd != null ? curveEnd : curveStart;

        final double distance = Mth.clamp(horizontalDistance2 * 0.45D, 2, 5);
        inboundControl = curveBottom.subtract(horizontalDistance.scale(distance));
        outboundControl = curveBottom.add(horizontalDistance.scale(distance));

        curveLength = approximateCurveLength();
        curveProgress = 0.0D;
        movementTicks = 0;
    }

    private void tickDashMovement() {
        phaseTicks++;
        if (phaseTicks == 3) {
            harpy.level().playSound(null, harpy, OlympusSounds.HARPY_DASH.get(), harpy.getSoundSource(), 2.0F, 1.0F);
        }

        if (!advanceAlongCurve()) {
            beginEnding(true);
            return;
        }

        if (phase == Phase.DASHING && curveProgress >= ENDING_START_PROGRESS) {
            beginEnding(false);
        }

    }

    private void tickEnding() {
        phaseTicks++;
        if (!movementFinished && !advanceAlongCurve()) {
            finishMovement();
        }

        if (phaseTicks >= endingDuration) {
            finishMovement();
            goalFinished = true;
        }

    }

    private boolean advanceAlongCurve() {
        if (movementFinished || curveProgress >= 1.0D) {
            return false;
        }

        movementTicks++;
        if (movementTicks > 80) {
            return false;
        }

        final double speed = getDashSpeed(curveProgress);
        final double next = Math.min(1.0D, curveProgress + speed / curveLength);
        final Vec3 nextPosition = sampleCurve(next);
        Vec3 movement = nextPosition.subtract(harpy.position());
        final double correctionSpeed = speed * 1.35D;
        if (movement.lengthSqr() > correctionSpeed * correctionSpeed) {
            movement = movement.normalize().scale(correctionSpeed);
        }

        if (movement.lengthSqr() < 1.0E-6D) {
            return false;
        }

        harpy.getNavigation().stop();
        harpy.getMoveControl().setWait();
        harpy.setDeltaMovement(movement);

        faceMovement(movement);
        final boolean blockedByShield = damageTargetAcross(movement);
        if (blockedByShield) {
            beginEnding(true);
            return true;
        }

        curveProgress = next;

        return true;
    }

    private double findLowestY(final double x, final double z, final double minimumY) {
        for (double lift = 0.0D; lift <= 3; lift += 0.25) {
            final double possibleY = minimumY + lift;
            final AABB crossingBox = harpy.getBoundingBox().move(x - harpy.getX(), possibleY - harpy.getY(), z - harpy.getZ()).deflate(1.0E-4D);
            if (harpy.level().noBlockCollision(harpy, crossingBox)) {
                return possibleY;
            }

        }

        return minimumY + 3;
    }

    private double getDashSpeed(final double progress) {
        if (progress < 0.18) {
            final double acceleration = smoothstep(progress / 0.18);
            return Mth.lerp(acceleration, 0.35, MAX_DASH_SPEED);
        }

        if (progress >= ENDING_START_PROGRESS) {
            final double endingProgress = smoothstep((progress - ENDING_START_PROGRESS) / (1.0D - ENDING_START_PROGRESS));
            // Ending dash speed
            return Mth.lerp(endingProgress, MAX_DASH_SPEED, 0.12);
        }

        return MAX_DASH_SPEED;
    }

    private Vec3 sampleCurve(final double progress) {
        if (progress <= 0.5D) {
            return curve(curveStart, inboundControl, curveBottom, progress * 2.0D);
        }

        return curve(curveBottom, outboundControl, curveEnd, (progress - 0.5D) * 2.0D);
    }

    private double approximateCurveLength() {
        Vec3 previous = curveStart;
        double length = 0.0D;
        for (int sample = 1; sample <= 24; sample++) {
            final Vec3 point = sampleCurve(sample / 24.0D);
            length += previous.distanceTo(point);
            previous = point;
        }
        return Math.max(1.0D, length);
    }

    private static Vec3 curve(Vec3 start, Vec3 control, Vec3 end, double progress) {
        final double inverseProgress = 1.0 - progress;
        return start.scale(inverseProgress * inverseProgress)
                .add(control.scale(2.0 * inverseProgress * progress))
                .add(end.scale(progress * progress));
    }

    private boolean damageTargetAcross(final Vec3 movement) {
        if (targetHit || target == null || !target.isAlive() || !(harpy.level() instanceof ServerLevel serverLevel)) {
            return false;
        }

        final AABB sweptArea = harpy.getBoundingBox().expandTowards(movement).inflate(0.35);
        if (sweptArea.intersects(target.getBoundingBox())) {
            targetHit = true;
            final DamageSource damageSource = harpy.damageSources().mobAttack(harpy);
            final boolean blockedByShield = isBlockedByPlayerShield(target, damageSource);
            final float damage = harpy.isElite() ? 9 : BASE_DASH_DAMAGE;
            if (target.hurtServer(serverLevel, damageSource, damage)) {
                harpy.setLastHurtMob(target);
            }
            if (blockedByShield) {
                spawnShieldBlockParticles(serverLevel, target);
            }

            return blockedByShield;
        }

        return false;
    }

    private void spawnShieldBlockParticles(final ServerLevel serverLevel, final LivingEntity currentTarget) {
        Vec3 direction = harpy.position().subtract(currentTarget.position());
        direction = new Vec3(direction.x, 0, direction.z);
        if (direction.lengthSqr() < 1.0E-6D) {
            direction = currentTarget.getLookAngle();
        }

        direction = direction.normalize();

        final Vec3 impactPosition = currentTarget.getEyePosition()
                .add(direction.scale(currentTarget.getBbWidth() * 0.6))
                .add(0, -0.25, 0);

        serverLevel.sendParticles(ParticleTypes.CRIT, impactPosition.x, impactPosition.y, impactPosition.z, 20, 0.25D, 0.3D, 0.25D, 0.2D);
    }

    private boolean isBlockedByPlayerShield(LivingEntity currentTarget, DamageSource damageSource) {
        if (!(currentTarget instanceof Player player)) {
            return false;
        }

        final ItemStack itemStack = player.getItemBlockingWith();
        if (itemStack == null) {
            return false;
        }

        final BlocksAttacks blocksAttacks = itemStack.get(DataComponents.BLOCKS_ATTACKS);
        final Vec3 sourcePosition = damageSource.getSourcePosition();
        if (blocksAttacks == null || sourcePosition == null) {
            return false;
        }

        final Vec3 direction = player.calculateViewVector(0.0F, player.getYHeadRot());
        final Vec3 attackDirection = new Vec3(sourcePosition.x - player.getX(), 0, sourcePosition.z - player.getZ()).normalize();
        if (attackDirection.lengthSqr() < 1.0E-6D) {
            return false;
        }

        final double attackAngle = Math.acos(Mth.clamp(attackDirection.dot(direction), -1, 1));
        return blocksAttacks.resolveBlockedDamage(damageSource, BASE_DASH_DAMAGE, attackAngle) > 0;
    }

    private void faceMovement(final Vec3 movement) {
        if (movement.horizontalDistanceSqr() < 1.0E-6D) {
            return;
        }

        harpy.setYRot((float) (Mth.atan2(-movement.x, movement.z) * Mth.RAD_TO_DEG));
        harpy.yBodyRot = harpy.getYRot();
        harpy.yHeadRot = harpy.getYRot();
    }

    private void beginEnding(final boolean stopImmediately) {
        if (stopImmediately) {
            finishMovement();
        }

        phase = Phase.ENDING;
        phaseTicks = 0;
        harpy.setAttackState(HarpyEntity.STATE_DASH_ENDING);
    }

    private void finishMovement() {
        movementFinished = true;
        harpy.getNavigation().stop();
        harpy.getMoveControl().setWait();
        harpy.setDeltaMovement(Vec3.ZERO);
        harpy.noPhysics = false;
        harpy.escapeFromBlocks();
    }

    private boolean isInFlightState() {
        final int state = harpy.getAttackState();
        return state == HarpyEntity.STATE_IDLE || state == HarpyEntity.STATE_FLY;
    }

    private boolean isDashState() {
        final int state = harpy.getAttackState();
        return state == HarpyEntity.STATE_DASH_PREPARING || state == HarpyEntity.STATE_DASHING || state == HarpyEntity.STATE_DASH_ENDING;
    }

    private static double horizontalDistance(final Vec3 first, final Vec3 second) {
        final double x = first.x - second.x;
        final double z = first.z - second.z;
        return Math.sqrt(x * x + z * z);
    }

    /// TODO: refactor with the client sided smoothstep method used in some classes
    private static double smoothstep(final double progress) {
        final double t = Mth.clamp(progress, 0.0D, 1.0D);
        return t * t * (3.0D - 2.0D * t);
    }

    private enum Phase {
        PREPARING,
        DASHING,
        ENDING
    }

}
