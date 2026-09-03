package dev.xylonity.olympus.client.particle;

import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.common.entity.projectile.HarpyProjectileEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

/// Same logic as {@link SoulTrailParticle}
public final class HarpyProjectileTrailParticle extends BaseRibbonTrailParticle {

    private static final ResourceLocation TEXTURE = Olympus.of("textures/particle/harpy_projectile_trail.png");

    private final int projectileEntityId;

    public HarpyProjectileTrailParticle(final ClientLevel level, final HarpyProjectileEntity projectile) {
        super(level, projectile.getX(), projectile.getY() + projectile.getBbHeight() * 0.5D, projectile.getZ(), TEXTURE, 0.09F, 0xAC / 255.0F, 0xC8 / 255.0F, 0xE2 / 255.0F);
        projectileEntityId = projectile.getId();
    }

    @Override
    public void tick() {
        final Entity entity = level.getEntity(projectileEntityId);
        if (!(entity instanceof HarpyProjectileEntity projectile) || !projectile.isAlive()) {
            remove();
            return;
        }

        updateTrackedPosition(projectile.getX(), projectile.getY() + projectile.getBbHeight() * 0.5D, projectile.getZ());
    }

    @Override
    protected int getLightColor(final float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

}
