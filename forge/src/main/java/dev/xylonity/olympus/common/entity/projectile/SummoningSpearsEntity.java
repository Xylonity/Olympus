package dev.xylonity.olympus.common.entity.projectile;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;
import dev.xylonity.olympus.config.OlympusConfig;
import dev.xylonity.olympus.registry.OlympusEntities;
import dev.xylonity.olympus.registry.OlympusParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;

public final class SummoningSpearsEntity extends Entity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation APPEARANCE = RawAnimation.begin().thenPlayAndHold("appearance");

    private static final EntityDataAccessor<Long> SPAWN_TICK = SynchedEntityData.defineId(SummoningSpearsEntity.class, EntityDataSerializers.LONG);
    private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(SummoningSpearsEntity.class, EntityDataSerializers.INT);
    // Saves the positions (byte shifting) in which each spear will spawn with a height difference (so the height is adapted to the terrain)
    private static final EntityDataAccessor<Long> SPEAR_GROUND_STATES = SynchedEntityData.defineId(SummoningSpearsEntity.class, EntityDataSerializers.LONG);

    private static final String TAG_SPAWN_TICK = "SpawnTick";
    private static final String TAG_DAMAGE_APPLIED = "DamageApplied";
    private static final String TAG_SPEAR_GROUND_STATES = "SpearGroundStates";

    // Animation purposes
    private static final int APPEARANCE_DELAY = 2;

    private static final int DISSOLVE_DURATION = 3 * 20;
    private static final int DISSOLVE_START = APPEARANCE_DELAY + 10 + 2 * 20;

    private static final double MIN_HEIGHT = 0.25;
    private static final double HEIGHT_RANGE = 2;

    private boolean damageApplied;

    public SummoningSpearsEntity(final EntityType<? extends SummoningSpearsEntity> entityType, final Level level) {
        super(entityType, level);
        noPhysics = true;
        setNoGravity(true);

        if (!level.isClientSide()) {
            entityData.set(SPAWN_TICK, level.getGameTime());
        }

    }

    public SummoningSpearsEntity(final Level level, final Entity owner) {
        this(OlympusEntities.SUMMONING_SPEARS.get(), level);
        setPos(owner.getX(), owner.getY(), owner.getZ());
        entityData.set(OWNER_ID, owner.getId());
        entityData.set(SPEAR_GROUND_STATES, calculateSpearGroundStates());
    }

    @Override
    protected void defineSynchedData(final SynchedEntityData.Builder builder) {
        builder.define(SPAWN_TICK, -1L);
        builder.define(OWNER_ID, -1);
        builder.define(SPEAR_GROUND_STATES, -1L);
    }

    @Override
    public void tick() {
        super.tick();

        if (level() instanceof ServerLevel serverLevel) {
            // Computed the first tick
            if (entityData.get(SPEAR_GROUND_STATES) < 0) {
                entityData.set(SPEAR_GROUND_STATES, calculateSpearGroundStates());
            }

            if (getLifetimeAge(0) >= APPEARANCE_DELAY) {
                if (!damageApplied) {
                    damageNearbyEntities(serverLevel);
                    damageApplied = true;
                }

                spawnTraceParticles(serverLevel);
            }

            if (getLifetimeAge(0) >= DISSOLVE_START + DISSOLVE_DURATION) {
                discard();
            }
        }

    }

    private long calculateSpearGroundStates() {
        // XZ positions of each spear (derived from the actual model)
        final double[] modelX = {-17.95468, 5.95468, 2.95231, -8.04769, -26.64769, 27.64769, 23.94769, -26.34769, -37.15468, 31.65468, 1.75468, -10.24532};
        final double[] modelZ = {0, 2, -20.3, 19.3, 27.3, 28.4, -18.7, -18.6, 4, 10, 38.6, -34.1};
        long groundStates = 0;

        // Per spear
        for (int index = 0; index < modelX.length; index++) {
            final double x = getX() + modelX[index] / 16;
            final double z = getZ() - modelZ[index] / 16;
            // Finds the closest relevant ground position
            groundStates |= (long) findGroundState(x, z) << index * 5;
        }

        return groundStates;
    }

    /// Derived from my own implementation
    /// https://github.com/Xylonity/Companions/blob/v1.20.1/common/src/main/java/dev/xylonity/companions/common/entity/ai/pontiff/goal/HolinessImpactAttackGoal.java#L61
    private int findGroundState(final double x, final double z) {
        final int blockX = (int) Math.floor(x);
        final int blockZ = (int) Math.floor(z);
        final int highestBlockY = (int) Math.floor(getY() + 2 - 0.0001);
        final int lowestBlockY = (int) Math.floor(getY() - 3);
        // Block's collision shape coords
        final double localX = x - blockX;
        final double localZ = z - blockZ;

        // Searches downward for the highest collision surface beneath this spear's exact model position
        for (int blockY = highestBlockY; blockY >= lowestBlockY; blockY--) {
            final BlockPos blockPos = new BlockPos(blockX, blockY, blockZ);
            final VoxelShape collisionShape = level().getBlockState(blockPos).getCollisionShape(level(), blockPos);
            double y = Double.NEGATIVE_INFINITY;

            for (final AABB box : collisionShape.toAabbs()) {
                if (localX >= box.minX - 0.0001 && localX <= box.maxX + 0.0001 && localZ >= box.minZ - 0.0001 && localZ <= box.maxZ + 0.0001) {
                    y = Math.max(y, blockY + box.maxY);
                }

            }

            if (y == Double.NEGATIVE_INFINITY) {
                continue;
            }

            final double yDiff = y - getY();
            if (yDiff <= -5 || yDiff >= 5) {
                return 0;
            }

            // Encodes the vertical offset in steps (quarters)
            final int offset = Math.clamp((int) Math.round(yDiff * 4), -11, 8);
            return offset + 12;
        }

        return 0;
    }

    private void spawnTraceParticles(final ServerLevel level) {
        final double areaWidth = 5;
        final double x = getX() + (random.nextDouble() - 0.5) * areaWidth;
        final double y = getY() + MIN_HEIGHT + random.nextDouble() * HEIGHT_RANGE;
        final double z = getZ() + (random.nextDouble() - 0.5) * areaWidth;
        final double xSpeed = (random.nextDouble() - 0.5) * 0.02;
        final double ySpeed = 0.025 + random.nextDouble() * 0.035;
        final double zSpeed = (random.nextDouble() - 0.5) * 0.02;

        level.sendParticles(OlympusParticles.ARES_SPEAR_TRACE.get(), x, y, z, 0, xSpeed, ySpeed, zSpeed, 1);
    }

    private void damageNearbyEntities(final ServerLevel level) {
        final double halfWidth = OlympusConfig.INSTANCE.aresSpearAbilityRadius.get();
        final AABB area = new AABB(
                getX() - halfWidth, getY() + MIN_HEIGHT, getZ() - halfWidth,
                getX() + halfWidth, getY() + MIN_HEIGHT + HEIGHT_RANGE, getZ() + halfWidth
        );
        final Entity owner = level.getEntity(entityData.get(OWNER_ID));
        final DamageSource damageSource = damageSources().trident(this, owner == null ? this : owner);

        level.getEntitiesOfClass(LivingEntity.class, area, target -> target.isAlive() && target != owner
        ).forEach(target -> {
            if (target.hurtServer(level, damageSource, OlympusConfig.INSTANCE.aresSpearAbilityDamage.get().floatValue()) && owner instanceof LivingEntity livingOwner) {
                livingOwner.setLastHurtMob(target);
            }

        });

    }

    public float getDissolveVisibility(final float partialTick) {
        // Converts lifetime age into a visibility value
        final float dissolveAge = getLifetimeAge(partialTick) - DISSOLVE_START;
        if (dissolveAge <= 0) {
            return 1;
        }

        return Math.clamp(1 - dissolveAge / DISSOLVE_DURATION, 0, 1);
    }

    public long getSpearGroundStates() {
        return Math.max(0, entityData.get(SPEAR_GROUND_STATES));
    }

    private float getLifetimeAge(final float partialTick) {
        final long appeartick = entityData.get(SPAWN_TICK);
        return appeartick < 0L ? tickCount + partialTick : level().getGameTime() - appeartick + partialTick;
    }

    @Override
    protected void readAdditionalSaveData(final ValueInput input) {
        entityData.set(SPAWN_TICK, input.getLongOr(TAG_SPAWN_TICK, level().getGameTime()));
        entityData.set(SPEAR_GROUND_STATES, input.getLongOr(TAG_SPEAR_GROUND_STATES, -1L));
        damageApplied = input.getBooleanOr(TAG_DAMAGE_APPLIED, false);
    }

    @Override
    protected void addAdditionalSaveData(final ValueOutput output) {
        output.putLong(TAG_SPAWN_TICK, entityData.get(SPAWN_TICK));
        output.putLong(TAG_SPEAR_GROUND_STATES, entityData.get(SPEAR_GROUND_STATES));
        output.putBoolean(TAG_DAMAGE_APPLIED, damageApplied);
    }

    @Override
    protected @NonNull MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public boolean hurtServer(final ServerLevel level, final DamageSource damageSource, final float amount) {
        return false;
    }

    @Override
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("main", 0, state -> state.setAndContinue(
                getLifetimeAge(0) < APPEARANCE_DELAY ? IDLE : APPEARANCE
        )));

    }

    @Override
    public @NonNull AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

}
