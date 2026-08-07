package dev.xylonity.olympus.registry;

import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.common.effect.LightningStunEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OlympusMobEffects {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, Olympus.MOD_ID);

    public static final DeferredHolder<MobEffect, LightningStunEffect> LIGHTNING_STUN = MOB_EFFECTS.register("lightning_stun", LightningStunEffect::new);

}