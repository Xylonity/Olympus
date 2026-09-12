package dev.xylonity.olympus.registry;

import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.common.worldgen.structure.CelestialParthenonStructure;
import dev.xylonity.olympus.common.worldgen.structure.CelestialParthenonPlacement;
import dev.xylonity.knightlib.api.registrar.ResourceDispatcher;
import dev.xylonity.knightlib.api.registrar.ResourceEntry;
import dev.xylonity.knightlib.api.registrar.ResourceRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;

public final class OlympusStructureTypes {

    public static final ResourceRegistry<StructureType<?>> STRUCTURE_TYPES = ResourceDispatcher.create(BuiltInRegistries.STRUCTURE_TYPE, Olympus.MOD_ID);
    public static final ResourceRegistry<StructurePlacementType<?>> PLACEMENT_TYPES = ResourceDispatcher.create(BuiltInRegistries.STRUCTURE_PLACEMENT, Olympus.MOD_ID);

    public static final ResourceEntry<StructureType<CelestialParthenonStructure>> CELESTIAL_PARTHENON = STRUCTURE_TYPES.register("celestial_parthenon", () -> () -> CelestialParthenonStructure.CODEC);
    public static final ResourceEntry<StructurePlacementType<CelestialParthenonPlacement>> CELESTIAL_PARTHENON_PLACEMENT = PLACEMENT_TYPES.register("celestial_parthenon", () -> () -> CelestialParthenonPlacement.CODEC);

}
