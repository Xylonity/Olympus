package dev.xylonity.olympus.common.entity.ai.harpy.internal;

import dev.xylonity.olympus.common.entity.HarpyEntity;
import dev.xylonity.olympus.common.entity.ai.harpy.AbstractHarpyGoal;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public class MeleeHarpyGoal extends AbstractHarpyGoal {

    public MeleeHarpyGoal(HarpyEntity harpy, int attackDuration, int cooldown) {
        super(harpy, attackDuration, cooldown);
    }

    @Override
    public boolean canUse() {
        if (harpy.getTarget() instanceof LivingEntity entity) {
            if (harpy.distanceTo(entity) <= 2) {
                return true;
            }

        }

        return false;
    }

    @Override
    protected int attackState() {
        return HarpyEntity.STATE_MELEE;
    }

    @Override
    protected int momentumTicks() {
        return 0;
    }

    @Override
    public void stop() {
        super.stop();
        harpy.setAttackState(0);
    }

    @Override
    protected void doAttack() {
        if (harpy.getBoundingBox().inflate(1.25).intersects(harpy.getTarget().getBoundingBox())) {
            harpy.doHurtTarget((ServerLevel) harpy.level(), harpy.getTarget());
        }

    }

}
