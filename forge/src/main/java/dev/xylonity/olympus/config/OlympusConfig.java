package dev.xylonity.olympus.config;

import dev.xylonity.knightlib.api.config.AutoConfig;
import dev.xylonity.knightlib.api.config.ConfigEntry;

@AutoConfig(
        file = "olympus",
        title = "Olympus Common Config",
        accentColor = 0xFFD5B45C
)
public final class OlympusConfig {

    @ConfigEntry(
            category = "Aphrodite's Lyre",
            comment = "Cooldown (in seconds) after playing Aphrodite's Lyre",
            min = 0.0D,
            max = 3600.0D
    )
    public static double APHRODITE_LYRE_COOLDOWN_SECONDS = 5.0D;

    @ConfigEntry(
            category = "Aphrodite's Lyre",
            comment = "Radius in which Aphrodite's Lyre puts animals in breeding (love mode)",
            min = 0.0D,
            max = 64.0D
    )
    public static double APHRODITE_LYRE_BREEDING_RADIUS = 8.0D;

    @ConfigEntry(
            category = "Artemis's Bow",
            comment = "Arrow speed multiplier for the Bow of Artemis",
            min = 0.1D,
            max = 10.0D
    )
    public static double ARTEMIS_BOW_PROJECTILE_SPEED_MULTIPLIER = 1.2D;

    @ConfigEntry(
            category = "Artemis's Bow",
            comment = "Extra Looting levels applied to kills made with the Bow of Artemis",
            min = 0,
            max = 100
    )
    public static int ARTEMIS_BOW_LOOTING_BONUS = 1;

    @ConfigEntry(
            category = "Artemis's Bow",
            comment = "Fraction of the arrow's damage restored as health when hitting one of the shooter's tamed animals (0.35 = 35%)",
            min = 0.0D,
            max = 100.0D
    )
    public static double ARTEMIS_BOW_TAMED_HEALING_MULTIPLIER = 0.5D;

    @ConfigEntry(
            category = "Bracers of Zeus",
            comment = "Additional lightning damage dealt by the Bracers of Zeus",
            min = 0.0D,
            max = 1000.0D
    )
    public static double ZEUS_BRACERS_DAMAGE = 8.0D;

    @ConfigEntry(
            category = "Bracers of Zeus",
            comment = "Cooldown (in seconds) of the Bracers of Zeus",
            min = 0.0D,
            max = 3600.0D
    )
    public static double ZEUS_BRACERS_COOLDOWN_SECONDS = 10.0D;

    @ConfigEntry(
            category = "Bracers of Zeus",
            comment = "Maximum number of additional targets hit by chained lightning",
            min = 0,
            max = 100
    )
    public static int ZEUS_BRACERS_CHAIN_JUMPS = 3;

    @ConfigEntry(
            category = "Bracers of Zeus",
            comment = "Search range around the current chained lightning target",
            min = 0.0D,
            max = 128.0D
    )
    public static double ZEUS_BRACERS_CHAIN_RANGE = 8.0D;

    @ConfigEntry(
            category = "Bracers of Zeus",
            comment = "Damage multiplier applied to chained lightning after the first target",
            min = 0.0D,
            max = 100.0D
    )
    public static double ZEUS_BRACERS_CHAIN_DAMAGE_MULTIPLIER = 0.5D;

    @ConfigEntry(
            category = "Bracers of Zeus",
            comment = "Minimum duration (in seconds) of the lightning stun",
            min = 0.0D,
            max = 60.0D
    )
    public static double ZEUS_BRACERS_MINIMUM_STUN_SECONDS = 1.5D;

    @ConfigEntry(
            category = "Bracers of Zeus",
            comment = "Maximum duration (in seconds) of the lightning stun",
            min = 0.0D,
            max = 60.0D
    )
    public static double ZEUS_BRACERS_MAXIMUM_STUN_SECONDS = 3.5D;

    @ConfigEntry(
            category = "Persephone's Cup",
            comment = "Duration (in seconds) of the Regeneration II effect granted by Persephone's Cup",
            min = 0.0D,
            max = 3600.0D
    )
    public static double PERSEPHONE_CUP_REGENERATION_SECONDS = 20.0D;

    @ConfigEntry(
            category = "Persephone's Cup",
            comment = "Whether Persephone's Cup damage bonus applies to every player weapon attack, including melee and ranged attacks"
    )
    public static boolean PERSEPHONE_CUP_DAMAGE_APPLIES_TO_WEAPONS = false;

    @ConfigEntry(
            category = "Persephone's Cup",
            comment = "Additional attack damage granted per stored soul",
            min = 0.0D,
            max = 100.0D
    )
    public static double PERSEPHONE_CUP_DAMAGE_PER_SOUL = 0.2D;

    @ConfigEntry(
            category = "Persephone's Cup",
            comment = "Percentage of maximum health restored by the Cup's death protection (0.3 = 30%)",
            min = 0.01D,
            max = 1.0D
    )
    public static double PERSEPHONE_CUP_RESTORED_HEALTH_PERCENTAGE = 0.3D;

    @ConfigEntry(
            category = "Persephone's Cup",
            comment = "Soul charges consumed when the Cup prevents death",
            min = 0,
            max = 40
    )
    public static int PERSEPHONE_CUP_DEATH_PROTECTION_CHARGE_COST = 20;

    @ConfigEntry(
            category = "Helmet of Hades",
            comment = "Cooldown (in seconds) of the Helmet of Hades death protection",
            min = 0.0D,
            max = 3600.0D
    )
    public static double HELMET_OF_HADES_COOLDOWN_SECONDS = 80.0D;

    @ConfigEntry(
            category = "Helmet of Hades",
            comment = "Armor points granted by the Helmet of Hades",
            min = 0.0D,
            max = 100.0D
    )
    public static double HELMET_OF_HADES_ARMOR = 2.0D;

    @ConfigEntry(
            category = "Helmet of Hades",
            comment = "Percentage of maximum health restored by the Helmet's death protection (0.5 = 50%)",
            min = 0.01D,
            max = 1.0D
    )
    public static double HELMET_OF_HADES_RESTORED_HEALTH_PERCENTAGE = 0.5D;

    @ConfigEntry(
            category = "Helmet of Hades",
            comment = "Duration (in seconds) of the Helmet's invisibility after preventing death",
            min = 0.0D,
            max = 3600.0D
    )
    public static double HELMET_OF_HADES_INVISIBILITY_SECONDS = 30.0D;

    @ConfigEntry(
            category = "Hermes' Sandals",
            comment = "Armor points granted by Hermes' Sandals",
            min = 0.0D,
            max = 100.0D
    )
    public static double HERMES_SANDALS_ARMOR = 1.0D;

    @ConfigEntry(
            category = "Hermes' Sandals",
            comment = "Movement speed bonus granted by Hermes' Sandals (0.1 = 10% of base speed)",
            min = 0.0D,
            max = 10.0D
    )
    public static double HERMES_SANDALS_MOVEMENT_SPEED_BONUS = 0.1D;

    @ConfigEntry(
            category = "Hermes' Sandals",
            comment = "Number of extra mid-air jumps granted by Hermes' Sandals",
            min = 0,
            max = 2000
    )
    public static int HERMES_SANDALS_JUMP_AMOUNT = 3;

    @ConfigEntry(
            category = "Instruments of Hephaestus",
            comment = "Durability restored each time the Instruments repair an item",
            min = 1,
            max = 100000
    )
    public static int HEPHAESTUS_INSTRUMENTS_REPAIR_AMOUNT = 30;

    @ConfigEntry(
            category = "Instruments of Hephaestus",
            comment = "Cooldown (in seconds) between automatic repairs",
            min = 0.0D,
            max = 36000.0D
    )
    public static double HEPHAESTUS_INSTRUMENTS_REPAIR_COOLDOWN_SECONDS = 50.0D;

    @ConfigEntry(
            category = "Instruments of Hephaestus",
            comment = "Repair cooldown reduction (in seconds) after a kill",
            min = 0.0D,
            max = 36000.0D
    )
    public static double HEPHAESTUS_INSTRUMENTS_KILL_COOLDOWN_REDUCTION_SECONDS = 5.0D;

    @ConfigEntry(
            category = "Poseidon's Trident",
            comment = "Melee attack damage added by Poseidon's Trident",
            min = 0.0D,
            max = 1000.0D
    )
    public static double POSEIDON_TRIDENT_ATTACK_DAMAGE_BONUS = 9.0D;

    @ConfigEntry(
            category = "Poseidon's Trident",
            comment = "Swim speed multiplier bonus while holding Poseidon's Trident (0.4 = 40%)",
            min = 0.0D,
            max = 10.0D
    )
    public static double POSEIDON_TRIDENT_SWIM_SPEED_BONUS = 0.4D;

    @ConfigEntry(
            category = "Poseidon's Trident",
            comment = "Damage dealt to the entity directly hit by a thrown Poseidon's Trident",
            min = 0.0D,
            max = 1000.0D
    )
    public static double POSEIDON_TRIDENT_PROJECTILE_DAMAGE = 10.0D;

    @ConfigEntry(
            category = "Poseidon's Trident",
            comment = "Damage dealt by the thrown trident's splash",
            min = 0.0D,
            max = 1000.0D
    )
    public static double POSEIDON_TRIDENT_SPLASH_DAMAGE = 10.0D;

    @ConfigEntry(
            category = "Poseidon's Trident",
            comment = "Radius of the thrown trident's damaging splash",
            min = 0.1D,
            max = 128.0D
    )
    public static double POSEIDON_TRIDENT_SPLASH_RADIUS = 3.5D;

    @ConfigEntry(
            category = "Spear of Ares",
            comment = "Melee attack damage added by the Spear of Ares",
            min = 0.0D,
            max = 1000.0D
    )
    public static double ARES_SPEAR_ATTACK_DAMAGE_BONUS = 8.0D;

    @ConfigEntry(
            category = "Spear of Ares",
            comment = "Damage dealt when the thrown Spear of Ares hits an entity",
            min = 0.0D,
            max = 1000.0D
    )
    public static double ARES_SPEAR_PROJECTILE_DAMAGE = 8.0D;

    @ConfigEntry(
            category = "Spear of Ares",
            comment = "Damage dealt to pinned entities when the Spear of Ares hits a wall",
            min = 0.0D,
            max = 1000.0D
    )
    public static double ARES_SPEAR_WALL_IMPACT_DAMAGE = 6.0D;

    @ConfigEntry(
            category = "Spear of Ares",
            comment = "Maximum distance entities can be carried by a thrown Spear of Ares",
            min = 0.0D,
            max = 128.0D
    )
    public static double ARES_SPEAR_PINNED_ENTITY_DISTANCE = 10.0D;

    @ConfigEntry(
            category = "Spear of Ares",
            comment = "Duration (in seconds) of Slowness after a pinned entity hits a wall",
            min = 0.0D,
            max = 3600.0D
    )
    public static double ARES_SPEAR_WALL_SLOWNESS_SECONDS = 3.0D;

    @ConfigEntry(
            category = "Spear of Ares",
            comment = "Cooldown (in seconds) after throwing the Spear of Ares",
            min = 0.0D,
            max = 3600.0D
    )
    public static double ARES_SPEAR_THROW_COOLDOWN_SECONDS = 2.5D;

    @ConfigEntry(
            category = "Spear of Ares",
            comment = "Cooldown (in seconds) of the Spear of Ares ground ability",
            min = 0.0D,
            max = 3600.0D
    )
    public static double ARES_SPEAR_ABILITY_COOLDOWN_SECONDS = 6.0D;

    @ConfigEntry(
            category = "Spear of Ares",
            comment = "Minimum fall distance required to activate the Spear of Ares ground ability",
            min = 0.0D,
            max = 256.0D
    )
    public static double ARES_SPEAR_ABILITY_MINIMUM_FALL_DISTANCE = 3.0D;

    @ConfigEntry(
            category = "Spear of Ares",
            comment = "Damage dealt by the summoned spears from the ground ability",
            min = 0.0D,
            max = 1000.0D
    )
    public static double ARES_SPEAR_ABILITY_DAMAGE = 15.0D;

    @ConfigEntry(
            category = "Spear of Ares",
            comment = "Radius of the Spear of Ares ground ability",
            min = 0.1D,
            max = 128.0D
    )
    public static double ARES_SPEAR_ABILITY_RADIUS = 3.0D;

    @ConfigEntry(
            category = "Poppy of Demeter",
            comment = "Radius in which the Poppy of Demeter searches for growable blocks",
            min = 1,
            max = 32
    )
    public static int DEMETER_POPPY_RADIUS = 5;

    @ConfigEntry(
            category = "Poppy of Demeter",
            comment = "Time (in seconds) between growth accelerations",
            min = 0.2D,
            max = 3600.0D
    )
    public static double DEMETER_POPPY_GROWTH_INTERVAL_SECONDS = 2.0D;

    @ConfigEntry(
            category = "Poppy of Demeter",
            comment = "Number of growth accelerations before the Poppy chooses a different block",
            min = 1,
            max = 1000
    )
    public static int DEMETER_POPPY_GROWTHS_PER_TARGET = 3;

    public static int secondsToTicks(final double seconds) {
        return Math.max(0, (int) Math.round(seconds * 20.0D));
    }

}
