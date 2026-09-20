package dev.xylonity.olympus.common.compat;

import dev.xylonity.olympus.Olympus;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.MissingMappingsEvent;

@Mod.EventBusSubscriber(modid = Olympus.MOD_ID)
public final class LegacyRegistryRemapper {

    @SubscribeEvent
    public static void onMissingMappings(final MissingMappingsEvent event) {
        remap(event, ForgeRegistries.Keys.BLOCKS, ForgeRegistries.BLOCKS);
        remap(event, ForgeRegistries.Keys.ITEMS, ForgeRegistries.ITEMS);
        remap(event, ForgeRegistries.Keys.ENTITY_TYPES, ForgeRegistries.ENTITY_TYPES);
        remap(event, ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, ForgeRegistries.BLOCK_ENTITY_TYPES);
        remap(event, ForgeRegistries.Keys.MOB_EFFECTS, ForgeRegistries.MOB_EFFECTS);
        remap(event, ForgeRegistries.Keys.SOUND_EVENTS, ForgeRegistries.SOUND_EVENTS);
        remap(event, ForgeRegistries.Keys.PARTICLE_TYPES, ForgeRegistries.PARTICLE_TYPES);
    }

    private static <T> void remap(final MissingMappingsEvent event, final ResourceKey<Registry<T>> key, final IForgeRegistry<T> registry) {
        for (MissingMappingsEvent.Mapping<T> mapping : event.getMappings(key, LegacyNamespace.ID)) {
            final T target = registry.getValue(LegacyNamespace.migrate(mapping.getKey()));
            if (target != null) {
                mapping.remap(target);
                Olympus.LOGGER.info("Remapped legacy entry {} -> {}", mapping.getKey(), LegacyNamespace.migrate(mapping.getKey()));
            }
            else {
                mapping.ignore();
                Olympus.LOGGER.warn("Legacy entry {} has no counterpart... ignoring", mapping.getKey());
            }

        }

    }

}
