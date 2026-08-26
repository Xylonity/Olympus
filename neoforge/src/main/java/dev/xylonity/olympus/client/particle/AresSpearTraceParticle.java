package dev.xylonity.olympus.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.NonNull;

public final class AresSpearTraceParticle extends SingleQuadParticle {

    private final SpriteSet sprites;

    private AresSpearTraceParticle(final ClientLevel level, final double x, final double y, final double z, final double xSpeed, final double ySpeed, final double zSpeed, final SpriteSet sprites, final RandomSource random) {
        super(level, x, y, z, sprites.first());
        this.sprites = sprites;

        hasPhysics = false;
        friction = 0.9F;
        lifetime = 12;
        quadSize = 0.1F * (random.nextFloat() * random.nextFloat() * 6.0F + 1.0F);

        setParticleSpeed(xSpeed, ySpeed, zSpeed);
        setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        setSpriteFromAge(sprites);
    }

    @Override
    protected @NonNull Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    @Override
    protected int getLightCoords(final float partialTick) {
        return LightCoordsUtil.FULL_BRIGHT;
    }

    public static final class Provider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet sprites;

        public Provider(final SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(final SimpleParticleType options, final ClientLevel level, final double x, final double y, final double z, final double xSpeed, final double ySpeed, final double zSpeed, final RandomSource random) {
            return new AresSpearTraceParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites, random);
        }

    }

}
