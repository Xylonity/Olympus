package dev.xylonity.olympus.common.block.entity;

import dev.xylonity.olympus.registry.OlympusBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
    public void load(final CompoundTag input) {
        super.load(input);
        targetPos = input.contains(TAG_TARGET_POS) ? BlockPos.of(input.getLong(TAG_TARGET_POS)) : null;
        accelerations = Math.max(0, input.getInt(TAG_ACCELERATIONS));
        cycleTicks = Math.max(0, input.getInt(TAG_TICKS));
    }

    @Override
    protected void saveAdditional(final CompoundTag output) {
        super.saveAdditional(output);
        if (targetPos != null) {
            output.putLong(TAG_TARGET_POS, targetPos.asLong());
        }

        output.putInt(TAG_ACCELERATIONS, accelerations);
        output.putInt(TAG_TICKS, cycleTicks);
    }

}
