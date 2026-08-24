package dev.xylonity.olympus.common.item;

import dev.xylonity.olympus.common.entity.ArtemisArrow;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class ArtemisBowItem extends BowItem {

    public ArtemisBowItem(final Properties properties) {
        super(properties);
    }

    @Override
    protected Projectile createProjectile(final Level level, final LivingEntity shooter, final ItemStack bowStack, final ItemStack ammunition, final boolean critical) {
        final Projectile projectile = super.createProjectile(level, shooter, bowStack, ammunition, critical);
        if (projectile instanceof ArtemisArrow artemisArrow) {
            artemisArrow.setArtemisArrow(true);
        }

        return projectile;
    }

    @Override
    protected void shootProjectile(final LivingEntity shooter, final Projectile projectile, final int projectileIndex, final float velocity, final float inaccuracy, final float angle, final LivingEntity target) {
        super.shootProjectile(shooter, projectile, projectileIndex, velocity * 1.2f, inaccuracy, angle, target);
    }

    @Override
    public int getDefaultProjectileRange() {
        return 22;
    }

}
