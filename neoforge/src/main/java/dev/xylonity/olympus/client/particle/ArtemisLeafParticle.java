package dev.xylonity.olympus.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.FallingLeavesParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public final class ArtemisLeafParticle extends FallingLeavesParticle {

    private final float initialSize;
    private final int initialLifetime;

    private ArtemisLeafParticle(final ClientLevel level, final double x, final double y, final double z, final double xSpeed, final double ySpeed, final double zSpeed, final SpriteSet sprites, final RandomSource random, final boolean small) {
        super(level, x, y, z, sprites.get(random), 0.06F, 2.0F, true, true, small ? 1.6F : 2.4F, 0.004F);

        initialSize = quadSize;
        initialLifetime = 28 + random.nextInt(30);
        lifetime = initialLifetime;
        setParticleSpeed(xSpeed, ySpeed, zSpeed);
    }

    @Override
    public float getQuadSize(final float partialTick) {
        final float progress = Mth.clamp(1 - (lifetime - partialTick) / initialLifetime, 0, 1);
        return initialSize * Mth.lerp(progress, 1, 0.12f);
    }

    public static final class MainProvider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet sprites;

        public MainProvider(final SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(final SimpleParticleType options, final ClientLevel level, final double x, final double y, final double z, final double xSpeed, final double ySpeed, final double zSpeed, final RandomSource random) {
            return new ArtemisLeafParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites, random, false);
        }

    }

    public static final class SmallProvider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet sprites;

        public SmallProvider(final SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(final SimpleParticleType options, final ClientLevel level, final double x, final double y, final double z, final double xSpeed, final double ySpeed, final double zSpeed, final RandomSource random) {
            return new ArtemisLeafParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites, random, true);
        }

    }

}
