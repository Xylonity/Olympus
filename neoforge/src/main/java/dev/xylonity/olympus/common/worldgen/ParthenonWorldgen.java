package dev.xylonity.olympus.common.worldgen;

import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.config.OlympusConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public final class ParthenonWorldgen {

    // The structure generation ctx doesnt expose a dimension per se, so I memoize the level before chunk loading
    private static final Map<ChunkGeneratorStructureState, Boolean> ALLOWED_LEVELS = Collections.synchronizedMap(new WeakHashMap<>());

    public static void loadLevel(final ServerLevel level) {
        final String dimension = level.dimension().location().toString();
        boolean allowed = false;
        for (final String entry : OlympusConfig.PARTHENON_DIMENSIONS.split(",")) {
            final String value = entry.trim();
            if (value.equals("*") || value.equals(dimension)) {
                allowed = true;
            }
            else if (!value.isEmpty() && ResourceLocation.tryParse(value) == null) {
                Olympus.LOGGER.warn("Ignoring invalid Celestial Parthenon dimension id: {}", value);
            }

        }

        ALLOWED_LEVELS.put(level.getChunkSource().getGeneratorState(), OlympusConfig.PARTHENON_GENERATION_ENABLED && allowed);
    }

    public static void unloadLevel(final ServerLevel level) {
        ALLOWED_LEVELS.remove(level.getChunkSource().getGeneratorState());
    }

    public static boolean isGenerationAllowed(final ChunkGeneratorStructureState state) {
        return Boolean.TRUE.equals(ALLOWED_LEVELS.get(state));
    }

}

