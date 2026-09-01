package dev.xylonity.olympus.client.particle;

import dev.xylonity.olympus.common.entity.projectile.HarpyProjectileEntity;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/// Same logic as {@link SoulTrailParticle}
public final class HarpyProjectileTrailParticle extends Particle {

    private static final float WIDTH = 0.09F;

    private final ArrayDeque<Vec3> trailPositions = new ArrayDeque<>();

    private final int projectileEntityId;

    public HarpyProjectileTrailParticle(final ClientLevel level, final HarpyProjectileEntity projectile) {
        super(level, projectile.getX(), projectile.getY() + projectile.getBbHeight() * 0.5D, projectile.getZ());
        projectileEntityId = projectile.getId();
        hasPhysics = false;
        trailPositions.addFirst(new Vec3(x, y, z));
        updateBounds();
    }

    @Override
    public void tick() {
        final Entity entity = level.getEntity(projectileEntityId);
        if (!(entity instanceof HarpyProjectileEntity projectile) || !projectile.isAlive()) {
            remove();
            return;
        }

        xo = x;
        yo = y;
        zo = z;

        // Saving the old head position or the first segment would overlap
        rememberPosition(new Vec3(xo, yo, zo));
        setPos(projectile.getX(), projectile.getY() + projectile.getBbHeight() * 0.5D, projectile.getZ());
        updateBounds();
    }

    @Override
    public ParticleRenderType getGroup() {
        return HarpyProjectileTrailParticleGroup.TYPE;
    }

    public @Nullable RenderSnapshot extractSnapshot(final Camera camera, final float partialTick) {
        final Vec3 cameraPosition = camera.position();
        final List<Vec3> points = new ArrayList<>(trailPositions.size() + 1);
        Vec3 previous = new Vec3(
                Mth.lerp(partialTick, xo, x),
                Mth.lerp(partialTick, yo, y),
                Mth.lerp(partialTick, zo, z)
        );

        points.add(previous.subtract(cameraPosition));

        for (final Vec3 position : trailPositions) {
            if (position.distanceToSqr(previous) < 1.0E-6D) {
                continue;
            }

            points.add(position.subtract(cameraPosition));
            previous = position;
        }

        return points.size() < 2 ? null : new RenderSnapshot(List.copyOf(points), WIDTH);
    }

    private void rememberPosition(final Vec3 position) {
        final Vec3 newestPosition = trailPositions.peekFirst();
        // Basically quite identical samples are ignored
        if (newestPosition != null && newestPosition.distanceToSqr(position) < 0.0025D) {
            return;
        }

        trailPositions.addFirst(position);
        while (trailPositions.size() > 16 || trailLength() > 5.0D) {
            trailPositions.removeLast();
        }

    }

    private double trailLength() {
        double length = 0;
        Vec3 previous = null;
        for (final Vec3 position : trailPositions) {
            if (previous != null) {
                length += previous.distanceTo(position);
            }

            previous = position;
        }

        return length;
    }

    private void updateBounds() {
        // The bbox contains the whole ribbon rather than just the head
        double minX = x;
        double minY = y;
        double minZ = z;
        double maxX = x;
        double maxY = y;
        double maxZ = z;

        for (final Vec3 position : trailPositions) {
            minX = Math.min(minX, position.x);
            minY = Math.min(minY, position.y);
            minZ = Math.min(minZ, position.z);
            maxX = Math.max(maxX, position.x);
            maxY = Math.max(maxY, position.y);
            maxZ = Math.max(maxZ, position.z);
        }

        setBoundingBox(new AABB(minX, minY, minZ, maxX, maxY, maxZ).inflate(WIDTH));
    }

    public record RenderSnapshot(
            List<Vec3> points,
            float width
    ) {
        ;;
    }

}
