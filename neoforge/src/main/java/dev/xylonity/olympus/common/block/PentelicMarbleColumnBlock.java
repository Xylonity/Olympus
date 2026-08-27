package dev.xylonity.olympus.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jspecify.annotations.NonNull;

public final class PentelicMarbleColumnBlock extends RotatedPillarBlock {

    public static final MapCodec<PentelicMarbleColumnBlock> CODEC = simpleCodec(PentelicMarbleColumnBlock::new);

    public static final EnumProperty<ColumnPart> PART = EnumProperty.create("part", ColumnPart.class);

    public PentelicMarbleColumnBlock(final BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(PART, ColumnPart.SINGLE));
    }

    @Override
    public @NonNull MapCodec<PentelicMarbleColumnBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PART);
    }

    @Override
    public @NonNull BlockState getStateForPlacement(final BlockPlaceContext context) {
        final BlockState state = super.getStateForPlacement(context);
        return updatePart(state, context.getLevel(), context.getClickedPos());
    }

    @Override
    protected @NonNull BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        if (directionToNeighbour.getAxis() != state.getValue(AXIS)) {
            return state;
        }

        return updatePart(state, level, pos);
    }

    private static BlockState updatePart(final BlockState state, final LevelReader level, final BlockPos pos) {
        final Direction.Axis axis = state.getValue(AXIS);
        final Direction negative = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE);
        final Direction positive = negative.getOpposite();
        final boolean connectedNegative = connects(level.getBlockState(pos.relative(negative)), axis);
        final boolean connectedPositive = connects(level.getBlockState(pos.relative(positive)), axis);
        final ColumnPart part;

        if (connectedNegative && connectedPositive) {
            part = ColumnPart.MIDDLE;
        }
        else if (connectedPositive) {
            part = ColumnPart.BASE;
        }
        else if (connectedNegative) {
            part = ColumnPart.CAPITAL;
        }
        else {
            part = ColumnPart.SINGLE;
        }

        return state.setValue(PART, part);
    }

    private static boolean connects(final BlockState state, final Direction.Axis axis) {
        return state.getBlock() instanceof PentelicMarbleColumnBlock && state.getValue(AXIS) == axis;
    }

    public enum ColumnPart implements StringRepresentable {
        SINGLE("single"),
        BASE("base"),
        MIDDLE("middle"),
        CAPITAL("capital");

        private final String name;

        ColumnPart(final String name) {
            this.name = name;
        }

        @Override
        public @NonNull String getSerializedName() {
            return this.name;
        }

    }

}
