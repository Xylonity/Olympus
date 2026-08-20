package dev.xylonity.olympus.registry;

import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.common.block.PoppyOfDemeterBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OlympusBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Olympus.MOD_ID);

    public static final DeferredBlock<PoppyOfDemeterBlock> POPPY_OF_DEMETER = BLOCKS.registerBlock("poppy_of_demeter", PoppyOfDemeterBlock::new,
            properties -> properties
                .mapColor(MapColor.PLANT)
                .noCollision()
                .instabreak()
                .sound(SoundType.GRASS)
                .offsetType(BlockBehaviour.OffsetType.XZ)
                .pushReaction(PushReaction.DESTROY)
    );

}
