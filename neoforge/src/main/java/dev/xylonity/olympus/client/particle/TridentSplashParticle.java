package dev.xylonity.olympus.client.particle;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;
import org.joml.Quaternionf;
import org.jspecify.annotations.NonNull;

public final class TridentSplashParticle extends SingleQuadParticle {

    private static final Quaternionf AZIMUTH = new Quaternionf().rotationX((float) (-Math.PI * 0.5D));

    private final SpriteSet sprites;
    private final int frameCount;

    private TridentSplashParticle(final ClientLevel level, final double x, final double y, final double z, final double requestedScale, final SpriteSet sprites, final int frameCount, final int lifetime, final float defaultScale) {
        super(level, x, y, z, sprites.first());

        this.sprites = sprites;
        this.frameCount = frameCount;

        hasPhysics = false;
        this.lifetime = lifetime;
        quadSize = requestedScale > 0.0D ? (float) requestedScale : defaultScale;

        setParticleSpeed(0, 0, 0);

        updateSprite();
    }

    @Override
    public void tick() {
        super.tick();
        updateSprite();
    }

    @Override
    public void extract(final QuadParticleRenderState renderState, final Camera camera, final float partialTick) {
        extractRotatedQuad(renderState, camera, AZIMUTH, partialTick);
    }

    @Override
    protected @NonNull Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    @Override
    protected int getLightCoords(float a) {
        // Prevents the black overlay if inside a block
        return level.getBlockState(new BlockPos((int) getPos().x, (int) getPos().y, (int) getPos().z)).isAir() ? super.getLightCoords(a) : LightCoordsUtil.FULL_SKY;
    }

    private void updateSprite() {
        final int frame = Math.min(age / 2, frameCount - 1);
        setSprite(sprites.get(frame, frameCount - 1));
    }

    public static final class MainProvider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet sprites;

        public MainProvider(final SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(final SimpleParticleType options, final ClientLevel level, final double x, final double y, final double z, final double xSpeed, final double ySpeed, final double zSpeed, final RandomSource random) {
            return new TridentSplashParticle(level, x, y, z, xSpeed, sprites, 7, 12, 3.5F);
        }

    }

    public static final class SmallProvider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet sprites;

        public SmallProvider(final SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(final SimpleParticleType options, final ClientLevel level, final double x, final double y, final double z, final double xSpeed, final double ySpeed, final double zSpeed, final RandomSource random) {
            final double scale = xSpeed > 0.0D ? xSpeed : 0.20F + random.nextFloat() * 0.3F;
            return new TridentSplashParticle(level, x, y, z, scale, sprites, 5, 8, 0.20F);
        }

    }

}
