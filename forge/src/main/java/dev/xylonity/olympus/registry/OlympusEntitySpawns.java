package dev.xylonity.olympus.registry;

import dev.xylonity.knightlib.api.entity.spawn.SpawnConfig;
import dev.xylonity.knightlib.api.registrar.ResourceEntry;
import dev.xylonity.knightlib.api.spawn.KnightLibEntityBiomeSpawns;
import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.config.OlympusConfig;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;

public final class OlympusEntitySpawns {

    public static void init() {
        register(OlympusEntities.HARPY, "HARPY_SPAWN", OlympusConfig.HARPY_SPAWN);
        register(OlympusEntities.ELITE_HARPY, "ELITE_HARPY_SPAWN", OlympusConfig.ELITE_HARPY_SPAWN);
    }

    private static <T extends Mob> void register(final ResourceEntry<EntityType<T>> entity, final String configName, final String configLine) {
        final SpawnConfig spawn;
        try {
            spawn = SpawnConfig.parse(configLine);
        }
        catch (final RuntimeException exception) {
            Olympus.LOGGER.error("Invalid {} config value \"{}\", expected [weight, minAmount, maxAmount, biomes and tags...]. Natural spawning is disabled for this entity", configName, configLine);
            return;
        }

        if (spawn.weight <= 0) {
            return;
        }

        final int minCount = Math.max(1, spawn.minCount);
        final int maxCount = Math.max(minCount, spawn.maxCount);
        KnightLibEntityBiomeSpawns.builder(entity, MobCategory.MONSTER)
                .spawnRate(spawn.weight, minCount, maxCount)
                .biomeFilter(spawn::matches)
                .submit();
    }

}
