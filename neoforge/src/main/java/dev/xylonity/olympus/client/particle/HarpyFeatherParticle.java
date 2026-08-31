package dev.xylonity.olympus.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.NonNull;

public final class HarpyFeatherParticle extends SingleQuadParticle {

    private final float initialSize;
    private final float spinSpeed;
    private final double sway;

    private HarpyFeatherParticle(final ClientLevel level, final double x, final double y, final double z, final double xSpeed, final double ySpeed, final double zSpeed, final SpriteSet sprites, final RandomSource random) {
        super(level, x, y, z, sprites.get(random));

        hasPhysics = true;
        gravity = 0.035F;
        friction = 0.975F;
        lifetime = 65 + random.nextInt(36);
        initialSize = 0.08F + random.nextFloat() * 0.16F;
        quadSize = initialSize;
        spinSpeed = (random.nextBoolean() ? 1.0F : -1.0F) * (0.018F + random.nextFloat() * 0.025F);
        sway = random.nextDouble() * Mth.TWO_PI;
        roll = random.nextFloat() * Mth.TWO_PI;
        oRoll = roll;

        setParticleSpeed(xSpeed, ySpeed, zSpeed);
    }

    @Override
    public void tick() {
        xd += Math.cos(sway + age * 0.13D) * 0.00055D;
        zd += Math.sin(sway + age * 0.13D) * 0.00055D;
        oRoll = roll;
        roll += spinSpeed;

        super.tick();

        yd = Math.max(yd, -0.035D);
    }

    @Override
    public float getQuadSize(final float partialTick) {
        final float progress = Mth.clamp((age + partialTick) / lifetime, 0.0F, 1.0F);
        final float scale = 1 - progress * progress;
        return initialSize * scale;
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
            return new HarpyFeatherParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites, random);
        }

    }

}