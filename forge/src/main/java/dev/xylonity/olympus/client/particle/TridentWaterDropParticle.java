package dev.xylonity.olympus.client.particle;

import dev.xylonity.olympus.registry.OlympusParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.material.FluidState;
import org.jspecify.annotations.NonNull;

public final class TridentWaterDropParticle extends SingleQuadParticle {

    private TridentWaterDropParticle(final ClientLevel level, final double x, final double y, final double z, final double xSpeed, final double ySpeed, final double zSpeed, final SpriteSet sprites) {
        super(level, x, y, z, sprites.first());

        friction = 0.98F;
        gravity = 0.7F;
        lifetime = 40 + random.nextInt(41);
        quadSize = 0.08F + random.nextFloat() * 0.3F;
        setSize(0.08F, 0.08F);

        setParticleSpeed(xSpeed, ySpeed, zSpeed);
    }

    @Override
    public void tick() {
        super.tick();
        if (removed) {
            return;
        }

        // If it didn't touch ground yet
        final BlockPos fluidPos = BlockPos.containing(x, y, z);
        final FluidState fluidState = level.getFluidState(fluidPos);
        final boolean landedInWater = fluidState.is(FluidTags.WATER);
        if (!onGround && !landedInWater) {
            return;
        }

        final double splashY = landedInWater ? fluidPos.getY() + fluidState.getHeight(level, fluidPos) + 0.1D : y + 0.1D;
        level.addParticle(OlympusParticles.TRIDENT_SMALL_SPLASH_OF_WATER.get(), x, splashY, z, 0.16 + random.nextDouble() * 0.08, 0, 0);

        remove();
    }

    @Override
    protected @NonNull Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    public static final class Provider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet sprites;

        public Provider(final SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(final SimpleParticleType options, final ClientLevel level, final double x, final double y, final double z, final double xSpeed, final double ySpeed, final double zSpeed, final RandomSource random) {
            return new TridentWaterDropParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        }

    }

}