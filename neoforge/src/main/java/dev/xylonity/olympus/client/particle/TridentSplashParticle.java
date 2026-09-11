package dev.xylonity.olympus.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

public final class TridentSplashParticle extends TextureSheetParticle {

    private static final Quaternionf AZIMUTH = new Quaternionf().rotationX((float) (Math.PI * 0.5D));

    private final SpriteSet sprites;
    private final int frameCount;

    private TridentSplashParticle(final ClientLevel level, final double x, final double y, final double z, final double requestedScale, final SpriteSet sprites, final int frameCount, final int lifetime, final float defaultScale) {
        super(level, x, y, z);

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
    public void render(final VertexConsumer buffer, final Camera camera, final float partialTick) {
        final Vec3 cameraPosition = camera.getPosition();
        final float x = (float) (Mth.lerp(partialTick, xo, this.x) - cameraPosition.x);
        final float y = (float) (Mth.lerp(partialTick, yo, this.y) - cameraPosition.y);
        final float z = (float) (Mth.lerp(partialTick, zo, this.z) - cameraPosition.z);
        final Vector3f[] vertices = {
                new Vector3f(-1, -1, 0),
                new Vector3f(-1, 1, 0),
                new Vector3f(1, 1, 0),
                new Vector3f(1, -1, 0)
        };
        final float size = getQuadSize(partialTick);
        for (final Vector3f vertex : vertices) {
            vertex.rotate(AZIMUTH);
            vertex.mul(size);
            vertex.add(x, y, z);
        }

        final int light = getLightColor(partialTick);
        buffer.addVertex(vertices[0]).setUv(getU1(), getV1()).setColor(rCol, gCol, bCol, alpha).setLight(light);
        buffer.addVertex(vertices[1]).setUv(getU1(), getV0()).setColor(rCol, gCol, bCol, alpha).setLight(light);
        buffer.addVertex(vertices[2]).setUv(getU0(), getV0()).setColor(rCol, gCol, bCol, alpha).setLight(light);
        buffer.addVertex(vertices[3]).setUv(getU0(), getV1()).setColor(rCol, gCol, bCol, alpha).setLight(light);
    }

    @Override
    public @NonNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected int getLightColor(float a) {
        // Prevents the black overlay if inside a block
        return level.getBlockState(BlockPos.containing(getPos())).isAir() ? super.getLightColor(a) : LightTexture.pack(0, 15);
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
        public Particle createParticle(final SimpleParticleType options, final ClientLevel level, final double x, final double y, final double z, final double xSpeed, final double ySpeed, final double zSpeed) {
            return new TridentSplashParticle(level, x, y, z, xSpeed, sprites, 7, 12, 3.5F);
        }

    }

    public static final class SmallProvider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet sprites;

        public SmallProvider(final SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(final SimpleParticleType options, final ClientLevel level, final double x, final double y, final double z, final double xSpeed, final double ySpeed, final double zSpeed) {
            final double scale = xSpeed > 0.0D ? xSpeed : 0.20F + level.getRandom().nextFloat() * 0.3F;
            return new TridentSplashParticle(level, x, y, z, scale, sprites, 5, 8, 0.20F);
        }

    }

}
