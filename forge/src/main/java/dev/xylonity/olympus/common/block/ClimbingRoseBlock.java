package dev.xylonity.olympus.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.MultifaceSpreader;
import net.minecraft.world.level.block.MultifaceSpreadeableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public final class ClimbingRoseBlock extends MultifaceSpreadeableBlock implements BonemealableBlock {

    public static final MapCodec<ClimbingRoseBlock> CODEC = simpleCodec(ClimbingRoseBlock::new);

    private final MultifaceSpreader spreader = new MultifaceSpreader(this);

    public ClimbingRoseBlock(final BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<ClimbingRoseBlock> codec() {
        return CODEC;
    }

    @Override
    public boolean isValidBonemealTarget(final LevelReader level, final BlockPos pos, final BlockState state) {
        return Direction.stream().anyMatch(face -> this.spreader.canSpreadInAnyDirection(state, level, pos, face.getOpposite()));
    }

    @Override
    public boolean isBonemealSuccess(final Level level, final RandomSource random, final BlockPos pos, final BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(final ServerLevel level, final RandomSource random, final BlockPos pos, final BlockState state) {
        this.spreader.spreadAll(state, level, pos, true);
    }

    @Override
    public MultifaceSpreader getSpreader() {
        return this.spreader;
    }

}
