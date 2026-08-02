package dev.xylonity.olympus;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Olympus.MOD_ID)
public class OlympusNeoForge {

    public OlympusNeoForge(IEventBus eventBus) {



        Olympus.init();
    }

}