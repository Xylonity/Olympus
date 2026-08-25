package dev.xylonity.olympus.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.NonNull;

public final class ForgingSparkParticle extends SingleQuadParticle {

    private ForgingSparkParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites, RandomSource random) {
        super(level, x, y, z, sprites.first());

        friction = 0.96F;
        gravity = 0.75F;
        lifetime = 40 + random.nextInt(21);
        quadSize = 0.08F + random.nextFloat() * 0.06F;
        setSize(0.05F, 0.05F);
        setParticleSpeed(xSpeed, ySpeed, zSpeed);
    }

    @Override
    public void tick() {
        super.tick();
        if (removed || !onGround) {
            return;
        }

        level.addParticle(ParticleTypes.SMOKE, x, y + 0.02D, z, 0, 0, 0);

        remove();
    }

    @Override
    protected int getLightCoords(final float partialTick) {
        return LightCoordsUtil.FULL_BRIGHT;
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
        public Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
            return new ForgingSparkParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites, random);
        }

    }

}
