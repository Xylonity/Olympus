package dev.xylonity.olympus.registry;

import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.common.entity.projectile.AbsorbedSoulEntity;
import dev.xylonity.olympus.common.entity.HarpyEntity;
import dev.xylonity.olympus.common.entity.projectile.HarpyProjectileEntity;
import dev.xylonity.olympus.common.entity.projectile.PoseidonTridentEntity;
import dev.xylonity.olympus.common.entity.projectile.SpearOfAresEntity;
import dev.xylonity.olympus.common.entity.projectile.SummoningSpearsEntity;
import dev.xylonity.knightlib.api.registrar.ResourceDispatcher;
import dev.xylonity.knightlib.api.registrar.ResourceEntry;
import dev.xylonity.knightlib.api.registrar.ResourceRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.List;

public final class OlympusEntities {

    public static final ResourceRegistry<EntityType<?>> ENTITIES = ResourceDispatcher.create(BuiltInRegistries.ENTITY_TYPE, Olympus.MOD_ID);

    public static final ResourceEntry<EntityType<AbsorbedSoulEntity>> ABSORBED_SOUL = ENTITIES.registerEntity(
            "absorbed_soul", AbsorbedSoulEntity::new, MobCategory.MISC, 0.1F, 0.1F, List.of(builder -> builder.clientTrackingRange(6).updateInterval(2)));

    public static final ResourceEntry<EntityType<PoseidonTridentEntity>> POSEIDON_TRIDENT = ENTITIES.registerEntity(
            "poseidon_trident", PoseidonTridentEntity::new, MobCategory.MISC, 0.5F, 0.5F, List.of(builder -> builder.clientTrackingRange(4).updateInterval(20)));

    public static final ResourceEntry<EntityType<SpearOfAresEntity>> SPEAR_OF_ARES = ENTITIES.registerEntity(
            "spear_of_ares", SpearOfAresEntity::new, MobCategory.MISC, 0.5F, 0.5F, List.of(builder -> builder.clientTrackingRange(6).updateInterval(1)));

    public static final ResourceEntry<EntityType<SummoningSpearsEntity>> SUMMONING_SPEARS = ENTITIES.registerEntity(
            "summoning_spears", SummoningSpearsEntity::new, MobCategory.MISC, 9.0F, 5.0F, List.of(builder -> builder.clientTrackingRange(10).updateInterval(1)));

    public static final ResourceEntry<EntityType<HarpyEntity>> HARPY = ENTITIES.registerEntity(
            "harpy", HarpyEntity::new, MobCategory.MONSTER, 1.4F, 1.6F, List.of(builder -> builder.clientTrackingRange(8).updateInterval(2)));
    public static final ResourceEntry<EntityType<HarpyEntity>> ELITE_HARPY = ENTITIES.registerEntity(
            "elite_harpy", HarpyEntity::new, MobCategory.MONSTER, 1.4F, 1.6F, List.of(builder -> builder.clientTrackingRange(8).updateInterval(2)));

    public static final ResourceEntry<EntityType<HarpyProjectileEntity>> HARPY_PROJECTILE = ENTITIES.registerEntity(
            "harpy_projectile", HarpyProjectileEntity::new, MobCategory.MISC, 0.3F, 0.3F, List.of(builder -> builder.noSave().clientTrackingRange(8).updateInterval(1)));

}
