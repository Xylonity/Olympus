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

public final class AresSpearHitParticle extends SingleQuadParticle {

    private static final int FRAME_COUNT = 7;

    private final SpriteSet sprites;

    private AresSpearHitParticle(final ClientLevel level, final double x, final double y, final double z, final double requestedScale, final SpriteSet sprites) {
        super(level, x, y, z, sprites.first());
        this.sprites = sprites;

        hasPhysics = false;
        lifetime = 12;
        quadSize = requestedScale > 0.0D ? (float) requestedScale : 3.5F;

        setParticleSpeed(0, 0, 0);
        updateSprite();
    }

    @Override
    public void tick() {
        super.tick();
        updateSprite();
    }

    @Override
    protected @NonNull Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    @Override
    protected int getLightCoords(final float partialTick) {
        return LightCoordsUtil.FULL_BRIGHT;
    }

    private void updateSprite() {
        final int frame = Math.min(age / 2, FRAME_COUNT - 1);
        setSprite(sprites.get(frame, FRAME_COUNT - 1));
    }

    public static final class Provider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet sprites;

        public Provider(final SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(final SimpleParticleType options, final ClientLevel level, final double x, final double y, final double z, final double xSpeed, final double ySpeed, final double zSpeed, final RandomSource random) {
            return new AresSpearHitParticle(level, x, y, z, xSpeed, sprites);
        }

    }

}
