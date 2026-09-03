package dev.xylonity.olympus.registry;

import dev.xylonity.knightlib.KnightLib;
import dev.xylonity.olympus.Olympus;
import dev.xylonity.knightlib.api.registrar.ResourceDispatcher;
import dev.xylonity.knightlib.api.registrar.ResourceEntry;
import dev.xylonity.knightlib.api.registrar.ResourceRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;

public final class OlympusParticles {

    public static final ResourceRegistry<ParticleType<?>> PARTICLES = ResourceDispatcher.create(BuiltInRegistries.PARTICLE_TYPE, Olympus.MOD_ID);

    public static final ResourceEntry<SimpleParticleType> SOUL_SALVATION = PARTICLES.register("soul_salvation", KnightLib.PLATFORM.createParticle(true));
    public static final ResourceEntry<SimpleParticleType> POPPY_GROWTH = PARTICLES.register("poppy_growth", KnightLib.PLATFORM.createParticle(false));
    public static final ResourceEntry<SimpleParticleType> LIGHTNING_SPARKS = PARTICLES.register("lightning_sparks", KnightLib.PLATFORM.createParticle(false));
    public static final ResourceEntry<SimpleParticleType> HARPY_MAGIC = PARTICLES.register("harpy_magic", KnightLib.PLATFORM.createParticle(false));
    public static final ResourceEntry<SimpleParticleType> HARPY_FEATHER = PARTICLES.register("harpy_feather", KnightLib.PLATFORM.createParticle(false));
    public static final ResourceEntry<SimpleParticleType> ELITE_HARPY_FEATHER = PARTICLES.register("elite_harpy_feather", KnightLib.PLATFORM.createParticle(false));
    public static final ResourceEntry<SimpleParticleType> TRIDENT_SPLASH_OF_WATER = PARTICLES.register("trident_splash_of_water", KnightLib.PLATFORM.createParticle(false));
    public static final ResourceEntry<SimpleParticleType> TRIDENT_WATER_DROP = PARTICLES.register("trident_water_drop", KnightLib.PLATFORM.createParticle(false));
    public static final ResourceEntry<SimpleParticleType> TRIDENT_SMALL_SPLASH_OF_WATER = PARTICLES.register("trident_small_splash_of_water", KnightLib.PLATFORM.createParticle(false));
    public static final ResourceEntry<SimpleParticleType> TRIDENT_UNDERWATER_SPLASH = PARTICLES.register("trident_underwater_splash", KnightLib.PLATFORM.createParticle(false));
    public static final ResourceEntry<SimpleParticleType> ARES_SPEAR_TRACE = PARTICLES.register("ares_spear_trace", KnightLib.PLATFORM.createParticle(false));
    public static final ResourceEntry<SimpleParticleType> ARES_SPEAR_HIT = PARTICLES.register("ares_spear_hit", KnightLib.PLATFORM.createParticle(false));
    public static final ResourceEntry<SimpleParticleType> LYRE_NOTE = PARTICLES.register("lyre_note", KnightLib.PLATFORM.createParticle(false));
    public static final ResourceEntry<SimpleParticleType> FORGING_SPARK = PARTICLES.register("forging_spark", KnightLib.PLATFORM.createParticle(false));
    public static final ResourceEntry<SimpleParticleType> ARTEMIS_ARROW_TRACE = PARTICLES.register("artemis_arrow_trace", KnightLib.PLATFORM.createParticle(false));
    public static final ResourceEntry<SimpleParticleType> ARTEMIS_ARROW_TRACE_SMALL = PARTICLES.register("artemis_arrow_trace_small", KnightLib.PLATFORM.createParticle(false));

}
