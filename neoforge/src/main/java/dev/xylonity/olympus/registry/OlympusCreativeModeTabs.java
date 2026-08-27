package dev.xylonity.olympus.registry;

import dev.xylonity.olympus.Olympus;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OlympusCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Olympus.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> OLYMPUS = CREATIVE_MODE_TABS.register("olympus",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativetab.olympus.title"))
                    .icon(() -> new ItemStack(OlympusItems.HELMET_OF_HADES.get()))
                    .displayItems((_, output) -> {
                        output.accept(OlympusItems.APHRODITE_LYRE.get());
                        output.accept(OlympusItems.BRACERS_OF_ZEUS.get());
                        output.accept(OlympusItems.BOW_OF_ARTEMIS.get());
                        output.accept(OlympusItems.HELMET_OF_HADES.get());
                        output.accept(OlympusItems.HERMES_SANDALS.get());
                        output.accept(OlympusItems.INSTRUMENTS_OF_HEPHAESTUS.get());
                        output.accept(OlympusItems.PERSEPHONE_CUP.get());
                        output.accept(OlympusItems.POPPY_OF_DEMETER.get());
                        output.accept(OlympusItems.POSEIDON_TRIDENT.get());
                        output.accept(OlympusItems.SPEAR_OF_ARES.get());
                        output.accept(OlympusItems.PENTELIC_MARBLE.get());
                        output.accept(OlympusItems.PENTELIC_MARBLE_STAIRS.get());
                        output.accept(OlympusItems.PENTELIC_MARBLE_SLAB.get());
                        output.accept(OlympusItems.PENTELIC_MARBLE_WALL.get());
                        output.accept(OlympusItems.POLISHED_PENTELIC_MARBLE.get());
                        output.accept(OlympusItems.POLISHED_PENTELIC_MARBLE_STAIRS.get());
                        output.accept(OlympusItems.POLISHED_PENTELIC_MARBLE_SLAB.get());
                        output.accept(OlympusItems.PENTELIC_MARBLE_BRICK.get());
                        output.accept(OlympusItems.PENTELIC_MARBLE_BRICK_STAIRS.get());
                        output.accept(OlympusItems.PENTELIC_MARBLE_BRICK_SLAB.get());
                        output.accept(OlympusItems.PENTELIC_MARBLE_BRICK_WALL.get());
                        output.accept(OlympusItems.CRACKED_PENTELIC_MARBLE_BRICK.get());
                        output.accept(OlympusItems.PENTELIC_MARBLE_COLUMN.get());
                        output.accept(OlympusItems.PARTHENON_TERRACOTTA_TILES.get());
                        output.accept(OlympusItems.PARTHENON_TERRACOTTA_TILE_STAIRS.get());
                        output.accept(OlympusItems.PARTHENON_TERRACOTTA_TILE_SLAB.get());
                        output.accept(OlympusItems.CLIMBING_ROSE.get());
                        output.accept(OlympusItems.AIR_CLOUD_BLOCK.get());
                    })
                    .build()
    );

}
