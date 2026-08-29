package dev.xylonity.olympus.registry;

import dev.xylonity.olympus.Olympus;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OlympusSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, Olympus.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> ZEUS_BRACERS_LIGHTNING_STRIKE = SOUND_EVENTS.register("zeus_bracers_lightning_strike", () -> SoundEvent.createVariableRangeEvent(Olympus.of("zeus_bracers_lightning_strike")));
    public static final DeferredHolder<SoundEvent, SoundEvent> POSEIDONS_TRIDENT_HIT = SOUND_EVENTS.register("poseidons_trident_hit", () -> SoundEvent.createVariableRangeEvent(Olympus.of("poseidons_trident_hit")));
    public static final DeferredHolder<SoundEvent, SoundEvent> ARES_SPEAR_LANDING = SOUND_EVENTS.register("ares_spear_landing", () -> SoundEvent.createVariableRangeEvent(Olympus.of("ares_spear_landing")));
    public static final DeferredHolder<SoundEvent, SoundEvent> ARES_SPEAR_NAILING = SOUND_EVENTS.register("ares_spear_nailing", () -> SoundEvent.createVariableRangeEvent(Olympus.of("ares_spear_nailing")));
    public static final DeferredHolder<SoundEvent, SoundEvent> ARES_SPEAR_SURFACE_HIT = SOUND_EVENTS.register("ares_spear_surface_hit", () -> SoundEvent.createVariableRangeEvent(Olympus.of("ares_spear_surface_hit")));
    public static final DeferredHolder<SoundEvent, SoundEvent> LYRE_MUSIC = SOUND_EVENTS.register("lyre_music", () -> SoundEvent.createVariableRangeEvent(Olympus.of("lyre_music")));
    public static final DeferredHolder<SoundEvent, SoundEvent> HEPHAESTUS_FORGING = SOUND_EVENTS.register("hephaestus_forging", () -> SoundEvent.createVariableRangeEvent(Olympus.of("hephaestus_forging")));
    public static final DeferredHolder<SoundEvent, SoundEvent> HARPY_DEATH = SOUND_EVENTS.register("harpy_death", () -> SoundEvent.createVariableRangeEvent(Olympus.of("harpy_death")));
    public static final DeferredHolder<SoundEvent, SoundEvent> HARPY_DASH = SOUND_EVENTS.register("harpy_dash", () -> SoundEvent.createVariableRangeEvent(Olympus.of("harpy_dash")));
    public static final DeferredHolder<SoundEvent, SoundEvent> HARPY_HIT = SOUND_EVENTS.register("harpy_hit", () -> SoundEvent.createVariableRangeEvent(Olympus.of("harpy_hit")));
    public static final DeferredHolder<SoundEvent, SoundEvent> HARPY_SHOT = SOUND_EVENTS.register("harpy_shot", () -> SoundEvent.createVariableRangeEvent(Olympus.of("harpy_shot")));
    public static final DeferredHolder<SoundEvent, SoundEvent> HARPYS_WINGS_FLAPPING = SOUND_EVENTS.register("harpys_wings_flapping", () -> SoundEvent.createVariableRangeEvent(Olympus.of("harpys_wings_flapping")));

}
