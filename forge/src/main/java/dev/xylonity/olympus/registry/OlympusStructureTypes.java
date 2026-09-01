package dev.xylonity.olympus.registry;

import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.common.worldgen.structure.CelestialParthenonStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OlympusStructureTypes {

    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(Registries.STRUCTURE_TYPE, Olympus.MOD_ID);

    public static final DeferredHolder<StructureType<?>, StructureType<CelestialParthenonStructure>> CELESTIAL_PARTHENON = STRUCTURE_TYPES.register("celestial_parthenon", () -> () -> CelestialParthenonStructure.CODEC);

}
