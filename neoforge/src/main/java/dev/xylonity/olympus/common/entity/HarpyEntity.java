package dev.xylonity.olympus.common.entity;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.keyframehandler.AutoPlayingSoundKeyframeHandler;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.util.GeckoLibUtil;
import dev.xylonity.olympus.common.entity.ai.navigation.HarpyFlyingMoveControl;
import dev.xylonity.olympus.common.entity.ai.navigation.HarpyFlyingNavigation;
import dev.xylonity.olympus.common.entity.ai.harpy.internal.HarpyDashGoal;
import dev.xylonity.olympus.common.entity.ai.harpy.internal.HarpyFlightGoal;
import dev.xylonity.olympus.common.entity.ai.harpy.internal.HarpyProjectileDodgeGoal;
import dev.xylonity.olympus.common.entity.ai.harpy.internal.HarpyRetreatGoal;
import dev.xylonity.olympus.common.entity.ai.harpy.internal.HarpyMeleeGoal;
import dev.xylonity.olympus.common.entity.ai.harpy.internal.HarpyProjectileGoal;
import dev.xylonity.olympus.registry.OlympusEntities;
import dev.xylonity.olympus.registry.OlympusParticles;
import dev.xylonity.olympus.registry.OlympusSounds;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class HarpyEntity extends Monster implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<Integer> ATTACK_STATE = SynchedEntityData.defineId(HarpyEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> ELITE = SynchedEntityData.defineId(HarpyEntity.class, EntityDataSerializers.BOOLEAN);

    private static final RawAnimation ANIMATION_IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation ANIMATION_FLY = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation ANIMATION_MELEE = RawAnimation.begin().thenPlay("attack");
    private static final RawAnimation ANIMATION_SHOT = RawAnimation.begin().thenPlay("shot");
    private static final RawAnimation ANIMATION_DASH_PREPARING = RawAnimation.begin().thenPlay("dash_preparing");
    private static final RawAnimation ANIMATION_DASH = RawAnimation.begin().thenLoop("dash");
    private static final RawAnimation ANIMATION_DASH_ENDING = RawAnimation.begin().thenPlayAndHold("dash_ending");

    public static final int STATE_IDLE = 0;
    public static final int STATE_FLY = 1;
    public static final int STATE_MELEE = 2;
    public static final int STATE_SHOT = 3;
    public static final int STATE_DASH_PREPARING = 4;
    public static final int STATE_DASHING = 5;
    public static final int STATE_DASH_ENDING = 6;

    private static final int TICKS_ANIMATION_TRANSITION = 4;
    private static final int TICKS_MELEE_ANIMATION = 20;
    private static final int TICKS_SHOT_ANIMATION = 30;
    private static final int TICKS_SHOT_COOLDOWN = 12 * 20;
    private static final int TICKS_DASH_PREPARATION = 15 + TICKS_ANIMATION_TRANSITION;
    private static final int TICKS_DASH_ENDING = 15 + TICKS_ANIMATION_TRANSITION - 2;
    private static final int TICKS_SPECIAL_ATTACK_CHAIN_DELAY = 60;

    private static final int TICK_SHOT_RELEASE = 17;

    private static final double SPEED_THRESHOLD = 0.0025D;

    private static final double[] COMBAT_VERTICAL_SEARCH_OFFSETS = {
            0.0D, -0.5D, 0.5D, -1.0D, 1.0D, -1.5D, 1.5D, -2.0D, 2.0D, -2.5D
    };
    private static final double[] ESCAPE_VERTICAL_SEARCH_OFFSETS = {
            0.0D, -0.5D, 0.5D, -1.0D, 1.0D, -1.5D, 1.5D, -2.0D, 2.0D,
            -2.5D, 2.5D, -3.0D, 3.0D, -3.5D, 3.5D, -4.0D, 4.0D
    };

    private long specialAttackDelayEndGameTime;

    public HarpyEntity(final EntityType<? extends HarpyEntity> type, final Level level) {
        super(type, level);
        entityData.set(ELITE, type == OlympusEntities.ELITE_HARPY.get());
        if (isElite()) {
            xpReward = 14;
        }

        moveControl = new HarpyFlyingMoveControl(this);
        setNoGravity(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 35.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.FLYING_SPEED, 0.5)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    public static AttributeSupplier.Builder createEliteAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 70.0D)
                .add(Attributes.ARMOR, 8.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.FLYING_SPEED, 0.5D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.FOLLOW_RANGE, 28.0D)
                .add(Attributes.SCALE, 1.1D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        goalSelector.addGoal(0, new HarpyProjectileDodgeGoal(this));
        goalSelector.addGoal(1, new HarpyMeleeGoal(this, TICKS_MELEE_ANIMATION, 20));
        goalSelector.addGoal(2, new HarpyProjectileGoal(this, TICKS_SHOT_ANIMATION, TICK_SHOT_RELEASE, TICKS_SHOT_COOLDOWN));
        goalSelector.addGoal(2, new HarpyDashGoal(this, TICKS_DASH_PREPARATION, TICKS_DASH_ENDING));
        goalSelector.addGoal(3, new HarpyRetreatGoal(this));
        goalSelector.addGoal(8, new HarpyFlightGoal(this));

        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 25));

        targetSelector.addGoal(1, new HurtByTargetGoal(this, HarpyEntity.class).setAlertOthers());
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
        targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    @Override
    protected @NonNull PathNavigation createNavigation(final @NonNull Level level) {
        final HarpyFlyingNavigation navigation = new HarpyFlyingNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setRequiredPathLength(24.0F);
        return navigation;
    }

    @Override
    protected void defineSynchedData(final SynchedEntityData.@NonNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACK_STATE, STATE_IDLE);
        builder.define(ELITE, false);
    }

    @Override
    public void tick() {
        super.tick();

        setNoGravity(true);
        resetFallDistance();

        if (!level().isClientSide()) {
            if (!noPhysics) {
                escapeFromBlocks();
            }

            if (getAttackState() == STATE_DASHING) {
                spawnFeatherParticles(1 + random.nextInt(2));
            }

        }

    }

    @Override
    public boolean causeFallDamage(final double fallDistance, final float damageModifier, final DamageSource damageSource) {
        return false;
    }

    public void setAttackState(final int state) {
        entityData.set(ATTACK_STATE, state);
    }

    public int getAttackState() {
        return entityData.get(ATTACK_STATE);
    }

    public boolean isElite() {
        return entityData.get(ELITE);
    }

    public boolean canStartSpecialAttack() {
        return level().getGameTime() >= specialAttackDelayEndGameTime;
    }

    public void delaySpecialAttacks() {
        specialAttackDelayEndGameTime = Math.max(specialAttackDelayEndGameTime, level().getGameTime() + TICKS_SPECIAL_ATTACK_CHAIN_DELAY);
    }

    public @Nullable Vec3 findFreeCombatPosition(final LivingEntity target, final double x, final double preferredY, final double z) {
        final double minimumY = target.getBoundingBox().minY + 0.35D;
        final double maximumY = target.getEyeY() + 3.0D;
        final double y = Mth.clamp(preferredY, minimumY, maximumY);
        return findFreePositionNear(x, y, z, minimumY, maximumY, COMBAT_VERTICAL_SEARCH_OFFSETS, 3);
    }

    public void escapeFromBlocks() {
        final AABB currentBox = getBoundingBox().deflate(1.0E-4D);
        if (noPhysics || level().isClientSide() || level().noBlockCollision(this, currentBox)) {
            return;
        }

        final Vec3 pos = findFreePositionNear(getX(), getY(), getZ(), getY() - 4, getY() + 4, ESCAPE_VERTICAL_SEARCH_OFFSETS, 5);
        if (pos == null) {
            return;
        }

        getNavigation().stop();
        getMoveControl().setWait();
        setDeltaMovement(Vec3.ZERO);
        setPos(pos);

    }

    private @Nullable Vec3 findFreePositionNear(final double centerX, final double baseY, final double centerZ, final double minimumY, final double maximumY, final double[] verticalOffsets, final int horizontalRings) {
        for (int ring = 0; ring <= horizontalRings; ring++) {
            final int attempt = ring == 0 ? 1 : ring * 8;
            final double radius = ring * 0.75D;
            for (int sample = 0; sample < attempt; sample++) {
                final double angle = Mth.TWO_PI * sample / attempt;
                final double x = centerX + Mth.cos((float) angle) * radius;
                final double z = centerZ + Mth.sin((float) angle) * radius;

                for (final double verticalOffset : verticalOffsets) {
                    final double y = Mth.clamp(baseY + verticalOffset, minimumY, maximumY);
                    final AABB destinationBox = getBoundingBox().move(x - getX(), y - getY(), z - getZ()).deflate(1.0E-4D);
                    if (level().noBlockCollision(this, destinationBox)) {
                        return new Vec3(x, y, z);
                    }

                }

            }

        }

        return null;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        if (source.getDirectEntity() != null) {
            spawnFeatherParticles(5 + random.nextInt(10));
        }

        return super.hurtServer(level, source, damage);
    }

    private void spawnFeatherParticles(int amount) {
        for (int i = 0; i < amount; i++) {
            final double dx = (this.random.nextDouble() - 0.5) * 0.5;
            final double dy = (this.random.nextDouble() - 0.5) * 0.5;
            final double dz = (this.random.nextDouble() - 0.5) * 0.5;
            if (this.level() instanceof ServerLevel level) {
                final ParticleOptions particleType = isElite() ? OlympusParticles.ELITE_HARPY_FEATHER.get() : OlympusParticles.HARPY_FEATHER.get();
                level.sendParticles(particleType, this.getX(), this.getY() + getBbHeight() * 0.5f, this.getZ(), 1, dx, dy, dz, 0.1);
            }

        }

    }

    @Override
    protected @NonNull SoundEvent getDeathSound() {
        return OlympusSounds.HARPY_DEATH.get();
    }

    @Override
    protected @NonNull SoundEvent getHurtSound(@NonNull DamageSource source) {
        return OlympusSounds.HARPY_HIT.get();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("maincontroller", TICKS_ANIMATION_TRANSITION, this::mainPredicate)
                .setSoundKeyframeHandler(new AutoPlayingSoundKeyframeHandler<>()));
        controllers.add(new AnimationController<>("meleecontroller", TICKS_ANIMATION_TRANSITION, state ->
                getAttackState() == STATE_MELEE ? state.setAndContinue(ANIMATION_MELEE) : PlayState.STOP
        ));

    }

    private PlayState mainPredicate(AnimationTest<HarpyEntity> event) {
        if (getAttackState() == STATE_IDLE || getAttackState() == STATE_FLY) {
            final boolean moving = getDeltaMovement().lengthSqr() > SPEED_THRESHOLD;
            event.setAnimation(moving ? ANIMATION_FLY : ANIMATION_IDLE);
        }
        else if (getAttackState() == STATE_SHOT) {
            event.setAnimation(ANIMATION_SHOT);
        }
        else if (getAttackState() == STATE_DASH_PREPARING) {
            event.setAnimation(ANIMATION_DASH_PREPARING);
        }
        else if (getAttackState() == STATE_DASHING) {
            event.setAnimation(ANIMATION_DASH);
        }
        else if (getAttackState() == STATE_DASH_ENDING) {
            event.setAnimation(ANIMATION_DASH_ENDING);
        }

        return PlayState.CONTINUE;
    }

    @Override
    public @NonNull AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

}
