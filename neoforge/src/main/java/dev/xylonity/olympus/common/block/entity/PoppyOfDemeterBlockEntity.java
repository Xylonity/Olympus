package dev.xylonity.olympus.common.block.entity;

import dev.xylonity.olympus.registry.OlympusBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

/// Saves relevant logic for the ticks per cycle and the current block where applying the growth acceleration
public final class PoppyOfDemeterBlockEntity extends BlockEntity {

    private static final String TAG_TARGET_POS = "target_pos";
    private static final String TAG_ACCELERATIONS = "accelerations";
    private static final String TAG_TICKS = "cycle_ticks";

    private @Nullable BlockPos targetPos;
    private int accelerations;
    private int cycleTicks;

    public PoppyOfDemeterBlockEntity(final BlockPos pos, final BlockState state) {
        super(OlympusBlockEntities.POPPY_OF_DEMETER.get(), pos, state);
    }

    public @Nullable BlockPos getTargetPos() {
        return targetPos;
    }

    public void attachTo(final @Nullable BlockPos targetPos) {
        this.targetPos = targetPos == null ? null : targetPos.immutable();
        accelerations = 0;
        cycleTicks = 0;
        setChanged();
    }

    public int advanceCycle(final int ticks) {
        cycleTicks += ticks;
        setChanged();
        return cycleTicks;
    }

    public void resetCycle() {
        cycleTicks = 0;
        setChanged();
    }

    public int recordAcceleration() {
        accelerations++;
        setChanged();
        return accelerations;
    }

    @Override
    protected void loadAdditional(final ValueInput input) {
        super.loadAdditional(input);
        targetPos = input.getLong(TAG_TARGET_POS).map(BlockPos::of).orElse(null);
        accelerations = Math.max(0, input.getIntOr(TAG_ACCELERATIONS, 0));
        cycleTicks = Math.max(0, input.getIntOr(TAG_TICKS, 0));
    }

    @Override
    protected void saveAdditional(final ValueOutput output) {
        super.saveAdditional(output);
        if (targetPos != null) {
            output.putLong(TAG_TARGET_POS, targetPos.asLong());
        }

        output.putInt(TAG_ACCELERATIONS, accelerations);
        output.putInt(TAG_TICKS, cycleTicks);
    }

}