package dev.xylonity.olympus.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public final class PoppyOfDemeterBlock extends FlowerBlock {

    public static final MapCodec<PoppyOfDemeterBlock> CODEC = simpleCodec(PoppyOfDemeterBlock::new);

    public PoppyOfDemeterBlock(final BlockBehaviour.Properties properties) {
        super(SuspiciousStewEffects.EMPTY, properties);
    }

    @Override
    public MapCodec<PoppyOfDemeterBlock> codec() {
        return CODEC;
    }

    @Override
    protected void onPlace(final BlockState state, final Level level, final BlockPos pos, final BlockState oldState, final boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);

        if (!level.isClientSide() && !oldState.is(this)) {
            level.scheduleTick(pos, this, 100);
        }

    }

    @Override
    protected void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
        applyBonemealInRadius(level, pos, random);
        level.scheduleTick(pos, this, 100);
    }

    private static void applyBonemealInRadius(final ServerLevel level, final BlockPos origin, final RandomSource random) {
        final int radius = 5;
        final BlockPos min = origin.offset(-radius, -radius, -radius);
        final BlockPos max = origin.offset(radius, radius, radius);
        final double radiusSquared = radius * radius;

        // Applies bone meal on valid positions around the given radius above
        for (final BlockPos target : BlockPos.betweenClosed(min, max)) {
            if (origin.distSqr(target) > radiusSquared || !level.hasChunkAt(target)) {
                continue;
            }

            final BlockState targetState = level.getBlockState(target);
            if (targetState.getBlock() instanceof BonemealableBlock bonemealable && bonemealable.isValidBonemealTarget(level, target, targetState) && bonemealable.isBonemealSuccess(level, random, target, targetState)) {
                bonemealable.performBonemeal(level, random, target, targetState);
                level.levelEvent(1505, target, 15);
            }

        }

    }

}