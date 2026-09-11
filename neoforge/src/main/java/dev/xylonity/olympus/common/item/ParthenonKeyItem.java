package dev.xylonity.olympus.common.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public final class ParthenonKeyItem extends Item {

    public ParthenonKeyItem(final Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(final ItemStack stack, final Item.TooltipContext context, final List<Component> tooltip, final TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.olympus.parthenon_key").withStyle(ChatFormatting.DARK_GRAY));
    }

}
