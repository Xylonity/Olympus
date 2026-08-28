package dev.xylonity.olympus.common.entity.ai.harpy;

import dev.xylonity.olympus.common.entity.HarpyEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public abstract class AbstractHarpyGoal extends Goal {

    protected final HarpyEntity harpy;
    protected final int attackDuration;
    protected final int cooldown;

    protected int attackTicks;

    public AbstractHarpyGoal(final HarpyEntity harpy, int attackDuration, int cooldown) {
        this.harpy = harpy;
        this.attackDuration = attackDuration;
        this.cooldown = cooldown;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return false;
    }

    @Override
    public void start() {
        harpy.setAttackState(attackState());
        attackTicks = 0;
    }

    @Override
    public void tick() {
        if (attackTicks == momentumTicks()) {
            doAttack();
        }

        attackTicks++;
    }

    @Override
    public boolean canContinueToUse() {
        return attackTicks < attackDuration;
    }

    @Override
    public void stop() {
        attackTicks = 0;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    protected abstract int attackState();
    protected abstract int momentumTicks();
    protected abstract void doAttack();

}
