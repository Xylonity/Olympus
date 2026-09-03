package dev.xylonity.olympus.registry;

import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.common.item.BracersOfZeusItem;
import dev.xylonity.olympus.common.item.ArtemisBowItem;
import dev.xylonity.olympus.common.item.AphroditeLyreItem;
import dev.xylonity.olympus.common.item.HelmetOfHadesItem;
import dev.xylonity.olympus.common.item.HermesSandalsItem;
import dev.xylonity.olympus.common.item.InstrumentsOfHephaestusItem;
import dev.xylonity.olympus.common.item.PersephoneCupItem;
import dev.xylonity.olympus.common.item.PoppyOfDemeterItem;
import dev.xylonity.olympus.common.item.PoseidonTridentItem;
import dev.xylonity.olympus.common.item.SpearOfAresItem;
import dev.xylonity.knightlib.api.registrar.ResourceDispatcher;
import dev.xylonity.knightlib.api.registrar.ResourceEntry;
import dev.xylonity.knightlib.api.registrar.ResourceRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;

public class OlympusItems {

    public static final ResourceRegistry<Item> ITEMS = ResourceDispatcher.create(BuiltInRegistries.ITEM, Olympus.MOD_ID);

    public static final ResourceEntry<Item> HARPY_SPAWN_EGG = ITEMS.registerSpawnEgg("harpy_spawn_egg", OlympusEntities.HARPY, 0xA56284, 0xE7C18F, new Item.Properties());
    public static final ResourceEntry<Item> ELITE_HARPY_SPAWN_EGG = ITEMS.registerSpawnEgg("elite_harpy_spawn_egg", OlympusEntities.ELITE_HARPY, 0x71508A, 0xEED594, new Item.Properties());
    public static final ResourceEntry<Item> PARTHENON_KEY = ITEMS.register("parthenon_key", () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static final ResourceEntry<BlockItem> PARTHENON_SPAWNER = ITEMS.register("parthenon_spawner", () -> new BlockItem(OlympusBlocks.PARTHENON_SPAWNER.get(), new Item.Properties()));
    public static final ResourceEntry<BlockItem> LOCKED_CHEST = ITEMS.register("locked_chest", () -> new BlockItem(OlympusBlocks.LOCKED_CHEST.get(), new Item.Properties()));
    public static final ResourceEntry<BracersOfZeusItem> BRACERS_OF_ZEUS = ITEMS.register("bracers_of_zeus", () -> new BracersOfZeusItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final ResourceEntry<ArtemisBowItem> BOW_OF_ARTEMIS = ITEMS.register("bow_of_artemis", () -> new ArtemisBowItem(new Item.Properties().durability(384).rarity(Rarity.EPIC)));
    public static final ResourceEntry<AphroditeLyreItem> APHRODITE_LYRE = ITEMS.register("aphrodite_lyre", () -> new AphroditeLyreItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final ResourceEntry<HelmetOfHadesItem> HELMET_OF_HADES = ITEMS.register("helmet_of_hades", () -> new HelmetOfHadesItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final ResourceEntry<HermesSandalsItem> HERMES_SANDALS = ITEMS.register("hermes_sandals", () -> new HermesSandalsItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final ResourceEntry<InstrumentsOfHephaestusItem> INSTRUMENTS_OF_HEPHAESTUS = ITEMS.register("instruments_of_hephaestus", () -> new InstrumentsOfHephaestusItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final ResourceEntry<PersephoneCupItem> PERSEPHONE_CUP = ITEMS.register("persephone_cup", () -> new PersephoneCupItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final ResourceEntry<PoppyOfDemeterItem> POPPY_OF_DEMETER = ITEMS.register("poppy_of_demeter", () -> new PoppyOfDemeterItem(OlympusBlocks.POPPY_OF_DEMETER.get(), new Item.Properties().rarity(Rarity.EPIC)));
    public static final ResourceEntry<BlockItem> PENTELIC_MARBLE = blockItem("pentelic_marble", OlympusBlocks.PENTELIC_MARBLE);
    public static final ResourceEntry<BlockItem> PENTELIC_MARBLE_STAIRS = blockItem("pentelic_marble_stairs", OlympusBlocks.PENTELIC_MARBLE_STAIRS);
    public static final ResourceEntry<BlockItem> PENTELIC_MARBLE_SLAB = blockItem("pentelic_marble_slab", OlympusBlocks.PENTELIC_MARBLE_SLAB);
    public static final ResourceEntry<BlockItem> PENTELIC_MARBLE_WALL = blockItem("pentelic_marble_wall", OlympusBlocks.PENTELIC_MARBLE_WALL);
    public static final ResourceEntry<BlockItem> POLISHED_PENTELIC_MARBLE = blockItem("polished_pentelic_marble", OlympusBlocks.POLISHED_PENTELIC_MARBLE);
    public static final ResourceEntry<BlockItem> POLISHED_PENTELIC_MARBLE_STAIRS = blockItem("polished_pentelic_marble_stairs", OlympusBlocks.POLISHED_PENTELIC_MARBLE_STAIRS);
    public static final ResourceEntry<BlockItem> POLISHED_PENTELIC_MARBLE_SLAB = blockItem("polished_pentelic_marble_slab", OlympusBlocks.POLISHED_PENTELIC_MARBLE_SLAB);
    public static final ResourceEntry<BlockItem> PENTELIC_MARBLE_BRICK = blockItem("pentelic_marble_brick", OlympusBlocks.PENTELIC_MARBLE_BRICK);
    public static final ResourceEntry<BlockItem> PENTELIC_MARBLE_BRICK_STAIRS = blockItem("pentelic_marble_brick_stairs", OlympusBlocks.PENTELIC_MARBLE_BRICK_STAIRS);
    public static final ResourceEntry<BlockItem> PENTELIC_MARBLE_BRICK_SLAB = blockItem("pentelic_marble_brick_slab", OlympusBlocks.PENTELIC_MARBLE_BRICK_SLAB);
    public static final ResourceEntry<BlockItem> PENTELIC_MARBLE_BRICK_WALL = blockItem("pentelic_marble_brick_wall", OlympusBlocks.PENTELIC_MARBLE_BRICK_WALL);
    public static final ResourceEntry<BlockItem> CRACKED_PENTELIC_MARBLE_BRICK = blockItem("cracked_pentelic_marble_brick", OlympusBlocks.CRACKED_PENTELIC_MARBLE_BRICK);
    public static final ResourceEntry<BlockItem> PENTELIC_MARBLE_COLUMN = blockItem("pentelic_marble_column", OlympusBlocks.PENTELIC_MARBLE_COLUMN);
    public static final ResourceEntry<BlockItem> PARTHENON_TERRACOTTA_TILES = blockItem("parthenon_terracotta_tiles", OlympusBlocks.PARTHENON_TERRACOTTA_TILES);
    public static final ResourceEntry<BlockItem> PARTHENON_TERRACOTTA_TILE_STAIRS = blockItem("parthenon_terracotta_tile_stairs", OlympusBlocks.PARTHENON_TERRACOTTA_TILE_STAIRS);
    public static final ResourceEntry<BlockItem> PARTHENON_TERRACOTTA_TILE_SLAB = blockItem("parthenon_terracotta_tile_slab", OlympusBlocks.PARTHENON_TERRACOTTA_TILE_SLAB);
    public static final ResourceEntry<BlockItem> CLIMBING_ROSE = blockItem("climbing_rose", OlympusBlocks.CLIMBING_ROSE);
    public static final ResourceEntry<BlockItem> AIR_CLOUD_BLOCK = blockItem("air_cloud_block", OlympusBlocks.AIR_CLOUD_BLOCK);
    public static final ResourceEntry<PoseidonTridentItem> POSEIDON_TRIDENT = ITEMS.register("poseidon_trident", () -> new PoseidonTridentItem(new Item.Properties().rarity(Rarity.EPIC).durability(2000)));
    public static final ResourceEntry<SpearOfAresItem> SPEAR_OF_ARES = ITEMS.register("spear_of_ares", () -> new SpearOfAresItem(new Item.Properties().rarity(Rarity.EPIC).durability(2000)));

    private static ResourceEntry<BlockItem> blockItem(final String name, final ResourceEntry<? extends Block> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

}
