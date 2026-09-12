package dev.xylonity.olympus.common.worldgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.xylonity.olympus.common.worldgen.ParthenonWorldgen;
import dev.xylonity.olympus.config.OlympusConfig;
import dev.xylonity.olympus.registry.OlympusStructureTypes;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;

import java.util.Optional;

public final class CelestialParthenonPlacement extends RandomSpreadStructurePlacement {

    public static final Codec<CelestialParthenonPlacement> CODEC = RecordCodecBuilder.create(instance ->
            placementCodec(instance).and(instance.group(
                    Codec.intRange(1, 4096).fieldOf("spacing").forGetter(CelestialParthenonPlacement::spacing),
                    Codec.intRange(0, 4095).fieldOf("separation").forGetter(CelestialParthenonPlacement::separation),
                    RandomSpreadType.CODEC.optionalFieldOf("spread_type", RandomSpreadType.TRIANGULAR).forGetter(CelestialParthenonPlacement::spreadType)
            )).apply(instance, CelestialParthenonPlacement::new)
    );

    public CelestialParthenonPlacement(Vec3i locateOffset, FrequencyReductionMethod frequencyReductionMethod, float frequency, int salt, Optional<ExclusionZone> exclusionZone, int spacing, int separation, RandomSpreadType spreadType) {
        super(locateOffset, frequencyReductionMethod, OlympusConfig.PARTHENON_USE_DATAPACK_SETTINGS ? frequency : (float) OlympusConfig.PARTHENON_GENERATION_CHANCE, salt, exclusionZone, configuredSpacing(spacing), configuredSeparation(spacing, separation), spreadType);
    }

    private static int configuredSpacing(final int spacing) {
        return Mth.clamp(OlympusConfig.PARTHENON_USE_DATAPACK_SETTINGS ? spacing : OlympusConfig.PARTHENON_SPACING, 1, 4096);
    }

    private static int configuredSeparation(final int spacing, final int separation) {
        return Mth.clamp(OlympusConfig.PARTHENON_USE_DATAPACK_SETTINGS ? separation : OlympusConfig.PARTHENON_SEPARATION, 0, configuredSpacing(spacing) - 1);
    }

    @Override
    protected boolean isPlacementChunk(final ChunkGeneratorStructureState state, final int x, final int z) {
        return ParthenonWorldgen.isGenerationAllowed(state) && super.isPlacementChunk(state, x, z);
    }

    @Override
    public StructurePlacementType<?> type() {
        return OlympusStructureTypes.CELESTIAL_PARTHENON_PLACEMENT.get();
    }

}
