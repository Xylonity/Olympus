package dev.xylonity.olympus.common.block.entity;

import dev.xylonity.olympus.common.block.ParthenonSpawnerBlock;
import dev.xylonity.olympus.common.entity.HarpyEntity;
import dev.xylonity.olympus.registry.OlympusBlockEntities;
import dev.xylonity.olympus.registry.OlympusEntities;
import dev.xylonity.olympus.registry.OlympusItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/// Copy of the vanilla's trial spawner full implementation wrapped here for easier backporting
public final class ParthenonSpawnerBlockEntity extends BlockEntity {

    private static final String TAG_MODE = "mode";
    private static final String TAG_NEXT_ACTION = "next_action";
    private static final String TAG_REWARD_DROPPED = "reward_dropped";
    private static final String TAG_TARGET_PLAYER = "target_player";
    private static final String TAG_MOB_COUNT = "mob_count";
    private static final String TAG_MOB_PREFIX = "mob_";

    private Mode mode = Mode.IDLE;

    private long nextActionGameTime;
    private boolean rewardDropped;
    private @Nullable UUID targetPlayer;

    private final List<UUID> trackedHarpies = new ArrayList<>();

    public ParthenonSpawnerBlockEntity(final BlockPos pos, final BlockState state) {
        super(OlympusBlockEntities.PARTHENON_SPAWNER.get(), pos, state);
    }

    public static void serverTick(final ServerLevel level, final BlockPos pos, final BlockState state, final ParthenonSpawnerBlockEntity spawner) {
        switch (spawner.mode) {
            // If the player is close enough, starts the challenge
            case IDLE -> spawner.tryActivate(level, pos, state);
            // Clears dead harpies
            case FIRST_WAVE -> {
                spawner.removeDeadHarpies(level);
                if (spawner.trackedHarpies.isEmpty()) {
                    spawner.mode = Mode.BETWEEN_WAVES;
                    spawner.nextActionGameTime = level.getGameTime() + 40;
                    spawner.markUpdated();
                }

            }
            // Second harpies wave
            case BETWEEN_WAVES -> {
                if (level.getGameTime() >= spawner.nextActionGameTime) {
                    spawner.spawnWave(level, pos, 2, 1);
                    spawner.mode = Mode.SECOND_WAVE;
                    spawner.markUpdated();
                }

            }
            // Clears dead harpies
            case SECOND_WAVE -> {
                spawner.removeDeadHarpies(level);
                if (spawner.trackedHarpies.isEmpty()) {
                    spawner.beginReward(level, pos, state);
                }

            }
            // Challenge reward
            case REWARD -> spawner.tickReward(level, pos, state);
            // Cooldown before it can be activated again
            case COOLDOWN -> {
                if (level.getGameTime() >= spawner.nextActionGameTime) {
                    spawner.mode = Mode.IDLE;
                    spawner.nextActionGameTime = 0;
                    spawner.targetPlayer = null;
                    spawner.markUpdated();
                }

            }

        }

    }

    private void tryActivate(final ServerLevel level, final BlockPos pos, final BlockState state) {
        if (level.getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }

        final Player player = level.getNearestPlayer(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 9,
                entity -> entity instanceof Player player1 && player1.isAlive() && !player1.isCreative() && !player1.isSpectator());
        if (player == null) {
            return;
        }

        targetPlayer = player.getUUID();
        mode = Mode.FIRST_WAVE;

        setVisualState(level, pos, state, true, false);
        level.playSound(null, pos, SoundEvents.TRIAL_SPAWNER_DETECT_PLAYER, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.playSound(null, pos, SoundEvents.TRIAL_SPAWNER_OPEN_SHUTTER, SoundSource.BLOCKS, 1.0F, 1.0F);

        spawnWave(level, pos, 2, 0);

        markUpdated();
    }

    private void spawnWave(final ServerLevel level, final BlockPos pos, final int normalHarpies, final int eliteHarpies) {
        trackedHarpies.clear();

        final int total = normalHarpies + eliteHarpies;
        final double offset = level.getRandom().nextDouble() * Mth.TWO_PI;
        for (int idx = 0; idx < total; idx++) {
            final EntityType<HarpyEntity> harpy = idx < normalHarpies ? OlympusEntities.HARPY.get() : OlympusEntities.ELITE_HARPY.get();
            final double angle = offset + Mth.TWO_PI * idx / total;
            spawnHarpy(level, pos, harpy, angle);
        }

    }

    private void spawnHarpy(final ServerLevel level, final BlockPos pos, final EntityType<HarpyEntity> type, final double angle) {
        final HarpyEntity harpy = type.create(level, EntitySpawnReason.SPAWNER);
        if (harpy == null) {
            return;
        }

        final double x = pos.getX() + 0.5D + Math.cos(angle) * 2.5D;
        final double y = pos.getY() + 1.5D;
        final double z = pos.getZ() + 0.5D + Math.sin(angle) * 2.5D;

        harpy.snapTo(x, y, z, level.getRandom().nextFloat() * 360, 0);
        harpy.setPersistenceRequired();

        final Player player = findTargetPlayer(level, x, y, z);
        if (player != null) {
            harpy.setTarget(player);
        }

        if (!level.addFreshEntity(harpy)) {
            return;
        }

        trackedHarpies.add(harpy.getUUID());
        level.sendParticles(ParticleTypes.POOF, x, y + harpy.getBbHeight() * 0.5D, z, 24, 0.35D, 0.45D, 0.35D, 0.08D);
        level.playSound(null, x, y, z, SoundEvents.TRIAL_SPAWNER_SPAWN_MOB, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    private @Nullable Player findTargetPlayer(final ServerLevel level, final double x, final double y, final double z) {
        if (targetPlayer != null && level.getEntity(targetPlayer) instanceof Player player && player.isAlive() && !player.isCreative() && !player.isSpectator()) {
            return player;
        }

        return level.getNearestPlayer(x, y, z, 32, entity -> entity instanceof Player player && player.isAlive() && !player.isCreative() && !player.isSpectator());
    }

    private void removeDeadHarpies(final ServerLevel level) {
        final boolean changed = trackedHarpies.removeIf(uuid -> {
            final Entity entity = level.getEntity(uuid);
            return !(entity instanceof HarpyEntity harpy) || !harpy.isAlive();
        });

        if (changed) {
            markUpdated();
        }

    }

    private void beginReward(final ServerLevel level, final BlockPos pos, final BlockState state) {
        mode = Mode.REWARD;
        rewardDropped = false;
        nextActionGameTime = level.getGameTime() + 20;
        setVisualState(level, pos, state, false, true);
        level.playSound(null, pos, SoundEvents.TRIAL_SPAWNER_SPAWN_ITEM_BEGIN, SoundSource.BLOCKS, 1.0F, 1.0F);
        markUpdated();
    }

    private void tickReward(final ServerLevel level, final BlockPos pos, final BlockState state) {
        if (level.getGameTime() < nextActionGameTime) {
            return;
        }

        if (!rewardDropped) {
            dropRewards(level, pos);
            rewardDropped = true;
            nextActionGameTime = level.getGameTime() + 80;
            markUpdated();
            return;
        }

        mode = Mode.COOLDOWN;
        rewardDropped = false;
        // 1 hour cooldown
        nextActionGameTime = level.getGameTime() + (60 * 60 * 20);
        targetPlayer = null;
        setVisualState(level, pos, state, false, false);
        level.playSound(null, pos, SoundEvents.TRIAL_SPAWNER_CLOSE_SHUTTER, SoundSource.BLOCKS, 1, 1);

        markUpdated();
    }

    private void dropRewards(final ServerLevel level, final BlockPos pos) {
        final RandomSource random = level.getRandom();
        dropReward(level, pos, new ItemStack(OlympusItems.PARTHENON_KEY.get()));
        dropReward(level, pos, new ItemStack(Items.GOLD_INGOT, 3 + random.nextInt(5)));
        dropReward(level, pos, new ItemStack(Items.IRON_INGOT, 5 + random.nextInt(6)));
        dropReward(level, pos, new ItemStack(Items.EMERALD, 1 + random.nextInt(3)));
        dropReward(level, pos, new ItemStack(Items.EXPERIENCE_BOTTLE, 2 + random.nextInt(4)));
        dropReward(level, pos, new ItemStack(Items.DIAMOND, random.nextInt(2)));
        if (random.nextFloat() < 0.35F) {
            dropReward(level, pos, new ItemStack(Items.GOLDEN_APPLE));
        }

    }

    private static void dropReward(final ServerLevel level, final BlockPos pos, final ItemStack stack) {
        Block.popResource(level, pos.above(), stack);
        level.sendParticles(ParticleTypes.POOF, pos.getX() + 0.5D, pos.getY() + 1.15D, pos.getZ() + 0.5D, 8, 0.2D, 0.15D, 0.2D, 0.04D);
        level.playSound(null, pos, SoundEvents.TRIAL_SPAWNER_EJECT_ITEM, SoundSource.BLOCKS, 0.9F, 1.0F);
    }

    private static void setVisualState(final ServerLevel level, final BlockPos pos, final BlockState currentState, final boolean active, final boolean reward) {
        final BlockState state = currentState.setValue(ParthenonSpawnerBlock.ACTIVE, active).setValue(ParthenonSpawnerBlock.REWARD, reward);
        if (state != currentState) {
            level.setBlock(pos, state, Block.UPDATE_ALL);
        }

    }

    private void markUpdated() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }

    }

    @Override
    protected void loadAdditional(final ValueInput input) {
        super.loadAdditional(input);

        final int idx = Mth.clamp(input.getIntOr(TAG_MODE, 0), 0, Mode.values().length - 1);
        mode = Mode.values()[idx];
        nextActionGameTime = Math.max(0L, input.getLongOr(TAG_NEXT_ACTION, 0L));
        rewardDropped = input.getBooleanOr(TAG_REWARD_DROPPED, false);
        targetPlayer = parseUuid(input.getStringOr(TAG_TARGET_PLAYER, ""));

        trackedHarpies.clear();

        final int mobCount = Math.max(0, input.getIntOr(TAG_MOB_COUNT, 0));
        for (int index = 0; index < mobCount; index++) {
            final UUID uuid = parseUuid(input.getStringOr(TAG_MOB_PREFIX + index, ""));
            if (uuid != null) {
                trackedHarpies.add(uuid);
            }

        }

    }

    @Override
    protected void saveAdditional(final ValueOutput output) {
        super.saveAdditional(output);
        output.putInt(TAG_MODE, mode.ordinal());
        output.putLong(TAG_NEXT_ACTION, nextActionGameTime);
        output.putBoolean(TAG_REWARD_DROPPED, rewardDropped);
        if (targetPlayer != null) {
            output.putString(TAG_TARGET_PLAYER, targetPlayer.toString());
        }

        output.putInt(TAG_MOB_COUNT, trackedHarpies.size());
        for (int index = 0; index < trackedHarpies.size(); index++) {
            output.putString(TAG_MOB_PREFIX + index, trackedHarpies.get(index).toString());
        }

    }

    private static @Nullable UUID parseUuid(final String value) {
        try {
            return value.isEmpty() ? null : UUID.fromString(value);
        }
        catch (Exception _) {
            return null;
        }

    }

    private enum Mode {
        IDLE,
        FIRST_WAVE,
        BETWEEN_WAVES,
        SECOND_WAVE,
        REWARD,
        COOLDOWN
    }

}