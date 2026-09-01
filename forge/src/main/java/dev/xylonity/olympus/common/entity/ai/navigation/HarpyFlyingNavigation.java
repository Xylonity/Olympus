package dev.xylonity.olympus.common.entity.ai.navigation;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.FlyNodeEvaluator;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/// Simplified Companions! flying navigator
/// https://github.com/Xylonity/Companions/blob/v1.20.1/common/src/main/java/dev/xylonity/companions/common/ai/navigator/FlyingNavigator.java
public final class HarpyFlyingNavigation extends FlyingPathNavigation {

    private @Nullable Vec3 target;
    private double speedMod;

    public HarpyFlyingNavigation(final Mob mob, final Level level) {
        super(mob, level);
    }

    @Override
    protected @NonNull PathFinder createPathFinder(final int maxVisitedNodes) {
        final FlyNodeEvaluator evaluator = new FlyNodeEvaluator();
        evaluator.setCanPassDoors(false);
        evaluator.setCanOpenDoors(false);
        evaluator.setCanFloat(true);
        nodeEvaluator = evaluator;
        return new PathFinder(evaluator, maxVisitedNodes);
    }

    @Override
    public boolean moveTo(final double x, final double y, final double z, final double speedModifier) {
        final Vec3 target = new Vec3(x, y, z);
        if (canMoveDirectly(mob.position(), target)) {
            super.stop();
            this.target = target;
            speedMod = speedModifier;
            return true;
        }

        this.target = null;

        return super.moveTo(x, y, z, speedModifier);
    }

    @Override
    public void tick() {
        if (target == null) {
            super.tick();
            return;
        }

        tick++;

        if (mob.distanceToSqr(target) <= 0.36) {
            target = null;
            mob.getMoveControl().setWait();
            return;
        }

        if (!canMoveDirectly(mob.position(), target)) {
            final Vec3 blockedTarget = target;
            final double speedModifier = speedMod;
            target = null;

            if (super.moveTo(blockedTarget.x, blockedTarget.y, blockedTarget.z, speedModifier)) {
                super.tick();
            }

            return;
        }

        mob.getMoveControl().setWantedPosition(target.x, target.y, target.z, speedMod);
    }

    @Override
    public boolean isDone() {
        return target == null && super.isDone();
    }

    @Override
    public void stop() {
        target = null;
        super.stop();
    }

    @Override
    protected void followThePath() {
        if (path != null && !path.isDone()) {
            tryShortcut(getTempMobPos());
        }

        super.followThePath();
    }

    private void tryShortcut(final Vec3 currentPosition) {
        if (path != null) {
            final int nextNode = path.getNextNodeIndex();
            final int furthestNode = Math.min(path.getNodeCount() - 1, nextNode + 6);
            for (int idx = furthestNode; idx > nextNode; idx--) {
                final Vec3 nodePosition = path.getEntityPosAtNode(mob, idx);
                if (currentPosition.distanceToSqr(nodePosition) <= 144 && hasSafeNodesUntil(idx) && canMoveDirectly(currentPosition, nodePosition)) {
                    path.setNextNodeIndex(idx);
                    return;
                }

            }

        }

    }

    private boolean hasSafeNodesUntil(final int lastNodeIdx) {
        if (path != null) {
            for (int idx = path.getNextNodeIndex(); idx <= lastNodeIdx; idx++) {
                final Node node = path.getNode(idx);
                final float malus = mob.getPathfindingMalus(node.type);
                if (malus < 0 || malus >= 8) {
                    return false;
                }

            }

            return true;
        }

        return false;
    }

}