package dev.xylonity.olympus.registry;

import dev.xylonity.olympus.Olympus;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OlympusParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, Olympus.MOD_ID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SOUL_SALVATION = PARTICLES.register("soul_salvation", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> POPPY_GROWTH = PARTICLES.register("poppy_growth", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LIGHTNING_SPARKS = PARTICLES.register("lightning_sparks", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> TRIDENT_SPLASH_OF_WATER = PARTICLES.register("trident_splash_of_water", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> TRIDENT_WATER_DROP = PARTICLES.register("trident_water_drop", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> TRIDENT_SMALL_SPLASH_OF_WATER = PARTICLES.register("trident_small_splash_of_water", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> TRIDENT_UNDERWATER_SPLASH = PARTICLES.register("trident_underwater_splash", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ARES_SPEAR_TRACE = PARTICLES.register("ares_spear_trace", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ARES_SPEAR_HIT = PARTICLES.register("ares_spear_hit", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LYRE_NOTE = PARTICLES.register("lyre_note", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FORGING_SPARK = PARTICLES.register("forging_spark", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ARTEMIS_ARROW_TRACE = PARTICLES.register("artemis_arrow_trace", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ARTEMIS_ARROW_TRACE_SMALL = PARTICLES.register("artemis_arrow_trace_small", () -> new SimpleParticleType(false));

}
