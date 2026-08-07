package dev.xylonity.olympus.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public final class OlympusConfig {

    public static final OlympusConfig INSTANCE;
    public static final ModConfigSpec SPEC;

    public final ModConfigSpec.DoubleValue zeusBracersDamage;
    public final ModConfigSpec.DoubleValue zeusBracersCooldownSeconds;

    private OlympusConfig(final ModConfigSpec.Builder builder) {
        builder.push("bracersOfZeus");
        zeusBracersDamage = builder
                .comment("Additional lightning damage dealt by the Bracers of Zeus")
                .defineInRange("additionalLightningDamage", 8.0D, 0.0D, 1000.0D);
        zeusBracersCooldownSeconds = builder
                .comment("Cooldown (in seconds) of the Bracers of Zeus")
                .defineInRange("cooldownSeconds", 5.0D, 0.0D, 3600.0D);
        builder.pop();
    }

    static {
        final Pair<OlympusConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(OlympusConfig::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }

}