package dev.xylonity.olympus;


import dev.xylonity.olympus.registry.OlympusItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Olympus.MOD_ID)
public class OlympusNeoForge {

    public OlympusNeoForge(final IEventBus eventBus) {

        OlympusItems.ITEMS.register(eventBus);

        Olympus.init();
    }

}