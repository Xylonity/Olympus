package dev.xylonity.olympus.registry;

import dev.xylonity.olympus.Olympus;
import dev.xylonity.knightlib.api.registrar.ResourceDispatcher;
import dev.xylonity.knightlib.api.registrar.ResourceEntry;
import dev.xylonity.knightlib.api.registrar.ResourceRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;

public final class OlympusSounds {

    public static final ResourceRegistry<SoundEvent> SOUND_EVENTS = ResourceDispatcher.create(BuiltInRegistries.SOUND_EVENT, Olympus.MOD_ID);

    public static final ResourceEntry<SoundEvent> ZEUS_BRACERS_LIGHTNING_STRIKE = SOUND_EVENTS.register("zeus_bracers_lightning_strike", () -> SoundEvent.createVariableRangeEvent(Olympus.of("zeus_bracers_lightning_strike")));
    public static final ResourceEntry<SoundEvent> POSEIDONS_TRIDENT_HIT = SOUND_EVENTS.register("poseidons_trident_hit", () -> SoundEvent.createVariableRangeEvent(Olympus.of("poseidons_trident_hit")));
    public static final ResourceEntry<SoundEvent> ARES_SPEAR_LANDING = SOUND_EVENTS.register("ares_spear_landing", () -> SoundEvent.createVariableRangeEvent(Olympus.of("ares_spear_landing")));
    public static final ResourceEntry<SoundEvent> ARES_SPEAR_NAILING = SOUND_EVENTS.register("ares_spear_nailing", () -> SoundEvent.createVariableRangeEvent(Olympus.of("ares_spear_nailing")));
    public static final ResourceEntry<SoundEvent> ARES_SPEAR_SURFACE_HIT = SOUND_EVENTS.register("ares_spear_surface_hit", () -> SoundEvent.createVariableRangeEvent(Olympus.of("ares_spear_surface_hit")));
    public static final ResourceEntry<SoundEvent> LYRE_MUSIC = SOUND_EVENTS.register("lyre_music", () -> SoundEvent.createVariableRangeEvent(Olympus.of("lyre_music")));
    public static final ResourceEntry<SoundEvent> HEPHAESTUS_FORGING = SOUND_EVENTS.register("hephaestus_forging", () -> SoundEvent.createVariableRangeEvent(Olympus.of("hephaestus_forging")));
    public static final ResourceEntry<SoundEvent> HARPY_DEATH = SOUND_EVENTS.register("harpy_death", () -> SoundEvent.createVariableRangeEvent(Olympus.of("harpy_death")));
    public static final ResourceEntry<SoundEvent> HARPY_DASH = SOUND_EVENTS.register("harpy_dash", () -> SoundEvent.createVariableRangeEvent(Olympus.of("harpy_dash")));
    public static final ResourceEntry<SoundEvent> HARPY_HIT = SOUND_EVENTS.register("harpy_hit", () -> SoundEvent.createVariableRangeEvent(Olympus.of("harpy_hit")));
    public static final ResourceEntry<SoundEvent> HARPY_SHOT = SOUND_EVENTS.register("harpy_shot", () -> SoundEvent.createVariableRangeEvent(Olympus.of("harpy_shot")));
    public static final ResourceEntry<SoundEvent> HARPYS_WINGS_FLAPPING = SOUND_EVENTS.register("harpys_wings_flapping", () -> SoundEvent.createVariableRangeEvent(Olympus.of("harpys_wings_flapping")));
    public static final ResourceEntry<SoundEvent> HERMES_JUMP = register("hermes_jump");
    public static final ResourceEntry<SoundEvent> PARTHENON_SPAWNER_AMBIENT = register("parthenon_spawner_ambient");
    public static final ResourceEntry<SoundEvent> PARTHENON_SPAWNER_DETECT_PLAYER = register("parthenon_spawner_detect_player");
    public static final ResourceEntry<SoundEvent> PARTHENON_SPAWNER_OPEN_SHUTTER = register("parthenon_spawner_open_shutter");
    public static final ResourceEntry<SoundEvent> PARTHENON_SPAWNER_SPAWN_MOB = register("parthenon_spawner_spawn_mob");
    public static final ResourceEntry<SoundEvent> PARTHENON_SPAWNER_SPAWN_ITEM_BEGIN = register("parthenon_spawner_spawn_item_begin");
    public static final ResourceEntry<SoundEvent> PARTHENON_SPAWNER_CLOSE_SHUTTER = register("parthenon_spawner_close_shutter");
    public static final ResourceEntry<SoundEvent> PARTHENON_SPAWNER_EJECT_ITEM = register("parthenon_spawner_eject_item");
    public static final ResourceEntry<SoundEvent> VAULT_OPEN_SHUTTER = register("vault_open_shutter");
    public static final ResourceEntry<SoundEvent> ARES_SPEAR_ATTACK = register("ares_spear_attack");
    public static final ResourceEntry<SoundEvent> ARES_SPEAR_HIT = register("ares_spear_hit");

    private static final SoundEvent PARTHENON_SPAWNER_BREAK_EVENT = soundEvent("parthenon_spawner_break");
    private static final SoundEvent PARTHENON_SPAWNER_STEP_EVENT = soundEvent("parthenon_spawner_step");
    private static final SoundEvent PARTHENON_SPAWNER_PLACE_EVENT = soundEvent("parthenon_spawner_place");
    private static final SoundEvent PARTHENON_SPAWNER_HIT_EVENT = soundEvent("parthenon_spawner_hit");
    private static final SoundEvent PARTHENON_SPAWNER_FALL_EVENT = soundEvent("parthenon_spawner_fall");

    public static final ResourceEntry<SoundEvent> PARTHENON_SPAWNER_BREAK = register("parthenon_spawner_break", PARTHENON_SPAWNER_BREAK_EVENT);
    public static final ResourceEntry<SoundEvent> PARTHENON_SPAWNER_STEP = register("parthenon_spawner_step", PARTHENON_SPAWNER_STEP_EVENT);
    public static final ResourceEntry<SoundEvent> PARTHENON_SPAWNER_PLACE = register("parthenon_spawner_place", PARTHENON_SPAWNER_PLACE_EVENT);
    public static final ResourceEntry<SoundEvent> PARTHENON_SPAWNER_HIT = register("parthenon_spawner_hit", PARTHENON_SPAWNER_HIT_EVENT);
    public static final ResourceEntry<SoundEvent> PARTHENON_SPAWNER_FALL = register("parthenon_spawner_fall", PARTHENON_SPAWNER_FALL_EVENT);

    public static final SoundType PARTHENON_SPAWNER_SOUND_TYPE = new SoundType(
            1, 1,
            PARTHENON_SPAWNER_BREAK_EVENT,
            PARTHENON_SPAWNER_STEP_EVENT,
            PARTHENON_SPAWNER_PLACE_EVENT,
            PARTHENON_SPAWNER_HIT_EVENT,
            PARTHENON_SPAWNER_FALL_EVENT
    );

    private static ResourceEntry<SoundEvent> register(final String name) {
        return register(name, soundEvent(name));
    }

    private static ResourceEntry<SoundEvent> register(final String name, final SoundEvent soundEvent) {
        return SOUND_EVENTS.register(name, () -> soundEvent);
    }

    private static SoundEvent soundEvent(final String name) {
        return SoundEvent.createVariableRangeEvent(Olympus.of(name));
    }

}
