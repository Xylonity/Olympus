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

/// Mostly the same logic as {@link LightningSparksParticle}
public final class HarpyMagicParticle extends SingleQuadParticle {

    private final SpriteSet sprites;
    private final float initialSize;

    private HarpyMagicParticle(final ClientLevel level, final double x, final double y, final double z, final double xSpeed, final double ySpeed, final double zSpeed, final SpriteSet sprites, final RandomSource random) {
        super(level, x, y, z, sprites.first());
        this.sprites = sprites;

        hasPhysics = false;
        friction = 0.84F;
        lifetime = 9 + random.nextInt(3);
        initialSize = 0.2F + random.nextFloat() * 0.15F;
        quadSize = initialSize;
        setParticleSpeed(xSpeed, ySpeed, zSpeed);
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
        return initialSize * Mth.lerp(progress, 1.0F, 0.1F);
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
        final int frame = Math.min(age * 4 / lifetime, 4 - 1);
        setSprite(sprites.get(frame, 4 - 1));
    }

    public static final class Provider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet sprites;

        public Provider(final SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(final SimpleParticleType options, final ClientLevel level, final double x, final double y, final double z, final double xSpeed, final double ySpeed, final double zSpeed, final RandomSource random) {
            return new HarpyMagicParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites, random);
        }

    }

}
