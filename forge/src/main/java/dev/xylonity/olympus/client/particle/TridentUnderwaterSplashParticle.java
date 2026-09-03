package dev.xylonity.olympus.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jspecify.annotations.NonNull;

/// Literal structure port from 26.1 i'm done with rewriting things
public final class TridentUnderwaterSplashParticle extends Particle {

    private static final int FALLBACK_COLOR = 0x48DDE7;

    private final float maxRadius;
    private final float red;
    private final float green;
    private final float blue;

    private TridentUnderwaterSplashParticle(ClientLevel level, double x, double y, double z, double requestedRadius) {
        super(level, x, y, z);

        hasPhysics = false;
        lifetime = 12;
        maxRadius = requestedRadius > 0 ? (float) requestedRadius : 3.5F;

        // Uses the surrounding fluid color so (some) modded fluids can tint the sphere too
        final int color = lighten(resolveFluidColor(level, BlockPos.containing(x, y, z)));
        red = (color >> 16 & 0xFF) / 255.0F;
        green = (color >> 8 & 0xFF) / 255.0F;
        blue = (color & 0xFF) / 255.0F;

        setBoundingBox(new AABB(x, y, z, x, y, z).inflate(maxRadius));
    }

    @Override
    public void tick() {
        if (age++ >= lifetime) {
            remove();
        }

    }

    @Override
    public @NonNull ParticleRenderType getRenderType() {
        return TridentUnderwaterSplashParticleGroup.TYPE;
    }

    @Override
    public void render(final @NonNull VertexConsumer buffer, final @NonNull Camera camera, final float partialTick) {
        TridentUnderwaterSplashParticleGroup.render(buffer, extractSnapshot(camera, partialTick));
    }

    public RenderSnapshot extractSnapshot(final Camera camera, final float partialTick) {
        final float progress = Mth.clamp((age + partialTick) / lifetime, 0.0F, 1.0F);
        final float inverseProgress = 1 - progress;

        // Expands quickly at the beginning and slows down near the maximum radius
        final float easedProgress = 1 - inverseProgress * inverseProgress * inverseProgress;
        final float radius = Mth.lerp(easedProgress, 0.08F, maxRadius);

        // Short fade in prevents the sphere from suddenly appearing at full opacity
        final float appear = Mth.clamp(progress * 6f, 0, 1);
        final float alpha = 0.3F * appear * inverseProgress;
        return new RenderSnapshot(getPos().subtract(camera.getPosition()), radius, red, green, blue, alpha, progress);
    }

    private static int resolveFluidColor(final ClientLevel level, final BlockPos origin) {
        final BlockPos fluidPos = findFluidPosition(level, origin);
        if (fluidPos == null) {
            return FALLBACK_COLOR;
        }

        final FluidState fluidState = level.getFluidState(fluidPos);
        final int color = IClientFluidTypeExtensions.of(fluidState).getTintColor(fluidState, level, fluidPos);
        return color == -1 ? FALLBACK_COLOR : color & 0xFFFFFF;
    }

    private static BlockPos findFluidPosition(final ClientLevel level, final BlockPos origin) {
        if (level.getFluidState(origin).is(FluidTags.WATER)) {
            return origin;
        }

        for (int offset = 1; offset <= 3; offset++) {
            final BlockPos below = origin.below(offset);
            if (level.getFluidState(below).is(FluidTags.WATER)) {
                return below;
            }

            final BlockPos above = origin.above(offset);
            if (level.getFluidState(above).is(FluidTags.WATER)) {
                return above;
            }

        }

        return null;
    }

    private static int lighten(final int color) {
        // Makes darker fluid colors visible through the translucent render type
        final int red = lightenChannel(color >> 16 & 0xFF);
        final int green = lightenChannel(color >> 8 & 0xFF);
        final int blue = lightenChannel(color & 0xFF);
        return red << 16 | green << 8 | blue;
    }

    private static int lightenChannel(final int channel) {
        return Mth.clamp(Math.round(channel + (255 - channel) * 0.32f), 0, 255);
    }

    public record RenderSnapshot(
            Vec3 center,
            float radius,
            float red,
            float green,
            float blue,
            float alpha,
            float progress
    ) {
        ;;
    }

    public static final class Provider implements ParticleProvider<SimpleParticleType> {

        @Override
        public Particle createParticle(final SimpleParticleType options, final ClientLevel level, final double x, final double y, final double z, final double xSpeed, final double ySpeed, final double zSpeed) {
            return new TridentUnderwaterSplashParticle(level, x, y, z, xSpeed);
        }

    }

}