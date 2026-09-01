package dev.xylonity.olympus.common.item;

import dev.xylonity.olympus.common.util.OlympusTooltip;
import dev.xylonity.olympus.config.OlympusConfig;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

public final class PoppyOfDemeterItem extends BlockItem {

    public PoppyOfDemeterItem(final Block block, final Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(final ItemStack stack, final TooltipContext context, final TooltipDisplay display, final Consumer<Component> tooltip, final TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltip, flag);
        OlympusTooltip.append(tooltip, "poppy_of_demeter", 0xE5A84B,
                OlympusTooltip.ability(1,
                        OlympusTooltip.property("radius", Integer.toString(OlympusConfig.INSTANCE.demeterPoppyRadius.get())),
                        OlympusTooltip.property("growth_interval", OlympusTooltip.seconds(OlympusConfig.INSTANCE.demeterPoppyGrowthIntervalSeconds.get())),
                        OlympusTooltip.property("growths_per_target", Integer.toString(OlympusConfig.INSTANCE.demeterPoppyGrowthsPerTarget.get()))
                ));

    }

}
