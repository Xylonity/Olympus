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

public final class LightningSparksParticle extends SingleQuadParticle {

    private final SpriteSet sprites;
    private final float initialSize;

    private LightningSparksParticle(final ClientLevel level, final double x, final double y, final double z, final SpriteSet sprites, final RandomSource random) {
        super(level, x, y, z, sprites.first());
        this.sprites = sprites;

        hasPhysics = false;
        friction = 0.84F;
        lifetime = 10 + random.nextInt(8);
        initialSize = 0.2F + random.nextFloat() * 0.15F;
        quadSize = initialSize;

        final double verticalDirection = Math.cos(Math.toRadians(50.0D)) + random.nextDouble() * (1.0D - Math.cos(Math.toRadians(50.0D)));
        final double horizontalDirection = Math.sqrt(1.0D - verticalDirection * verticalDirection);
        final double angle = random.nextDouble() * Math.PI * 2.0D;
        final double speed = 0.075D + random.nextDouble() * 0.2D;
        setParticleSpeed(Math.cos(angle) * horizontalDirection * speed, verticalDirection * speed, Math.sin(angle) * horizontalDirection * speed);

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
        final int frame = Math.min(age * 6 / lifetime, 6 - 1);
        setSprite(sprites.get(frame, 6 - 1));
    }

    public static final class Provider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet sprites;

        public Provider(final SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(final SimpleParticleType options, final ClientLevel level, final double x, final double y, final double z, final double xSpeed, final double ySpeed, final double zSpeed, final RandomSource random) {
            return new LightningSparksParticle(level, x, y, z, sprites, random);
        }

    }

}
