package dev.xylonity.olympus.common.effect;

import dev.xylonity.olympus.Olympus;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class LightningStunEffect extends MobEffect {

    public LightningStunEffect() {
        super(MobEffectCategory.HARMFUL, 0xF4D64A, ParticleTypes.ELECTRIC_SPARK);
        addAttributeModifier(
                Attributes.ATTACK_DAMAGE,
                Olympus.of("lightning_stun_attack"),
                -1.0D,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );

    }

}