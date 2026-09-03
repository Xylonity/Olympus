package dev.xylonity.olympus.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.olympus.registry.OlympusRenderTypes;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.phys.Vec3;

/// Based off my own implementation
/// https://github.com/Xylonity/Hostiles/blob/v1.20.1/common/src/main/java/dev/xylonity/hostiles/client/particle/GroundToTargetLightningParticle.java
public final class LightningParticleGroup {

    private static final RenderType RENDER_TYPE = OlympusRenderTypes.lightningBolt();

    public static final ParticleRenderType TYPE = new ParticleRenderType() {

        @Override
        public void begin(final BufferBuilder buffer, final TextureManager textureManager) {
            buffer.begin(RENDER_TYPE.mode(), RENDER_TYPE.format());
        }

        @Override
        public void end(final Tesselator tesselator) {
            RENDER_TYPE.end(tesselator.getBuilder(), RenderSystem.getVertexSorting());
        }

        @Override
        public String toString() {
            return "OLYMPUS_LIGHTNING_BOLT";
        }

    };

    public static void render(final VertexConsumer buffer, final LightningBoltParticle.RenderSnapshot snapshot) {
        final List<Vec3> points = snapshot.points();
        if (points.size() < 2) {
            return;
        }

        final List<CrossSection> sections = buildCrossSections(points, snapshot.width());

        // Artificial glow (the first gradient is less noticeable and larger)
        drawGradientGlow(buffer, sections, 6.6F, 0.28F * snapshot.alpha(), 1.0F, 1.0F, 0.02F);
        drawGradientGlow(buffer, sections, 3.2F, 0.56F * snapshot.alpha(), 1.0F, 1.0F, 0.08F);

        // Internal part
        drawCore(buffer, sections, snapshot.alpha(), 1.0F, 1.0F, 0.7F);
    }

    private static List<CrossSection> buildCrossSections(final List<Vec3> points, final float baseWidth) {
        final List<CrossSection> sections = new ArrayList<>(points.size());
        Vec3 previousRight = null;
        for (int index = 0; index < points.size(); index++) {
            final Vec3 point = points.get(index);
            final Vec3 tangent;
            if (index == 0) {
                tangent = points.get(1).subtract(point);
            }
            else if (index == points.size() - 1) {
                tangent = point.subtract(points.get(index - 1));
            }
            else {
                tangent = points.get(index + 1).subtract(points.get(index - 1));
            }

            final Vec3 direction = tangent.normalize();

            // Points are already relative to the camera so this should keep every vertex facing the camera
            Vec3 right = direction.cross(point.scale(-1));
            if (right.lengthSqr() < 1.0E-8D) {
                right = direction.cross(Math.abs(direction.y) > 0.9D ? new Vec3(1, 0, 0) : new Vec3(0, 1, 0));
            }

            right = right.normalize();
            if (previousRight != null && previousRight.dot(right) < 0) {
                right = right.scale(-1);
            }

            previousRight = right;

            final float progress = index / (float) (points.size() - 1);
            sections.add(new CrossSection(point, right, baseWidth * (1.0F - progress * 0.32F)));
        }

        return sections;
    }

    private static void drawGradientGlow(final VertexConsumer buffer, final List<CrossSection> sections, final float widthMultiplier, final float innerAlpha, final float red, final float green, final float blue) {
        if (innerAlpha <= 0.001F) {
            return;
        }

        for (int index = 0; index < sections.size() - 1; index++) {
            final CrossSection from = sections.get(index);
            final CrossSection to = sections.get(index + 1);
            final Vec3 fromLeft = from.offset(widthMultiplier);
            final Vec3 toLeft = to.offset(widthMultiplier);
            final Vec3 fromRight = from.offset(-widthMultiplier);
            final Vec3 toRight = to.offset(-widthMultiplier);

            // Each half fades from the center line towards a fully transparent outer edge
            doubleSidedQuad(buffer,
                    new ColoredVertex(from.center, red, green, blue, innerAlpha),
                    new ColoredVertex(fromLeft, red, green, blue, 0),
                    new ColoredVertex(toLeft, red, green, blue, 0),
                    new ColoredVertex(to.center, red, green, blue, innerAlpha)
            );
            doubleSidedQuad(buffer,
                    new ColoredVertex(from.center, red, green, blue, innerAlpha),
                    new ColoredVertex(to.center, red, green, blue, innerAlpha),
                    new ColoredVertex(toRight, red, green, blue, 0),
                    new ColoredVertex(fromRight, red, green, blue, 0)
            );

        }

    }

    private static void drawCore(final VertexConsumer buffer, final List<CrossSection> sections, final float alpha, final float red, final float green, final float blue) {
        for (int index = 0; index < sections.size() - 1; index++) {
            final CrossSection from = sections.get(index);
            final CrossSection to = sections.get(index + 1);
            doubleSidedQuad(buffer,
                    new ColoredVertex(from.offset(1.0F), red, green, blue, alpha),
                    new ColoredVertex(from.offset(-1.0F), red, green, blue, alpha),
                    new ColoredVertex(to.offset(-1.0F), red, green, blue, alpha),
                    new ColoredVertex(to.offset(1.0F), red, green, blue, alpha)
            );

        }

    }

    private static void doubleSidedQuad(final VertexConsumer buffer, final ColoredVertex first, final ColoredVertex second, final ColoredVertex third, final ColoredVertex fourth) {
        addTriangle(buffer, first, second, third);
        addTriangle(buffer, first, third, fourth);
        addTriangle(buffer, third, second, first);
        addTriangle(buffer, fourth, third, first);
    }

    private static void addTriangle(final VertexConsumer buffer, final ColoredVertex first, final ColoredVertex second, final ColoredVertex third) {
        addVertex(buffer, first);
        addVertex(buffer, second);
        addVertex(buffer, third);
    }

    private static void addVertex(final VertexConsumer buffer, final ColoredVertex vertex) {
        final Vec3 position = vertex.position;
        buffer.vertex(position.x, position.y, position.z)
                .color(vertex.red, vertex.green, vertex.blue, vertex.alpha)
                .endVertex();
    }

    private record CrossSection(
            Vec3 center,
            Vec3 right,
            float halfWidth
    ) {

        private Vec3 offset(final float multiplier) {
            return center.add(right.scale(halfWidth * multiplier));
        }

    }

    private record ColoredVertex(
            Vec3 position,
            float red,
            float green,
            float blue,
            float alpha
    ) {
        ;;
    }

}
