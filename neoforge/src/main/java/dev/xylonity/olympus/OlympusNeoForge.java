package dev.xylonity.olympus;


import dev.xylonity.olympus.registry.OlympusItems;
import dev.xylonity.olympus.common.event.BracersOfZeusHandler;
import dev.xylonity.olympus.config.OlympusConfig;
import dev.xylonity.olympus.network.OlympusNetwork;
import dev.xylonity.olympus.registry.OlympusMobEffects;
import dev.xylonity.olympus.registry.OlympusSounds;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Olympus.MOD_ID)
public class OlympusNeoForge {

    public OlympusNeoForge(final IEventBus eventBus, final ModContainer modContainer) {

        OlympusItems.ITEMS.register(eventBus);
        OlympusMobEffects.MOB_EFFECTS.register(eventBus);
        OlympusSounds.SOUND_EVENTS.register(eventBus);

        eventBus.addListener(OlympusNetwork::registerPayloads);
        NeoForge.EVENT_BUS.addListener(BracersOfZeusHandler::onLivingDamage);
        
        modContainer.registerConfig(ModConfig.Type.SERVER, OlympusConfig.SPEC);

        Olympus.init();
    }

}