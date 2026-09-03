package dev.xylonity.olympus.common.item;

import dev.xylonity.olympus.common.util.OlympusTooltip;
import dev.xylonity.olympus.config.OlympusConfig;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public final class ArtemisBowItem extends BowItem {

    public ArtemisBowItem(final Properties properties) {
        super(properties);
    }

    @Override
    public int getDefaultProjectileRange() {
        return 22;
    }

    @Override
    public void appendHoverText(final ItemStack stack, final Level level, final List<Component> tooltip, final TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        OlympusTooltip.append(tooltip::add, "bow_of_artemis", 0x8FD17F,
                OlympusTooltip.ability(1,
                        OlympusTooltip.property("projectile_speed", OlympusTooltip.number(OlympusConfig.ARTEMIS_BOW_PROJECTILE_SPEED_MULTIPLIER) + "x"),
                        OlympusTooltip.property("looting_bonus", "+" + OlympusConfig.ARTEMIS_BOW_LOOTING_BONUS)
                ),
                OlympusTooltip.ability(2,
                        OlympusTooltip.property("tamed_healing", OlympusTooltip.percent(OlympusConfig.ARTEMIS_BOW_TAMED_HEALING_MULTIPLIER))
                ));

    }

}
