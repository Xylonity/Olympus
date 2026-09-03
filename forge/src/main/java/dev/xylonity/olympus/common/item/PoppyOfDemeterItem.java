package dev.xylonity.olympus.common.item;

import dev.xylonity.olympus.common.util.OlympusTooltip;
import dev.xylonity.olympus.config.OlympusConfig;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public final class PoppyOfDemeterItem extends BlockItem {

    public PoppyOfDemeterItem(final Block block, final Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(final ItemStack stack, final Level level, final List<Component> tooltip, final TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        OlympusTooltip.append(tooltip::add, "poppy_of_demeter", 0xE5A84B,
                OlympusTooltip.ability(1,
                        OlympusTooltip.property("radius", Integer.toString(OlympusConfig.DEMETER_POPPY_RADIUS)),
                        OlympusTooltip.property("growth_interval", OlympusTooltip.seconds(OlympusConfig.DEMETER_POPPY_GROWTH_INTERVAL_SECONDS)),
                        OlympusTooltip.property("growths_per_target", Integer.toString(OlympusConfig.DEMETER_POPPY_GROWTHS_PER_TARGET))
                ));

    }

}
