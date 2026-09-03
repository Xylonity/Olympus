package dev.xylonity.olympus.common.effect;

import dev.xylonity.olympus.registry.OlympusMobEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public final class InvisibilityOfHadesEffect extends MobEffect {

    public InvisibilityOfHadesEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xC22836);
    }

    public static void onEffectStarted(final @NonNull LivingEntity living) {
        if (!(living.level() instanceof ServerLevel level)) {
            return;
        }

        // Purges the target if it's the affected entity (by the effect)
        for (final Entity entity : level.getAllEntities()) {
            if (!(entity instanceof Mob mob)) {
                continue;
            }

            if (mob.getTarget() == living) {
                mob.setTarget(null);
            }

            final Optional<LivingEntity> brainTarget = mob.getBrain().getMemoryInternal(MemoryModuleType.ATTACK_TARGET);
            if (brainTarget != null && brainTarget.isPresent() && brainTarget.orElse(null) == living) {
                mob.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
            }

        }

    }

    public static boolean preventsTargeting(final LivingEntity target) {
        return target.hasEffect(OlympusMobEffects.INVISIBILITY_OF_HADES.get());
    }

    public static void shortenAfterAttack(final ServerPlayer player) {
        final MobEffectInstance effect = player.getEffect(OlympusMobEffects.INVISIBILITY_OF_HADES.get());
        if (effect != null) {
            player.forceAddEffect(new MobEffectInstance(effect.getEffect(), Math.max(1, effect.getDuration() / 2), effect.getAmplifier(), effect.isAmbient(), effect.isVisible(), effect.showIcon()), player);
        }

    }

}