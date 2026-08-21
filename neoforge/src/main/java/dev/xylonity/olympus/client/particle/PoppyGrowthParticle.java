package dev.xylonity.olympus.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.NonNull;

public final class PoppyGrowthParticle extends SingleQuadParticle {

    private final SpriteSet sprites;

    private final float initialSize;

    private PoppyGrowthParticle(final ClientLevel level, final double x, final double y, final double z, final SpriteSet sprites, final RandomSource random) {
        super(level, x, y, z, sprites.first());
        this.sprites = sprites;

        hasPhysics = false;
        friction = 0.96F;
        lifetime = 12 + random.nextInt(3);
        initialSize = 0.14F + random.nextFloat() * 0.04F;
        quadSize = initialSize;

        setParticleSpeed((random.nextDouble() - 0.5D) * 0.006D, 0.028D + random.nextDouble() * 0.008D, (random.nextDouble() - 0.5D) * 0.006D);

        updateSprite();
    }

    @Override
    public void tick() {
        super.tick();
        updateSprite();
    }

    @Override
    public float getQuadSize(final float partialTick) {
        final float progress = Mth.clamp((age + partialTick) / lifetime, 0.0F, 1.0F);
        return initialSize * (1.0F - progress);
    }

    @Override
    protected int getLightCoords(final float partialTick) {
        return LightCoordsUtil.FULL_BRIGHT;
    }

    @Override
    protected @NonNull Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    private void updateSprite() {
        final int frame = Math.min(age / 3, 5 - 1);
        setSprite(sprites.get(frame, 5 - 1));
    }

    public static final class Provider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet sprites;

        public Provider(final SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(final SimpleParticleType options, final ClientLevel level, final double x, final double y, final double z, final double xSpeed, final double ySpeed, final double zSpeed, final RandomSource random) {
            return new PoppyGrowthParticle(level, x, y, z, sprites, random);
        }

    }

}