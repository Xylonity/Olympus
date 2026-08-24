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
                    .icon(() -> new ItemStack(OlympusItems.POPPY_OF_DEMETER.get()))
                    .displayItems((_, output) -> {
                        output.accept(OlympusItems.APHRODITE_LYRE.get());
                        output.accept(OlympusItems.BRACERS_OF_ZEUS.get());
                        output.accept(OlympusItems.HELMET_OF_HADES.get());
                        output.accept(OlympusItems.HERMES_SANDALS.get());
                        output.accept(OlympusItems.INSTRUMENTS_OF_HEPHAESTUS.get());
                        output.accept(OlympusItems.PERSEPHONE_CUP.get());
                        output.accept(OlympusItems.POPPY_OF_DEMETER.get());
                        output.accept(OlympusItems.POSEIDON_TRIDENT.get());
                        output.accept(OlympusItems.SPEAR_OF_ARES.get());
                    })
                    .build()
    );

}
