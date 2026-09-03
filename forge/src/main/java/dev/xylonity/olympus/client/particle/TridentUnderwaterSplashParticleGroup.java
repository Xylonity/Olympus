package dev.xylonity.olympus.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.olympus.registry.OlympusRenderTypes;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.phys.Vec3;

public final class TridentUnderwaterSplashParticleGroup {

    private static final RenderType RENDER_TYPE = OlympusRenderTypes.underwaterSplash();

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
            return "OLYMPUS_TRIDENT_UNDERWATER_SPLASH";
        }

    };

    private static final int LATITUDE_SEGMENTS = 12;
    private static final int LONGITUDE_SEGMENTS = 24;

    public static void render(final VertexConsumer buffer, final TridentUnderwaterSplashParticle.RenderSnapshot snapshot) {
        if (snapshot.alpha() <= 0.001F) {
            return;
        }

        // Sphere split into quads based on a segment amount (distributed along latitude and longitude)
        for (int latitude = 0; latitude < LATITUDE_SEGMENTS; latitude++) {
            final double latitudeFrom = -Math.PI * 0.5D + Math.PI * latitude / LATITUDE_SEGMENTS;
            final double latitudeTo = -Math.PI * 0.5D + Math.PI * (latitude + 1) / LATITUDE_SEGMENTS;
            for (int longitude = 0; longitude < LONGITUDE_SEGMENTS; longitude++) {
                final double longitudeFrom = Math.PI * 2.0D * longitude / LONGITUDE_SEGMENTS;
                final double longitudeTo = Math.PI * 2.0D * (longitude + 1) / LONGITUDE_SEGMENTS;

                addVertex(buffer, spherePoint(snapshot, latitudeFrom, longitudeFrom), snapshot);
                addVertex(buffer, spherePoint(snapshot, latitudeTo, longitudeFrom), snapshot);
                addVertex(buffer, spherePoint(snapshot, latitudeTo, longitudeTo), snapshot);
                addVertex(buffer, spherePoint(snapshot, latitudeFrom, longitudeTo), snapshot);
            }

        }

    }

    private static Vec3 spherePoint(final TridentUnderwaterSplashParticle.RenderSnapshot snapshot, final double latitude, final double longitude) {
        final double wave = 1.0D + Math.sin(longitude * 3.0D + latitude * 5.0D - snapshot.progress() * Math.PI * 4.0D) * 0.012D;
        final double radius = snapshot.radius() * wave;
        final double horizontalRadius = Math.cos(latitude) * radius;
        return snapshot.center().add(Math.cos(longitude) * horizontalRadius, Math.sin(latitude) * radius, Math.sin(longitude) * horizontalRadius);
    }

    private static void addVertex(final VertexConsumer buffer, final Vec3 position, final TridentUnderwaterSplashParticle.RenderSnapshot snapshot) {
        buffer.vertex(position.x, position.y, position.z)
                .color(snapshot.red(), snapshot.green(), snapshot.blue(), snapshot.alpha())
                .endVertex();
    }

}