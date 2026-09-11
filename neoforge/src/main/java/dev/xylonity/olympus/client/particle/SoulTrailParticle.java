package dev.xylonity.olympus.client.particle;

import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.common.entity.projectile.AbsorbedSoulEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public final class SoulTrailParticle extends BaseRibbonTrailParticle {

    private static final ResourceLocation TEXTURE = Olympus.of("textures/particle/soul_trail.png");

    private final int soulEntityId;

    public SoulTrailParticle(final ClientLevel level, final AbsorbedSoulEntity soul) {
        super(level, soul.getX(), soul.getY() + soul.getBbHeight() * 0.5D, soul.getZ(), TEXTURE, 0.09F, 0xBD / 255.0F, 0.0F, 0x1A / 255.0F);
        soulEntityId = soul.getId();
    }

    @Override
    public void tick() {
        final Entity entity = level.getEntity(soulEntityId);
        if (!(entity instanceof AbsorbedSoulEntity soul) || !soul.isAlive()) {
            remove();
            return;
        }

        updateTrackedPosition(soul.getX(), soul.getY() + soul.getBbHeight() * 0.5D, soul.getZ());
    }

    @Override
    protected int getLightColor(final float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

}
