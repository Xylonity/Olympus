package dev.xylonity.olympus.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.jspecify.annotations.NonNull;

/// Vanilla falling leaves particle logic backport (mc26+)
abstract class FallingLeavesParticle extends TextureSheetParticle {

    private static final float ACCELERATION_SCALE = 0.0025F;
    private static final int INITIAL_LIFETIME = 300;

    private float rotSpeed;
    private final float spinAcceleration;
    private final float windBig;
    private final boolean swirl;
    private final boolean flowAway;
    private final double xaFlowScale;
    private final double zaFlowScale;
    private final double swirlPeriod;

    public FallingLeavesParticle(final ClientLevel level, final double x, final double y, final double z, final TextureAtlasSprite sprite, final float gravityScale, final float windBig, final boolean swirl, final boolean flowAway, final float leafSize, final float initialFallSpeed) {
        super(level, x, y, z);
        setSprite(sprite);
        rotSpeed = (float) Math.toRadians(random.nextBoolean() ? -30.0D : 30.0D);
        spinAcceleration = (float) Math.toRadians(random.nextBoolean() ? -5.0D : 5.0D);
        this.windBig = windBig;
        this.swirl = swirl;
        this.flowAway = flowAway;
        lifetime = INITIAL_LIFETIME;
        gravity = gravityScale * 1.2F * ACCELERATION_SCALE;

        final float size = leafSize * (random.nextBoolean() ? 0.05F : 0.075F);
        quadSize = size;
        setSize(size, size);
        friction = 1.0F;
        yd = -initialFallSpeed;

        final float direction = random.nextFloat();
        xaFlowScale = Math.cos(Math.toRadians(direction * 60.0F)) * this.windBig;
        zaFlowScale = Math.sin(Math.toRadians(direction * 60.0F)) * this.windBig;
        swirlPeriod = Math.toRadians(1000.0F + direction * 3000.0F);
    }

    @Override
    public void tick() {
        xo = x;
        yo = y;
        zo = z;
        if (lifetime-- <= 0) {
            remove();
        }
        if (removed) {
            return;
        }

        final float elapsed = INITIAL_LIFETIME - lifetime;
        final float progress = Math.min(elapsed / INITIAL_LIFETIME, 1.0F);
        double xFlow = 0.0D;
        double zFlow = 0.0D;
        if (flowAway) {
            xFlow += xaFlowScale * Math.pow(progress, 1.25D);
            zFlow += zaFlowScale * Math.pow(progress, 1.25D);
        }
        if (swirl) {
            xFlow += progress * Math.cos(progress * swirlPeriod) * windBig;
            zFlow += progress * Math.sin(progress * swirlPeriod) * windBig;
        }

        xd += xFlow * ACCELERATION_SCALE;
        zd += zFlow * ACCELERATION_SCALE;
        yd -= gravity;
        rotSpeed += spinAcceleration / 20.0F;
        oRoll = roll;
        roll += rotSpeed / 20.0F;
        move(xd, yd, zd);

        if (onGround || lifetime < INITIAL_LIFETIME - 1 && (xd == 0.0D || zd == 0.0D)) {
            remove();
        }
        if (removed) {
            return;
        }

        xd *= friction;
        yd *= friction;
        zd *= friction;
    }

    @Override
    public @NonNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

}