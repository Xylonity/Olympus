package dev.xylonity.olympus.common.entity;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;
import dev.xylonity.olympus.registry.OlympusEntities;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;

public final class SummoningSpearsEntity extends Entity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation APPEARANCE = RawAnimation.begin().thenPlayAndHold("appearance");

    private static final EntityDataAccessor<Long> SPAWN_TICK = SynchedEntityData.defineId(SummoningSpearsEntity.class, EntityDataSerializers.LONG);
    private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(SummoningSpearsEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> ANCHORED = SynchedEntityData.defineId(SummoningSpearsEntity.class, EntityDataSerializers.BOOLEAN);

    private static final String TAG_SPAWN_TICK = "SpawnTick";

    private static final int APPEARANCE_DELAY = 5;
    private static final int DISSOLVE_DURATION = 3 * 20;
    private static final int DISSOLVE_START = APPEARANCE_DELAY + 10 + 2 * 20;

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
        builder.define(ANCHORED, false);
    }

    @Override
    public void tick() {
        super.tick();

        followOwner();

        if (!level().isClientSide() && getLifetimeAge(0) >= DISSOLVE_START + DISSOLVE_DURATION) {
            discard();
        }

    }

    private void followOwner() {
        if (entityData.get(ANCHORED)) {
            return;
        }

        final Entity owner = level().getEntity(entityData.get(OWNER_ID));
        if (owner == null) {
            if (!level().isClientSide()) {
                entityData.set(ANCHORED, true);
            }

            return;
        }

        setPos(owner.getX(), owner.getY(), owner.getZ());
        // Applies a bit of fall damage
        owner.fallDistance = 3;

        if (!level().isClientSide() && (owner.onGround() || owner.isInWater())) {
            entityData.set(ANCHORED, true);
        }

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
        entityData.set(ANCHORED, true);
    }

    @Override
    protected void addAdditionalSaveData(final ValueOutput output) {
        output.putLong(TAG_SPAWN_TICK, entityData.get(SPAWN_TICK));
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