package dev.xylonity.olympus;

import dev.xylonity.knightlib.api.config.ConfigComposer;
import dev.xylonity.olympus.config.OlympusConfig;
import dev.xylonity.olympus.network.OlympusNetwork;
import dev.xylonity.olympus.registry.OlympusBlockEntities;
import dev.xylonity.olympus.registry.OlympusBlocks;
import dev.xylonity.olympus.registry.OlympusCreativeModeTabs;
import dev.xylonity.olympus.registry.OlympusEntities;
import dev.xylonity.olympus.registry.OlympusItems;
import dev.xylonity.olympus.registry.OlympusMobEffects;
import dev.xylonity.olympus.registry.OlympusParticles;
import dev.xylonity.olympus.registry.OlympusSounds;
import dev.xylonity.olympus.registry.OlympusStructureTypes;
import net.minecraftforge.fml.common.Mod;

@Mod(Olympus.MOD_ID)
public class OlympusForge {

    public OlympusForge() {
        OlympusEntities.ENTITIES.init();
        OlympusBlocks.BLOCKS.init();
        OlympusBlockEntities.BLOCK_ENTITIES.init();
        OlympusItems.ITEMS.init();
        OlympusCreativeModeTabs.CREATIVE_MODE_TABS.init();
        OlympusMobEffects.MOB_EFFECTS.init();
        OlympusParticles.PARTICLES.init();
        OlympusSounds.SOUND_EVENTS.init();
        OlympusStructureTypes.STRUCTURE_TYPES.init();

        OlympusNetwork.register();
        ConfigComposer.registerConfig(Olympus.MOD_ID, OlympusConfig.class);

        Olympus.init();
    }

}
