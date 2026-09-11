package dev.xylonity.olympus.common.effect;

import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.registry.OlympusParticles;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class LightningStunEffect extends MobEffect {

    public LightningStunEffect() {
        super(MobEffectCategory.HARMFUL, 0xF4D64A);
        addAttributeModifier(Attributes.ATTACK_DAMAGE, Olympus.of("lightning_stun_attack_damage"), -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    @Override
    public boolean applyEffectTick(final LivingEntity entity, final int amplifier) {
        if (!entity.level().isClientSide) {
            return true;
        }

        final int particleInterval = entity.isInvisible() ? 15 : 2;
        if (entity.getRandom().nextInt(particleInterval) != 0) {
            return true;
        }

        // Simulates the effect particles using the lightning sparks
        entity.level().addParticle(OlympusParticles.LIGHTNING_SPARKS.get(), entity.getRandomX(0.5D), entity.getRandomY(), entity.getRandomZ(0.5D), 0, 0, 0);
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(final int duration, final int amplifier) {
        return true;
    }

}
