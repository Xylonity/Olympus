package dev.xylonity.olympus.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.NonNull;

public final class LyreNoteParticle extends TextureSheetParticle {

    private final double baseXSpeed;
    private final double baseZSpeed;
    private final float wavePhase;
    private final float waveSpeed;
    private final float waveStrength;
    private final float initialSize;

    private LyreNoteParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites, RandomSource random) {
        super(level, x, y, z);

        hasPhysics = false;
        friction = 0.96F;
        lifetime = 32 + random.nextInt(16);
        initialSize = 0.2F + random.nextFloat() * 0.07F;
        quadSize = initialSize;
        baseXSpeed = xSpeed;
        baseZSpeed = zSpeed;
        wavePhase = random.nextFloat() * Mth.TWO_PI;
        waveSpeed = 0.25F + random.nextFloat() * 0.3F;
        waveStrength = 0.008F + random.nextFloat() * 0.006F;
        alpha = 0;

        setParticleSpeed(xSpeed, ySpeed, zSpeed);
        setSprite(sprites.get(0, 1));
        setRandomColor(random);
    }

    @Override
    public void tick() {
        xd = baseXSpeed + Math.cos(age * waveSpeed + wavePhase) * waveStrength;
        zd = baseZSpeed + Math.sin(age * waveSpeed + wavePhase) * waveStrength;

        super.tick();

        final float fadeIn = Mth.clamp(age / (float) 6, 0, 1);
        final float fadeOut = Mth.clamp((lifetime - age) / (float) 12, 0, 1);
        alpha = Math.min(fadeIn, fadeOut);
    }

    @Override
    public float getQuadSize(final float partialTick) {
        final float progress = Mth.clamp((age + partialTick) / lifetime, 0.0F, 1.0F);
        return initialSize * (0.9F + progress * 0.2F);
    }

    @Override
    protected int getLightColor(final float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    public @NonNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    private void setRandomColor(final RandomSource random) {
        switch (random.nextInt(4)) {
            case 0 -> setColor(1, 1, 1);
            case 1 -> setColor(1, 0.82F, 0.9F);
            case 2 -> setColor(0.9F, 0.82f, 1);
            default -> setColor(1, 0.93f, 0.72f);
        }

    }

    public static final class Provider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet sprites;

        public Provider(final SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(final SimpleParticleType options, final @NonNull ClientLevel level, final double x, final double y, final double z, final double xSpeed, final double ySpeed, final double zSpeed) {
            return new LyreNoteParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites, level.getRandom());
        }

    }

}
