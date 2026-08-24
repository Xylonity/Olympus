package dev.xylonity.olympus.common.entity;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;
import dev.xylonity.olympus.registry.OlympusEntities;
import dev.xylonity.olympus.registry.OlympusParticles;
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
import org.jspecify.annotations.NonNull;

public final class SummoningSpearsEntity extends Entity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation APPEARANCE = RawAnimation.begin().thenPlayAndHold("appearance");

    private static final EntityDataAccessor<Long> SPAWN_TICK = SynchedEntityData.defineId(SummoningSpearsEntity.class, EntityDataSerializers.LONG);
    private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(SummoningSpearsEntity.class, EntityDataSerializers.INT);

    private static final String TAG_SPAWN_TICK = "SpawnTick";
    private static final String TAG_DAMAGE_APPLIED = "DamageApplied";

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
    }

    @Override
    protected void defineSynchedData(final SynchedEntityData.Builder builder) {
        builder.define(SPAWN_TICK, -1L);
        builder.define(OWNER_ID, -1);
    }

    @Override
    public void tick() {
        super.tick();

        if (level() instanceof ServerLevel serverLevel) {
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
        final double halfWidth = 2.5;
        final AABB area = new AABB(
                getX() - halfWidth, getY() + MIN_HEIGHT, getZ() - halfWidth,
                getX() + halfWidth, getY() + MIN_HEIGHT + HEIGHT_RANGE, getZ() + halfWidth
        );
        final Entity owner = level.getEntity(entityData.get(OWNER_ID));
        final DamageSource damageSource = damageSources().trident(this, owner == null ? this : owner);

        level.getEntitiesOfClass(LivingEntity.class, area, target -> target.isAlive() && target != owner
        ).forEach(target -> {
            if (target.hurtServer(level, damageSource, 8) && owner instanceof LivingEntity livingOwner) {
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

    private float getLifetimeAge(final float partialTick) {
        final long appeartick = entityData.get(SPAWN_TICK);
        return appeartick < 0L ? tickCount + partialTick : level().getGameTime() - appeartick + partialTick;
    }

    @Override
    protected void readAdditionalSaveData(final ValueInput input) {
        entityData.set(SPAWN_TICK, input.getLongOr(TAG_SPAWN_TICK, level().getGameTime()));
        damageApplied = input.getBooleanOr(TAG_DAMAGE_APPLIED, false);
    }

    @Override
    protected void addAdditionalSaveData(final ValueOutput output) {
        output.putLong(TAG_SPAWN_TICK, entityData.get(SPAWN_TICK));
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
