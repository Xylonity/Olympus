package dev.xylonity.olympus.common.block;

import com.mojang.serialization.MapCodec;
import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.registry.OlympusItems;
import dev.xylonity.olympus.registry.OlympusParticles;
import dev.xylonity.olympus.registry.OlympusSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;

public final class LockedChestBlock extends HorizontalDirectionalBlock {

    public static final MapCodec<LockedChestBlock> CODEC = simpleCodec(LockedChestBlock::new);
    private static final ResourceKey<net.minecraft.world.level.storage.loot.LootTable> LOOT_TABLE = ResourceKey.create(Registries.LOOT_TABLE, Olympus.of("chests/parthenon_chest"));

    public LockedChestBlock(final BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<LockedChestBlock> codec() {
        return CODEC;
    }

    @Override
    public @NonNull BlockState getStateForPlacement(final BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public @NonNull VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
        // Same shape as vanilla chest
        return Block.box(1, 0, 1, 15, 14, 15);
    }

    @Override
    protected @NonNull ItemInteractionResult useItemOn(final ItemStack stack, final BlockState state, final Level level, final BlockPos pos, final Player player, final InteractionHand hand, final BlockHitResult hitResult) {
        if (!stack.is(OlympusItems.PARTHENON_KEY.get())) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (level instanceof ServerLevel serverLevel) {
            unlock(serverLevel, pos, state, player, stack);
        }

        return ItemInteractionResult.SUCCESS;
    }

    private static void unlock(final ServerLevel level, final BlockPos pos, final BlockState state, final Player player, final ItemStack key) {
        // Permutes this block with an actual chest block
        final BlockState chestState = Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, state.getValue(FACING));
        level.setBlock(pos, chestState, Block.UPDATE_ALL);

        // Sets random loot
        if (level.getBlockEntity(pos) instanceof ChestBlockEntity chest) {
            chest.setLootTable(LOOT_TABLE, level.getRandom().nextLong());
            chest.setChanged();
        }

        if (!player.getAbilities().instabuild) {
            key.shrink(1);
        }

        level.playSound(null, pos, OlympusSounds.VAULT_OPEN_SHUTTER.get(), SoundSource.BLOCKS, 1.0F, 1.0F);

        final RandomSource random = level.getRandom();
        level.sendParticles(ParticleTypes.POOF, pos.getX() + 0.5D, pos.getY() + 0.7D, pos.getZ() + 0.5D, 28, 0.42D, 0.35D, 0.42D, 0.08D);
        for (int i = 0; i < 18; i++) {
            level.sendParticles(OlympusParticles.LIGHTNING_SPARKS.get(), pos.getX() + 0.2D + random.nextDouble() * 0.6D, pos.getY() + 0.2D + random.nextDouble() * 0.8D, pos.getZ() + 0.2D + random.nextDouble() * 0.6D, 1, 0, 0, 0, 0);
        }
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

}
