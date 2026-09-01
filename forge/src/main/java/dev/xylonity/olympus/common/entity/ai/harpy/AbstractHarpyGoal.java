package dev.xylonity.olympus.common.entity.ai.harpy;

import dev.xylonity.olympus.common.entity.HarpyEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public abstract class AbstractHarpyGoal extends Goal {

    protected final HarpyEntity harpy;

    private final int attackDuration;
    private final int cooldownTicks;

    private int attackTicks;
    private int activeAttackTick;
    private long cooldownEndGameTime;
    private boolean attackPerformed;
    private boolean cooldownStarted;

    protected AbstractHarpyGoal(final HarpyEntity harpy, final int attackDuration, final int cooldownTicks) {
        if (attackDuration <= 0) {
            throw new IllegalArgumentException("Attack duration must be positive");
        }
        if (cooldownTicks < 0) {
            throw new IllegalArgumentException("Attack cooldown cannot be negative");
        }

        this.harpy = harpy;
        this.attackDuration = attackDuration;
        this.cooldownTicks = cooldownTicks;

        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public final boolean canUse() {
        return harpy.level().getGameTime() >= cooldownEndGameTime && canStartAttack();
    }

    @Override
    public final void start() {
        activeAttackTick = attackTick();
        if (activeAttackTick < 1 || activeAttackTick > attackDuration) {
            throw new IllegalStateException(getClass().getSimpleName() + " incorrect attack tick");
        }

        attackTicks = 0;
        attackPerformed = false;
        cooldownStarted = false;
        harpy.setAttackState(attackState());
        onAttackStarted();
    }

    @Override
    public final void tick() {
        tickAttack();
        attackTicks++;

        if (attackTicks == activeAttackTick && performAttack()) {
            attackPerformed = true;
            if (startsCooldownOnAttack()) {
                startCooldown();
            }

        }

    }

    @Override
    public final boolean canContinueToUse() {
        return attackTicks < attackDuration && shouldContinueAttack();
    }

    @Override
    public final void stop() {
        onAttackStopped();

        if (attackPerformed && !cooldownStarted) {
            startCooldown();
        }

        if (harpy.getAttackState() == attackState()) {
            harpy.setAttackState(HarpyEntity.STATE_IDLE);
        }

        attackTicks = 0;
        activeAttackTick = 0;
        attackPerformed = false;
        cooldownStarted = false;
    }

    @Override
    public final boolean requiresUpdateEveryTick() {
        return true;
    }

    protected abstract boolean canStartAttack();
    protected abstract int attackState();
    protected abstract int attackTick();
    protected abstract boolean performAttack();

    protected boolean shouldContinueAttack() {
        return true;
    }

    protected boolean startsCooldownOnAttack() {
        return false;
    }

    protected void onAttackStarted() {
        ;;
    }

    protected void tickAttack() {
        ;;
    }

    protected void onAttackStopped() {
        ;;
    }

    private void startCooldown() {
        cooldownEndGameTime = harpy.level().getGameTime() + cooldownTicks;
        cooldownStarted = true;
    }

}