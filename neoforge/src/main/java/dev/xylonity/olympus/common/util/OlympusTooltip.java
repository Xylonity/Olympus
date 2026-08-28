package dev.xylonity.olympus.common.util;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;

/// Fast wrapper for tooltip building in the same format a lot of items use
public class OlympusTooltip {

    public static void append(final Consumer<Component> tooltip, final String itemKey, final int titleColor, final Ability... abilities) {
        append(tooltip, itemKey, titleColor, null, abilities);
    }

    public static void appendWithStatus(final Consumer<Component> tooltip, final String itemKey, final int titleColor, final Component status, final Ability... abilities) {
        append(tooltip, itemKey, titleColor, status, abilities);
    }

    private static void append(final Consumer<Component> tooltip, final String itemKey, final int titleColor, final Component status, final Ability... abilities) {
        tooltip.accept(Component.translatable(key(itemKey, "flavor"))
                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));

        if (status != null) {
            tooltip.accept(status);
        }

        tooltip.accept(Component.empty());

        if (!Minecraft.getInstance().hasShiftDown()) {
            final Component shift = Component.literal("[Shift]").withStyle(ChatFormatting.GRAY);
            tooltip.accept(Component.translatable("tooltip.olympus.hold_shift", shift)
                    .withStyle(ChatFormatting.DARK_GRAY));
            return;
        }

        tooltip.accept(Component.translatable(key(itemKey, "abilities"))
                .withStyle(ChatFormatting.DARK_GRAY));

        for (final Ability ability : abilities) {
            tooltip.accept(Component.translatable(key(itemKey, "ability_title_" + ability.number()))
                    .withStyle(style -> style.withColor(TextColor.fromRgb(titleColor))));
            tooltip.accept(Component.translatable(key(itemKey, "ability_description_" + ability.number()))
                    .withStyle(ChatFormatting.GRAY));

            if (ability.properties().isEmpty()) {
                continue;
            }

            tooltip.accept(Component.translatable("tooltip.olympus.properties")
                    .withStyle(ChatFormatting.DARK_GRAY));
            for (final Property property : ability.properties()) {
                final Component value = Component.literal(property.value())
                        .withStyle(style -> style.withColor(TextColor.fromRgb(0xF5C97B)));
                tooltip.accept(Component.literal("  ● ").withStyle(ChatFormatting.DARK_GRAY)
                        .append(Component.translatable(key(itemKey, "properties." + property.name()), value)
                                .withStyle(ChatFormatting.GRAY)));
            }

        }

    }

    public static Ability ability(final int number, final Property... properties) {
        return new Ability(number, List.of(properties));
    }

    public static Property property(final String name, final String value) {
        return new Property(name, value);
    }

    public static String number(final double value) {
        return BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
    }

    public static String seconds(final double value) {
        return number(value) + "s";
    }

    public static String percent(final double fraction) {
        return BigDecimal.valueOf(fraction).multiply(BigDecimal.valueOf(100L)).stripTrailingZeros().toPlainString() + "%";
    }

    private static String key(final String itemKey, final String suffix) {
        return "tooltip.olympus." + itemKey + "." + suffix;
    }

    public record Ability(
            int number,
            List<Property> properties
    ) {
        ;;
    }

    public record Property(
            String name,
            String value
    ) {
        ;;
    }

}
