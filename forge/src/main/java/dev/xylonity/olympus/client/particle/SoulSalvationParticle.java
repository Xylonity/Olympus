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

public final class SoulSalvationParticle extends SingleQuadParticle {

    private final SpriteSet sprites;

    private SoulSalvationParticle(final ClientLevel level, final double x, final double y, final double z, final double xSpeed, final double ySpeed, final double zSpeed, final SpriteSet sprites) {
        super(level, x, y, z, sprites.first());

        this.sprites = sprites;

        hasPhysics = false;
        friction = 0.95F;
        lifetime = 15;
        quadSize = 0.5f + level.getRandom().nextFloat() * 0.1f;

        setParticleSpeed(xSpeed, ySpeed, zSpeed);

        updateSprite();
    }

    @Override
    public void tick() {
        super.tick();
        updateSprite();
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
        final int frameCount = 6;
        final int frame = Math.min(age / 3, frameCount - 1);
        setSprite(sprites.get(frame, frameCount - 1));
    }

    public static final class Provider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet sprites;

        public Provider(final SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(final SimpleParticleType options, final ClientLevel level, final double x, final double y, final double z, final double xSpeed, final double ySpeed, final double zSpeed, final RandomSource random) {
            return new SoulSalvationParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        }

    }

}
