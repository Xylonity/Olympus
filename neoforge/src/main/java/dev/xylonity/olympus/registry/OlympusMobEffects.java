package dev.xylonity.olympus.registry;

import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.common.effect.InvisibilityOfHadesEffect;
import dev.xylonity.olympus.common.effect.LightningStunEffect;
import dev.xylonity.knightlib.api.registrar.ResourceDispatcher;
import dev.xylonity.knightlib.api.registrar.ResourceEntry;
import dev.xylonity.knightlib.api.registrar.ResourceRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;

public final class OlympusMobEffects {

    public static final ResourceRegistry<MobEffect> MOB_EFFECTS = ResourceDispatcher.create(BuiltInRegistries.MOB_EFFECT, Olympus.MOD_ID);

    public static final ResourceEntry<LightningStunEffect> LIGHTNING_STUN = MOB_EFFECTS.register("lightning_stun", LightningStunEffect::new);
    public static final ResourceEntry<InvisibilityOfHadesEffect> INVISIBILITY_OF_HADES = MOB_EFFECTS.register("invisibility_of_hades", InvisibilityOfHadesEffect::new);

}
