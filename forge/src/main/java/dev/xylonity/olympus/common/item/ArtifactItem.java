package dev.xylonity.olympus.common.item;

import dev.xylonity.olympus.config.OlympusConfig;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Base class for the artifacts that have no durability, so they can be made enchantable through the config
 */
public abstract class ArtifactItem extends Item {

    private static final int ENCHANTMENT_VALUE = 15;

    protected ArtifactItem(final Properties properties) {
        super(properties);
    }

    @Override
    public boolean isEnchantable(final ItemStack stack) {
        return OlympusConfig.ARTIFACTS_ENCHANTABLE || super.isEnchantable(stack);
    }

    @Override
    public int getEnchantmentValue(final ItemStack stack) {
        return OlympusConfig.ARTIFACTS_ENCHANTABLE ? ENCHANTMENT_VALUE : super.getEnchantmentValue(stack);
    }

    // Used by both the enchanting table and the anvil (Enchantment#canEnchant delegates here)
    @Override
    public boolean canApplyAtEnchantingTable(final ItemStack stack, final Enchantment enchantment) {
        return OlympusConfig.ARTIFACTS_ENCHANTABLE || super.canApplyAtEnchantingTable(stack, enchantment);
    }

}
