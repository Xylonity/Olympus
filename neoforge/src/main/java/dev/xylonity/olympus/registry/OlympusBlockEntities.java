package dev.xylonity.olympus.registry;

import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.common.block.entity.PoppyOfDemeterBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OlympusBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Olympus.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PoppyOfDemeterBlockEntity>> POPPY_OF_DEMETER = BLOCK_ENTITIES.register("poppy_of_demeter", () -> new BlockEntityType<>(PoppyOfDemeterBlockEntity::new, OlympusBlocks.POPPY_OF_DEMETER.get()));

}
