package dev.xylonity.olympus.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public final class OlympusConfig {

    public static final OlympusConfig INSTANCE;
    public static final ModConfigSpec SPEC;

    public final ModConfigSpec.DoubleValue aphroditeLyreCooldownSeconds;
    public final ModConfigSpec.DoubleValue aphroditeLyreBreedingRadius;

    public final ModConfigSpec.DoubleValue artemisBowProjectileSpeedMultiplier;
    public final ModConfigSpec.IntValue artemisBowLootingBonus;
    public final ModConfigSpec.DoubleValue artemisBowTamedHealingMultiplier;

    public final ModConfigSpec.DoubleValue zeusBracersDamage;
    public final ModConfigSpec.DoubleValue zeusBracersCooldownSeconds;
    public final ModConfigSpec.IntValue zeusBracersChainJumps;
    public final ModConfigSpec.DoubleValue zeusBracersChainRange;
    public final ModConfigSpec.DoubleValue zeusBracersChainDamageMultiplier;
    public final ModConfigSpec.DoubleValue zeusBracersMinimumStunSeconds;
    public final ModConfigSpec.DoubleValue zeusBracersMaximumStunSeconds;

    public final ModConfigSpec.DoubleValue persephoneCupRegenerationSeconds;
    public final ModConfigSpec.BooleanValue persephoneCupDamageAppliesToWeapons;
    public final ModConfigSpec.DoubleValue persephoneCupDamagePerSoul;
    public final ModConfigSpec.DoubleValue persephoneCupRestoredHealthPercentage;
    public final ModConfigSpec.IntValue persephoneCupDeathProtectionChargeCost;

    public final ModConfigSpec.DoubleValue helmetOfHadesCooldownSeconds;
    public final ModConfigSpec.DoubleValue helmetOfHadesArmor;
    public final ModConfigSpec.DoubleValue helmetOfHadesRestoredHealthPercentage;
    public final ModConfigSpec.DoubleValue helmetOfHadesInvisibilitySeconds;

    public final ModConfigSpec.DoubleValue hermesSandalsArmor;
    public final ModConfigSpec.DoubleValue hermesSandalsMovementSpeedBonus;

    public final ModConfigSpec.IntValue hephaestusInstrumentsRepairAmount;
    public final ModConfigSpec.DoubleValue hephaestusInstrumentsRepairCooldownSeconds;
    public final ModConfigSpec.DoubleValue hephaestusInstrumentsKillCooldownReductionSeconds;

    public final ModConfigSpec.DoubleValue poseidonTridentAttackDamageBonus;
    public final ModConfigSpec.DoubleValue poseidonTridentSwimSpeedBonus;
    public final ModConfigSpec.DoubleValue poseidonTridentProjectileDamage;
    public final ModConfigSpec.DoubleValue poseidonTridentSplashDamage;
    public final ModConfigSpec.DoubleValue poseidonTridentSplashRadius;

    public final ModConfigSpec.DoubleValue aresSpearAttackDamageBonus;
    public final ModConfigSpec.DoubleValue aresSpearProjectileDamage;
    public final ModConfigSpec.DoubleValue aresSpearWallImpactDamage;
    public final ModConfigSpec.DoubleValue aresSpearPinnedEntityDistance;
    public final ModConfigSpec.DoubleValue aresSpearWallSlownessSeconds;
    public final ModConfigSpec.DoubleValue aresSpearThrowCooldownSeconds;
    public final ModConfigSpec.DoubleValue aresSpearAbilityCooldownSeconds;
    public final ModConfigSpec.DoubleValue aresSpearAbilityMinimumFallDistance;
    public final ModConfigSpec.DoubleValue aresSpearAbilityDamage;
    public final ModConfigSpec.DoubleValue aresSpearAbilityRadius;

    public final ModConfigSpec.IntValue demeterPoppyRadius;
    public final ModConfigSpec.DoubleValue demeterPoppyGrowthIntervalSeconds;
    public final ModConfigSpec.IntValue demeterPoppyGrowthsPerTarget;

    private OlympusConfig(final ModConfigSpec.Builder builder) {
        builder.push("aphroditeLyre");
        aphroditeLyreCooldownSeconds = builder
                .comment("Cooldown (in seconds) after playing Aphrodite's Lyre")
                .defineInRange("cooldownSeconds", 5.0D, 0.0D, 3600.0D);
        aphroditeLyreBreedingRadius = builder
                .comment("Radius in which Aphrodite's Lyre puts animals in breeding (love mode)")
                .defineInRange("breedingRadius", 8.0D, 0.0D, 64.0D);
        builder.pop();

        builder.push("bowOfArtemis");
        artemisBowProjectileSpeedMultiplier = builder
                .comment("Arrow speed multiplier for the Bow of Artemis")
                .defineInRange("projectileSpeedMultiplier", 1.2D, 0.1D, 10.0D);
        artemisBowLootingBonus = builder
                .comment("Extra Looting levels applied to kills made with the Bow of Artemis")
                .defineInRange("lootingBonus", 1, 0, 100);
        artemisBowTamedHealingMultiplier = builder
                .comment("Fraction of the arrow's damage restored as health when hitting one of the shooter's tamed animals (0.35 = 35%)")
                .defineInRange("tamedHealingMultiplier", 0.5D, 0.0D, 100.0D);
        builder.pop();

        builder.push("bracersOfZeus");
        zeusBracersDamage = builder
                .comment("Additional lightning damage dealt by the Bracers of Zeus")
                .defineInRange("additionalLightningDamage", 8.0D, 0.0D, 1000.0D);
        zeusBracersCooldownSeconds = builder
                .comment("Cooldown (in seconds) of the Bracers of Zeus")
                .defineInRange("cooldownSeconds", 10, 0.0D, 3600.0D);
        zeusBracersChainJumps = builder
                .comment("Maximum number of additional targets hit by chained lightning")
                .defineInRange("chainJumps", 3, 0, 100);
        zeusBracersChainRange = builder
                .comment("Search range around the current chained lightning target")
                .defineInRange("chainRange", 8.0D, 0.0D, 128.0D);
        zeusBracersChainDamageMultiplier = builder
                .comment("Damage multiplier applied to chained lightning after the first target")
                .defineInRange("chainDamageMultiplier", 0.5D, 0.0D, 100.0D);
        zeusBracersMinimumStunSeconds = builder
                .comment("Minimum duration (in seconds) of the lightning stun")
                .defineInRange("minimumStunSeconds", 1.5D, 0.0D, 60.0D);
        zeusBracersMaximumStunSeconds = builder
                .comment("Maximum duration (in seconds) of the lightning stun")
                .defineInRange("maximumStunSeconds", 3.5D, 0.0D, 60.0D);
        builder.pop();

        builder.push("persephoneCup");
        persephoneCupRegenerationSeconds = builder
                .comment("Duration (in seconds) of the Regeneration II effect granted by Persephone's Cup")
                .defineInRange("regenerationSeconds", 20.0D, 0.0D, 3600.0D);
        persephoneCupDamageAppliesToWeapons = builder
                .comment("Whether Persephone's Cup damage bonus applies to every player weapon attack, including melee and ranged attacks")
                .define("damageAppliesToWeapons", false);
        persephoneCupDamagePerSoul = builder
                .comment("Additional attack damage granted per stored soul")
                .defineInRange("damagePerSoul", 0.2D, 0.0D, 100.0D);
        persephoneCupRestoredHealthPercentage = builder
                .comment("Percentage of maximum health restored by the Cup's death protection (0.3 = 30%)")
                .defineInRange("restoredHealthPercentage", 0.3D, 0.01D, 1.0D);
        persephoneCupDeathProtectionChargeCost = builder
                .comment("Soul charges consumed when the Cup prevents death")
                .defineInRange("deathProtectionChargeCost", 20, 0, 40);
        builder.pop();

        builder.push("helmetOfHades");
        helmetOfHadesCooldownSeconds = builder
                .comment("Cooldown (in seconds) of the Helmet of Hades death protection")
                .defineInRange("cooldownSeconds", 80.0D, 0.0D, 3600.0D);
        helmetOfHadesArmor = builder
                .comment("Armor points granted by the Helmet of Hades")
                .defineInRange("armor", 2.0D, 0.0D, 100.0D);
        helmetOfHadesRestoredHealthPercentage = builder
                .comment("Percentage of maximum health restored by the Helmet's death protection (0.5 = 50%)")
                .defineInRange("restoredHealthPercentage", 0.5D, 0.01D, 1.0D);
        helmetOfHadesInvisibilitySeconds = builder
                .comment("Duration (in seconds) of the Helmet's invisibility after preventing death")
                .defineInRange("invisibilitySeconds", 30.0D, 0.0D, 3600.0D);
        builder.pop();

        builder.push("hermesSandals");
        hermesSandalsArmor = builder
                .comment("Armor points granted by Hermes' Sandals")
                .defineInRange("armor", 1.0D, 0.0D, 100.0D);
        hermesSandalsMovementSpeedBonus = builder
                .comment("Movement speed bonus granted by Hermes' Sandals (0.1 = 10% of base speed)")
                .defineInRange("movementSpeedBonus", 0.1D, 0.0D, 10.0D);
        // Doesn't work with this stack config
        // hermesSandalsExtraJumps = builder
        //         .comment("Number of extra mid-air jumps granted by Hermes' Sandals")
        //         .defineInRange("extraJumps", 3, 0, 100);
        builder.pop();

        builder.push("instrumentsOfHephaestus");
        hephaestusInstrumentsRepairAmount = builder
                .comment("Durability restored each time the Instruments repair an item")
                .defineInRange("repairAmount", 30, 1, 100000);
        hephaestusInstrumentsRepairCooldownSeconds = builder
                .comment("Cooldown (in seconds) between automatic repairs")
                .defineInRange("repairCooldownSeconds", 50.0D, 0.0D, 36000.0D);
        hephaestusInstrumentsKillCooldownReductionSeconds = builder
                .comment("Repair cooldown reduction (in seconds) after a kill")
                .defineInRange("killCooldownReductionSeconds", 5.0D, 0.0D, 36000.0D);
        builder.pop();

        builder.push("poseidonTrident");
        poseidonTridentAttackDamageBonus = builder
                .comment("Melee attack damage added by Poseidon's Trident")
                .defineInRange("attackDamageBonus", 9.0D, 0.0D, 1000.0D);
        poseidonTridentSwimSpeedBonus = builder
                .comment("Swim speed multiplier bonus while holding Poseidon's Trident (0.4 = 40%)")
                .defineInRange("swimSpeedBonus", 0.4D, 0.0D, 10.0D);
        poseidonTridentProjectileDamage = builder
                .comment("Damage dealt to the entity directly hit by a thrown Poseidon's Trident")
                .defineInRange("projectileDamage", 10.0D, 0.0D, 1000.0D);
        poseidonTridentSplashDamage = builder
                .comment("Damage dealt by the thrown trident's splash")
                .defineInRange("splashDamage", 10.0D, 0.0D, 1000.0D);
        poseidonTridentSplashRadius = builder
                .comment("Radius of the thrown trident's damaging splash")
                .defineInRange("splashRadius", 3.5D, 0.1D, 128.0D);
        builder.pop();

        builder.push("spearOfAres");
        aresSpearAttackDamageBonus = builder
                .comment("Melee attack damage added by the Spear of Ares")
                .defineInRange("attackDamageBonus", 8.0D, 0.0D, 1000.0D);
        aresSpearProjectileDamage = builder
                .comment("Damage dealt when the thrown Spear of Ares hits an entity")
                .defineInRange("projectileDamage", 8.0D, 0.0D, 1000.0D);
        aresSpearWallImpactDamage = builder
                .comment("Damage dealt to pinned entities when the Spear of Ares hits a wall")
                .defineInRange("wallImpactDamage", 6.0D, 0.0D, 1000.0D);
        aresSpearPinnedEntityDistance = builder
                .comment("Maximum distance entities can be carried by a thrown Spear of Ares")
                .defineInRange("pinnedEntityDistance", 10.0D, 0.0D, 128.0D);
        aresSpearWallSlownessSeconds = builder
                .comment("Duration (in seconds) of Slowness after a pinned entity hits a wall")
                .defineInRange("wallSlownessSeconds", 3.0D, 0.0D, 3600.0D);
        aresSpearThrowCooldownSeconds = builder
                .comment("Cooldown (in seconds) after throwing the Spear of Ares")
                .defineInRange("throwCooldownSeconds", 2.5D, 0.0D, 3600.0D);
        aresSpearAbilityCooldownSeconds = builder
                .comment("Cooldown (in seconds) of the Spear of Ares ground ability")
                .defineInRange("abilityCooldownSeconds", 6.0D, 0.0D, 3600.0D);
        aresSpearAbilityMinimumFallDistance = builder
                .comment("Minimum fall distance required to activate the Spear of Ares ground ability")
                .defineInRange("abilityMinimumFallDistance", 3.0D, 0.0D, 256.0D);
        aresSpearAbilityDamage = builder
                .comment("Damage dealt by the summoned spears from the ground ability")
                .defineInRange("abilityDamage", 15.0D, 0.0D, 1000.0D);
        aresSpearAbilityRadius = builder
                .comment("Radius of the Spear of Ares ground ability")
                .defineInRange("abilityRadius", 3.0D, 0.1D, 128.0D);
        builder.pop();

        builder.push("poppyOfDemeter");
        demeterPoppyRadius = builder
                .comment("Radius in which the Poppy of Demeter searches for growable blocks")
                .defineInRange("growthRadius", 5, 1, 32);
        demeterPoppyGrowthIntervalSeconds = builder
                .comment("Time (in seconds) between growth accelerations")
                .defineInRange("growthIntervalSeconds", 2.0D, 0.2D, 3600.0D);
        demeterPoppyGrowthsPerTarget = builder
                .comment("Number of growth accelerations before the Poppy chooses a different block")
                .defineInRange("growthsPerTarget", 3, 1, 1000);
        builder.pop();
    }

    public static int secondsToTicks(final double seconds) {
        return Math.max(0, (int) Math.round(seconds * 20.0D));
    }

    static {
        final Pair<OlympusConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(OlympusConfig::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }

}
