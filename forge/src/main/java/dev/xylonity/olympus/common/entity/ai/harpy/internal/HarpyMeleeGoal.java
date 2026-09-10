package dev.xylonity.olympus.common.entity.ai.harpy.internal;

import dev.xylonity.olympus.common.entity.HarpyEntity;
import dev.xylonity.olympus.common.entity.ai.harpy.AbstractHarpyGoal;
import dev.xylonity.olympus.registry.OlympusSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public class HarpyMeleeGoal extends AbstractHarpyGoal {

    public HarpyMeleeGoal(final HarpyEntity harpy, final int attackDuration, final int cooldown) {
        super(harpy, attackDuration, cooldown);
    }

    @Override
    protected boolean canStartAttack() {
        final LivingEntity target = harpy.getTarget();
        return target != null && target.isAlive() && harpy.distanceTo(target) < 3;
    }

    @Override
    protected void onAttackStarted() {
        if (harpy.getAttackState() == HarpyEntity.STATE_DASH_ENDING) {
            harpy.setAttackState(HarpyEntity.STATE_IDLE);
        }

        harpy.setMeleeAttacking(true);
    }

    @Override
    protected void onAttackStopped() {
        harpy.setMeleeAttacking(false);
    }

    @Override
    protected int attackTick() {
        return 1;
    }

    @Override
    protected boolean performAttack() {
        final LivingEntity target = harpy.getTarget();
        if (!(harpy.level() instanceof ServerLevel serverLevel) || target == null || !target.isAlive()) {
            return false;
        }

        if (harpy.getBoundingBox().inflate(3).intersects(target.getBoundingBox())) {
            if (harpy.doHurtTarget(target)) {
                harpy.playSound(OlympusSounds.HARPY_HIT.get(), 1, 1);
            }

        }

        return true;
    }

}
