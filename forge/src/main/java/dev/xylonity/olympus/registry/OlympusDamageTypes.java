package dev.xylonity.olympus.registry;

import dev.xylonity.olympus.Olympus;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public final class OlympusDamageTypes {

    public static final ResourceKey<DamageType> LIGHTNING = ResourceKey.create(Registries.DAMAGE_TYPE, Olympus.of("lightning"));

}