package dev.xylonity.olympus.common.effect;

import dev.xylonity.olympus.registry.OlympusParticles;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class LightningStunEffect extends MobEffect {

    public LightningStunEffect() {
        super(MobEffectCategory.HARMFUL, 0xF4D64A);
        addAttributeModifier(Attributes.ATTACK_DAMAGE, "e73f2ac1-4170-4ac4-8d2f-80f37e32ec52", -1, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public void applyEffectTick(final LivingEntity entity, final int amplifier) {
        if (!entity.level().isClientSide) {
            return;
        }

        final int particleInterval = entity.isInvisible() ? 15 : 2;
        if (entity.getRandom().nextInt(particleInterval) != 0) {
            return;
        }

        // Simulates the effect particles using the lightning sparks
        entity.level().addParticle(OlympusParticles.LIGHTNING_SPARKS.get(), entity.getRandomX(0.5D), entity.getRandomY(), entity.getRandomZ(0.5D), 0, 0, 0);
    }

    @Override
    public boolean isDurationEffectTick(final int duration, final int amplifier) {
        return true;
    }

}