package dev.xylonity.olympus.registry;

import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.common.block.entity.PoppyOfDemeterBlockEntity;
import dev.xylonity.olympus.common.block.entity.ParthenonSpawnerBlockEntity;
import dev.xylonity.knightlib.api.registrar.ResourceDispatcher;
import dev.xylonity.knightlib.api.registrar.ResourceEntry;
import dev.xylonity.knightlib.api.registrar.ResourceRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class OlympusBlockEntities {

    public static final ResourceRegistry<BlockEntityType<?>> BLOCK_ENTITIES = ResourceDispatcher.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Olympus.MOD_ID);

    public static final ResourceEntry<BlockEntityType<PoppyOfDemeterBlockEntity>> POPPY_OF_DEMETER = BLOCK_ENTITIES.registerBlockEntity("poppy_of_demeter", PoppyOfDemeterBlockEntity::new, OlympusBlocks.POPPY_OF_DEMETER::get);
    public static final ResourceEntry<BlockEntityType<ParthenonSpawnerBlockEntity>> PARTHENON_SPAWNER = BLOCK_ENTITIES.registerBlockEntity("parthenon_spawner", ParthenonSpawnerBlockEntity::new, OlympusBlocks.PARTHENON_SPAWNER::get);

}
