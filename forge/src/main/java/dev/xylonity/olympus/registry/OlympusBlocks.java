package dev.xylonity.olympus.registry;

import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.common.block.AirCloudBlock;
import dev.xylonity.olympus.common.block.ClimbingRoseBlock;
import dev.xylonity.olympus.common.block.OlympusStairBlock;
import dev.xylonity.olympus.common.block.PentelicMarbleColumnBlock;
import dev.xylonity.olympus.common.block.PoppyOfDemeterBlock;
import dev.xylonity.olympus.common.block.LockedChestBlock;
import dev.xylonity.olympus.common.block.ParthenonSpawnerBlock;
import dev.xylonity.knightlib.api.registrar.ResourceDispatcher;
import dev.xylonity.knightlib.api.registrar.ResourceEntry;
import dev.xylonity.knightlib.api.registrar.ResourceRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public final class OlympusBlocks {

    public static final ResourceRegistry<Block> BLOCKS = ResourceDispatcher.create(BuiltInRegistries.BLOCK, Olympus.MOD_ID);

    public static final ResourceEntry<PoppyOfDemeterBlock> POPPY_OF_DEMETER = BLOCKS.register("poppy_of_demeter", () -> new PoppyOfDemeterBlock(
            BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .noOcclusion()
                .noCollission()
                .instabreak()
                .sound(SoundType.GRASS)
                .offsetType(BlockBehaviour.OffsetType.XZ)
                .pushReaction(PushReaction.DESTROY)
    ));

    public static final ResourceEntry<Block> PENTELIC_MARBLE = BLOCKS.register("pentelic_marble", () -> new Block(BlockBehaviour.Properties.copy(Blocks.CALCITE)));
    public static final ResourceEntry<OlympusStairBlock> PENTELIC_MARBLE_STAIRS = BLOCKS.register("pentelic_marble_stairs", () -> new OlympusStairBlock(PENTELIC_MARBLE.get().defaultBlockState(), BlockBehaviour.Properties.copy(Blocks.CALCITE)));
    public static final ResourceEntry<SlabBlock> PENTELIC_MARBLE_SLAB = BLOCKS.register("pentelic_marble_slab", () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.CALCITE)));
    public static final ResourceEntry<WallBlock> PENTELIC_MARBLE_WALL = BLOCKS.register("pentelic_marble_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(Blocks.CALCITE).forceSolidOn()));
    public static final ResourceEntry<Block> POLISHED_PENTELIC_MARBLE = BLOCKS.register("polished_pentelic_marble", () -> new Block(BlockBehaviour.Properties.copy(Blocks.CALCITE)));
    public static final ResourceEntry<OlympusStairBlock> POLISHED_PENTELIC_MARBLE_STAIRS = BLOCKS.register("polished_pentelic_marble_stairs", () -> new OlympusStairBlock(POLISHED_PENTELIC_MARBLE.get().defaultBlockState(), BlockBehaviour.Properties.copy(Blocks.CALCITE)));
    public static final ResourceEntry<SlabBlock> POLISHED_PENTELIC_MARBLE_SLAB = BLOCKS.register("polished_pentelic_marble_slab", () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.CALCITE)));
    public static final ResourceEntry<Block> PENTELIC_MARBLE_BRICK = BLOCKS.register("pentelic_marble_brick", () -> new Block(BlockBehaviour.Properties.copy(Blocks.CALCITE)));
    public static final ResourceEntry<OlympusStairBlock> PENTELIC_MARBLE_BRICK_STAIRS = BLOCKS.register("pentelic_marble_brick_stairs", () -> new OlympusStairBlock(PENTELIC_MARBLE_BRICK.get().defaultBlockState(), BlockBehaviour.Properties.copy(Blocks.CALCITE)));
    public static final ResourceEntry<SlabBlock> PENTELIC_MARBLE_BRICK_SLAB = BLOCKS.register("pentelic_marble_brick_slab", () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.CALCITE)));
    public static final ResourceEntry<WallBlock> PENTELIC_MARBLE_BRICK_WALL = BLOCKS.register("pentelic_marble_brick_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(Blocks.CALCITE).forceSolidOn()));
    public static final ResourceEntry<Block> CRACKED_PENTELIC_MARBLE_BRICK = BLOCKS.register("cracked_pentelic_marble_brick", () -> new Block(BlockBehaviour.Properties.copy(Blocks.CALCITE)));
    public static final ResourceEntry<PentelicMarbleColumnBlock> PENTELIC_MARBLE_COLUMN = BLOCKS.register("pentelic_marble_column", () -> new PentelicMarbleColumnBlock(BlockBehaviour.Properties.copy(Blocks.CALCITE)));

    public static final ResourceEntry<Block> PARTHENON_TERRACOTTA_TILES = BLOCKS.register("parthenon_terracotta_tiles", () -> new Block(BlockBehaviour.Properties.copy(Blocks.TERRACOTTA).sound(SoundType.DEEPSLATE_TILES)));
    public static final ResourceEntry<OlympusStairBlock> PARTHENON_TERRACOTTA_TILE_STAIRS = BLOCKS.register("parthenon_terracotta_tile_stairs", () -> new OlympusStairBlock(PARTHENON_TERRACOTTA_TILES.get().defaultBlockState(), BlockBehaviour.Properties.copy(Blocks.TERRACOTTA).sound(SoundType.DEEPSLATE_TILES)));
    public static final ResourceEntry<SlabBlock> PARTHENON_TERRACOTTA_TILE_SLAB = BLOCKS.register("parthenon_terracotta_tile_slab", () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.TERRACOTTA).sound(SoundType.DEEPSLATE_TILES)));

    public static final ResourceEntry<ClimbingRoseBlock> CLIMBING_ROSE = BLOCKS.register(
            "climbing_rose",
            () -> new ClimbingRoseBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .noOcclusion()
                    .replaceable()
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.AZALEA_LEAVES)
                    .ignitedByLava()
                    .pushReaction(PushReaction.DESTROY)
    ));

    public static final ResourceEntry<AirCloudBlock> AIR_CLOUD_BLOCK = BLOCKS.register("air_cloud_block", () -> new AirCloudBlock(
            BlockBehaviour.Properties.copy(Blocks.POWDER_SNOW)
                    .noOcclusion()
                    .isViewBlocking((state, level, pos) -> false)
    ));

    public static final ResourceEntry<ParthenonSpawnerBlock> PARTHENON_SPAWNER = BLOCKS.register("parthenon_spawner", () -> new ParthenonSpawnerBlock(
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(50.0F, 1200.0F)
                    .sound(OlympusSounds.PARTHENON_SPAWNER_SOUND_TYPE)
                    .lightLevel(state -> state.getValue(ParthenonSpawnerBlock.ACTIVE) || state.getValue(ParthenonSpawnerBlock.REWARD) ? 8 : 0)
                    .noOcclusion()
                    .isViewBlocking((state, level, pos) -> false)));

    public static final ResourceEntry<LockedChestBlock> LOCKED_CHEST = BLOCKS.register("locked_chest", () -> new LockedChestBlock(
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(50.0F, 1200.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

}
