package dev.xylonity.olympus.common.worldgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.config.OlympusConfig;
import dev.xylonity.olympus.registry.OlympusStructureTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.jspecify.annotations.NonNull;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public final class CelestialParthenonStructure extends Structure {

    public static final Codec<CelestialParthenonStructure> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            settingsCodec(instance),
            RegistryOps.retrieveRegistryLookup(Registries.BIOME).forGetter(structure -> structure.biomeLookup),
            StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
            Codec.intRange(0, 20).fieldOf("size").forGetter(structure -> structure.maxDepth),
            HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
            Codec.BOOL.fieldOf("use_expansion_hack").forGetter(structure -> structure.expansion),
            Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter)
    ).apply(instance, CelestialParthenonStructure::new));

    private final Holder<StructureTemplatePool> startPool;
    private final HolderLookup.RegistryLookup<Biome> biomeLookup;
    private final String biomeSelection;
    private volatile HolderSet<Biome> configuredBiomes;
    private final int maxDepth;
    private final HeightProvider startHeight;
    private final boolean expansion;
    private final int maxDistanceFromCenter;

    public CelestialParthenonStructure(StructureSettings settings, HolderLookup.RegistryLookup<Biome> biomeLookup, Holder<StructureTemplatePool> startPool, int maxDepth, HeightProvider startHeight, boolean expansion, int maxDistanceFromCenter) {
        super(settings);
        this.biomeLookup = biomeLookup;
        this.biomeSelection = OlympusConfig.PARTHENON_USE_DATAPACK_SETTINGS ? "" : OlympusConfig.PARTHENON_BIOMES.trim();
        this.startPool = startPool;
        this.maxDepth = maxDepth;
        this.startHeight = startHeight;
        this.expansion = expansion;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
    }

    @Override
    public @NonNull HolderSet<Biome> biomes() {
        // No biomes specified
        if (this.biomeSelection.isEmpty()) {
            return super.biomes();
        }

        // Specific biomes
        if (this.configuredBiomes == null) {
            synchronized (this) {
                if (this.configuredBiomes == null) {
                    this.configuredBiomes = resolveBiomes(this.biomeLookup, this.biomeSelection);
                }

            }

        }

        return this.configuredBiomes;
    }

    @Override
    protected @NonNull Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        final ChunkPos chunkPos = context.chunkPos();
        final int y;
        if (OlympusConfig.PARTHENON_USE_DATAPACK_SETTINGS) {
            y = this.startHeight.sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));
        }
        else {
            final int minY = Math.max(context.heightAccessor().getMinBuildHeight(), Math.min(OlympusConfig.PARTHENON_MIN_HEIGHT, OlympusConfig.PARTHENON_MAX_HEIGHT));
            final int maxY = Math.min(context.heightAccessor().getMaxBuildHeight() - 1, Math.max(OlympusConfig.PARTHENON_MIN_HEIGHT, OlympusConfig.PARTHENON_MAX_HEIGHT));
            if (minY > maxY) {
                return Optional.empty();
            }

            y = minY + context.random().nextInt(maxY - minY + 1);
        }

        final BlockPos start = new BlockPos(chunkPos.getMinBlockX(), y, chunkPos.getMinBlockZ());
        return JigsawPlacement.addPieces(context, this.startPool, Optional.empty(), this.maxDepth, start, this.expansion, Optional.empty(), this.maxDistanceFromCenter);
    }

    @Override
    public @NonNull Optional<GenerationStub> findValidGenerationPoint(GenerationContext context) {
        final ChunkPos chunkPos = context.chunkPos();
        final int x = chunkPos.getMiddleBlockX();
        final int z = chunkPos.getMiddleBlockZ();

        final Holder<Biome> surfaceBiome = context.biomeSource().getNoiseBiome(QuartPos.fromBlock(x), QuartPos.fromBlock(context.chunkGenerator().getSeaLevel()), QuartPos.fromBlock(z), context.randomState().sampler());
        return context.validBiome().test(surfaceBiome) ? this.findGenerationPoint(context) : Optional.empty();
    }

    @Override
    public @NonNull StructureType<?> type() {
        return OlympusStructureTypes.CELESTIAL_PARTHENON.get();
    }

    public static HolderSet<Biome> resolveBiomes(final HolderLookup.RegistryLookup<Biome> lookup, final String selection) {
        final Set<Holder<Biome>> biomes = new LinkedHashSet<>();
        // Per biome entry
        for (final String entry : selection.split(",")) {
            final String value = entry.trim();
            if (value.isEmpty()) {
                continue;
            }

            if (value.equals("*")) {
                lookup.listElements().forEach(biomes::add);
                continue;
            }

            final boolean tag = value.startsWith("#");
            final ResourceLocation id = ResourceLocation.tryParse(tag ? value.substring(1) : value);
            // No biome resource definition parsed
            if (id == null) {
                Olympus.LOGGER.warn("Ignoring invalid Celestial Parthenon biome selector: {}", value);
            }
            // Per tag
            else if (tag) {
                lookup.get(TagKey.create(Registries.BIOME, id)).ifPresentOrElse(holders -> holders.forEach(biomes::add), () -> Olympus.LOGGER.warn("Unknown Celestial Parthenon biome tag: {}", value));
            }
            // Per single biome
            else {
                lookup.get(ResourceKey.create(Registries.BIOME, id)).ifPresentOrElse(biomes::add, () -> Olympus.LOGGER.warn("Unknown Celestial Parthenon biome: {}", value));
            }

        }

        if (biomes.isEmpty()) {
            Olympus.LOGGER.warn("No biomes matched when generating the Celestial Parthenon structure");
        }

        return HolderSet.direct(biomes.stream().toList());
    }

}
