package dev.xylonity.olympus.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.olympus.registry.OlympusRenderTypes;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

/// Based off my own ribbon trail particle implementation, but adapted for camera facing
/// https://github.com/Xylonity/Knight-Lib/blob/1.20.1/common/src/main/java/dev/xylonity/knightlib/client/particle/AbstractRibbonTrailParticle.java
public abstract class BaseRibbonTrailParticle extends Particle {

    private static final Function<ResourceLocation, RibbonParticleRenderType> RENDER_TYPES = Util.memoize(RibbonParticleRenderType::new);

    private final ArrayDeque<Vec3> trailPositions = new ArrayDeque<>();
    private final RibbonParticleRenderType renderType;

    private final float halfWidth;
    private final float red;
    private final float green;
    private final float blue;

    protected BaseRibbonTrailParticle(final ClientLevel level, final double x, final double y, final double z, final ResourceLocation texture, final float halfWidth, final float red, final float green, final float blue) {
        super(level, x, y, z);
        this.renderType = RENDER_TYPES.apply(texture);
        this.halfWidth = halfWidth;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.hasPhysics = false;

        trailPositions.addFirst(new Vec3(x, y, z));

        updateBounds();
    }

    protected final void updateTrackedPosition(final double x, final double y, final double z) {
        xo = this.x;
        yo = this.y;
        zo = this.z;

        // Saving the old head position or the first segment would overlap
        rememberPosition(new Vec3(xo, yo, zo));
        setPos(x, y, z);
        updateBounds();
    }

    @Override
    public final @NonNull ParticleRenderType getRenderType() {
        return renderType;
    }

    @Override
    public final void render(final VertexConsumer buffer, final Camera camera, final float partialTick) {
        final List<Vec3> points = extractCameraRelativePoints(camera, partialTick);
        if (points.size() < 2) {
            return;
        }

        final List<CrossSection> sections = buildSections(points);
        final int light = getLightColor(partialTick);
        for (int index = 0; index < sections.size() - 1; index++) {
            final CrossSection from = sections.get(index);
            final CrossSection to = sections.get(index + 1);
            addSegment(buffer, from, to, light, false);
            addSegment(buffer, from, to, light, true);
        }

    }

    private List<Vec3> extractCameraRelativePoints(final Camera camera, final float partialTick) {
        final Vec3 cameraPosition = camera.getPosition();
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

        return points;
    }

    private List<CrossSection> buildSections(final List<Vec3> points) {
        final double length = pathLength(points);
        final List<CrossSection> sections = new ArrayList<>(points.size());
        Vec3 previousRight = null;
        double distance = 0.0D;

        for (int index = 0; index < points.size(); index++) {
            final Vec3 point = points.get(index);
            if (index > 0) {
                distance += points.get(index - 1).distanceTo(point);
            }

            // Using both neighbours on inner points keeps turns smoother than following a single segment
            final Vec3 line;
            if (index == 0) {
                line = points.get(1).subtract(point);
            }
            else if (index == points.size() - 1) {
                line = point.subtract(points.get(index - 1));
            }
            else {
                line = points.get(index + 1).subtract(points.get(index - 1));
            }

            final Vec3 direction = line.normalize();
            Vec3 right = direction.cross(point.scale(-1.0D));
            if (right.lengthSqr() < 1.0E-8D) {
                right = direction.cross(Math.abs(direction.y) > 0.9D ? new Vec3(1, 0, 0) : new Vec3(0, 1, 0));
            }

            right = right.normalize();
            // Keeping the same side along the path so the ribbon doesn't randomly twist
            if (previousRight != null && previousRight.dot(right) < 0.0D) {
                right = right.scale(-1.0D);
            }

            previousRight = right;

            final float progress = length < 1.0E-5D ? 0.0F : (float) (distance / length);
            final float width = halfWidth * (1.0F - progress * 0.55F);
            sections.add(new CrossSection(point, right, width, progress));
        }

        return sections;
    }

    private void rememberPosition(final Vec3 position) {
        final Vec3 newestPosition = trailPositions.peekFirst();
        // Basically quite identical samples are ignored
        if (newestPosition != null && newestPosition.distanceToSqr(position) < 0.0025D) {
            return;
        }

        trailPositions.addFirst(position);
        while (trailPositions.size() > 16 || trailLength() > 5) {
            trailPositions.removeLast();
        }

    }

    private double trailLength() {
        double length = 0.0D;
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

        setBoundingBox(new AABB(minX, minY, minZ, maxX, maxY, maxZ).inflate(halfWidth));
    }

    private static double pathLength(final List<Vec3> points) {
        double length = 0;
        for (int index = 1; index < points.size(); index++) {
            length += points.get(index - 1).distanceTo(points.get(index));
        }

        return length;
    }

    private void addSegment(final VertexConsumer buffer, final CrossSection from, final CrossSection to, final int light, final boolean reverse) {
        final Vec3 fromLeft = from.offset(1.0F);
        final Vec3 fromRight = from.offset(-1.0F);
        final Vec3 toLeft = to.offset(1.0F);
        final Vec3 toRight = to.offset(-1.0F);

        if (reverse) {
            addVertex(buffer, fromLeft, from.progress, 0.0F, light);
            addVertex(buffer, toLeft, to.progress, 0.0F, light);
            addVertex(buffer, toRight, to.progress, 1.0F, light);
            addVertex(buffer, fromRight, from.progress, 1.0F, light);
        }
        else {
            addVertex(buffer, fromLeft, from.progress, 0.0F, light);
            addVertex(buffer, fromRight, from.progress, 1.0F, light);
            addVertex(buffer, toRight, to.progress, 1.0F, light);
            addVertex(buffer, toLeft, to.progress, 0.0F, light);
        }

    }

    private void addVertex(final VertexConsumer buffer, final Vec3 position, final float u, final float v, final int light) {
        buffer.vertex(position.x, position.y, position.z)
                .color(red, green, blue, 1.0F)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    private record CrossSection(
            Vec3 center,
            Vec3 right,
            float halfWidth,
            float progress
    ) {

        private Vec3 offset(final float multiplier) {
            return center.add(right.scale(halfWidth * multiplier));
        }

    }

    private static final class RibbonParticleRenderType implements ParticleRenderType {

        private final ResourceLocation texture;
        private final RenderType renderType;

        private RibbonParticleRenderType(final ResourceLocation texture) {
            this.texture = texture;
            this.renderType = OlympusRenderTypes.translucentEntityComposite(texture);
        }

        @Override
        public void begin(final BufferBuilder buffer, final TextureManager textureManager) {
            buffer.begin(renderType.mode(), renderType.format());
        }

        @Override
        public void end(final Tesselator tesselator) {
            renderType.end(tesselator.getBuilder(), RenderSystem.getVertexSorting());
        }

        @Override
        public String toString() {
            return "OLYMPUS_CAMERA_FACING_RIBBON[" + texture + "]";
        }

    }

}
