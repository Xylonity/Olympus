package dev.xylonity.olympus.common.item;

import dev.xylonity.olympus.common.util.ArtemisArrow;
import dev.xylonity.olympus.common.util.OlympusTooltip;
import dev.xylonity.olympus.config.OlympusConfig;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

public final class ArtemisBowItem extends BowItem {

    public ArtemisBowItem(final Properties properties) {
        super(properties);
    }

    @Override
    protected Projectile createProjectile(final Level level, final LivingEntity shooter, final ItemStack bowStack, final ItemStack ammunition, final boolean critical) {
        final Projectile projectile = super.createProjectile(level, shooter, bowStack, ammunition, critical);
        if (projectile instanceof ArtemisArrow artemisArrow) {
            artemisArrow.olympus$setArtemisArrow(true);
        }

        return projectile;
    }

    @Override
    protected void shootProjectile(final LivingEntity shooter, final Projectile projectile, final int projectileIndex, final float velocity, final float inaccuracy, final float angle, final LivingEntity target) {
        final float speedMultiplier = OlympusConfig.INSTANCE.artemisBowProjectileSpeedMultiplier.get().floatValue();
        super.shootProjectile(shooter, projectile, projectileIndex, velocity * speedMultiplier, inaccuracy, angle, target);
    }

    @Override
    public int getDefaultProjectileRange() {
        return 22;
    }

    @Override
    public void appendHoverText(final ItemStack stack, final TooltipContext context, final TooltipDisplay display, final Consumer<Component> tooltip, final TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltip, flag);
        OlympusTooltip.append(tooltip, "bow_of_artemis", 0x8FD17F,
                OlympusTooltip.ability(1,
                        OlympusTooltip.property("projectile_speed", OlympusTooltip.number(OlympusConfig.INSTANCE.artemisBowProjectileSpeedMultiplier.get()) + "x"),
                        OlympusTooltip.property("looting_bonus", "+" + OlympusConfig.INSTANCE.artemisBowLootingBonus.get())
                ),
                OlympusTooltip.ability(2,
                        OlympusTooltip.property("tamed_healing", OlympusTooltip.percent(OlympusConfig.INSTANCE.artemisBowTamedHealingMultiplier.get()))
                ));

    }

}
