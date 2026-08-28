package dev.xylonity.olympus.registry;

import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.common.entity.projectile.AbsorbedSoulEntity;
import dev.xylonity.olympus.common.entity.HarpyEntity;
import dev.xylonity.olympus.common.entity.projectile.HarpyProjectileEntity;
import dev.xylonity.olympus.common.entity.projectile.PoseidonTridentEntity;
import dev.xylonity.olympus.common.entity.projectile.SpearOfAresEntity;
import dev.xylonity.olympus.common.entity.projectile.SummoningSpearsEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OlympusEntities {

    public static final DeferredRegister.Entities ENTITIES = DeferredRegister.createEntities(Olympus.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<AbsorbedSoulEntity>> ABSORBED_SOUL = ENTITIES.registerEntityType(
            "absorbed_soul", AbsorbedSoulEntity::new, MobCategory.MISC, builder -> builder.noLootTable().sized(0.1F, 0.1F).clientTrackingRange(6).updateInterval(2));

    public static final DeferredHolder<EntityType<?>, EntityType<PoseidonTridentEntity>> POSEIDON_TRIDENT = ENTITIES.registerEntityType(
            "poseidon_trident", PoseidonTridentEntity::new, MobCategory.MISC, builder -> builder.noLootTable().sized(0.5F, 0.5F).eyeHeight(0.13F).clientTrackingRange(4).updateInterval(20));

    public static final DeferredHolder<EntityType<?>, EntityType<SpearOfAresEntity>> SPEAR_OF_ARES = ENTITIES.registerEntityType(
            "spear_of_ares", SpearOfAresEntity::new, MobCategory.MISC, builder -> builder.noLootTable().sized(0.5F, 0.5F).eyeHeight(0.13F).clientTrackingRange(6).updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<SummoningSpearsEntity>> SUMMONING_SPEARS = ENTITIES.registerEntityType(
            "summoning_spears", SummoningSpearsEntity::new, MobCategory.MISC, builder -> builder.noLootTable().sized(9.0F, 5.0F).clientTrackingRange(10).updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<HarpyEntity>> HARPY = ENTITIES.registerEntityType(
            "harpy", HarpyEntity::new, MobCategory.MONSTER, builder -> builder.noLootTable().sized(0.9F, 1.8F).eyeHeight(1.55F).clientTrackingRange(8).updateInterval(2).notInPeaceful());

    public static final DeferredHolder<EntityType<?>, EntityType<HarpyProjectileEntity>> HARPY_PROJECTILE = ENTITIES.registerEntityType(
            "harpy_projectile", HarpyProjectileEntity::new, MobCategory.MISC, builder -> builder.noLootTable().noSave().sized(0.3F, 0.3F).clientTrackingRange(8).updateInterval(1));

}
