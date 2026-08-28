package dev.xylonity.olympus.common.entity;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.util.GeckoLibUtil;
import dev.xylonity.olympus.common.entity.ai.harpy.internal.HarpyFlightGoal;
import dev.xylonity.olympus.common.entity.ai.harpy.internal.MeleeHarpyGoal;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public class HarpyEntity extends Monster implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<Integer> ATTACK_STATE = SynchedEntityData.defineId(HarpyEntity.class, EntityDataSerializers.INT);

    private static final RawAnimation ANIMATION_IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation ANIMATION_FLY = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation ANIMATION_MELEE = RawAnimation.begin().thenPlayAndHold("attack");
    private static final RawAnimation ANIMATION_SHOT = RawAnimation.begin().thenPlayAndHold("shot");
    private static final RawAnimation ANIMATION_DASH_PREPARING = RawAnimation.begin().thenPlayAndHold("dash_preparing");
    private static final RawAnimation ANIMATION_DASH = RawAnimation.begin().thenLoop("dash");
    private static final RawAnimation ANIMATION_DASH_ENDING = RawAnimation.begin().thenPlay("dash_ending").thenLoop("idle");

    public static final int STATE_IDLE = 0;
    public static final int STATE_FLY = 1;
    public static final int STATE_MELEE = 2;
    public static final int STATE_SHOT = 3;
    public static final int STATE_DASH_PREPARING = 4;
    public static final int STATE_DASHING = 5;
    public static final int STATE_DASH_ENDING = 6;

    private static final int MELEE_ANIMATION_TICKS = 20;

    private int attackStateTicks;

    public HarpyEntity(final EntityType<? extends HarpyEntity> type, final Level level) {
        super(type, level);
        moveControl = new FlyingMoveControl(this, 20, true);
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

    @Override
    protected void registerGoals() {
        super.registerGoals();

        goalSelector.addGoal(1, new MeleeHarpyGoal(this, MELEE_ANIMATION_TICKS, 20));
        goalSelector.addGoal(8, new HarpyFlightGoal(this));

        targetSelector.addGoal(1, new HurtByTargetGoal(this, HarpyEntity.class).setAlertOthers());
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
        targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    @Override
    protected @NonNull PathNavigation createNavigation(final @NonNull Level level) {
        final FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setRequiredPathLength(24.0F);
        return navigation;
    }

    @Override
    protected void defineSynchedData(final SynchedEntityData.@NonNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACK_STATE, STATE_IDLE);
    }

    @Override
    public void tick() {
        super.tick();
        setNoGravity(true);
        resetFallDistance();
    }

    @Override
    public boolean causeFallDamage(final double fallDistance, final float damageModifier, final DamageSource damageSource) {
        return false;
    }

    public void setAttackState(final int state) {
        entityData.set(ATTACK_STATE, state);
        attackStateTicks = 0;
    }

    public int getAttackState() {
        return entityData.get(ATTACK_STATE);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("maincontroller", 2, this::mainPredicate));
        controllers.add(new AnimationController<>("meleecontroller", 2, state ->
                getAttackState() == STATE_MELEE ? state.setAndContinue(ANIMATION_MELEE) : PlayState.STOP
        ));

    }

    private PlayState mainPredicate(AnimationTest<HarpyEntity> event) {
        if (getAttackState() == STATE_IDLE) {
            event.setAnimation(ANIMATION_IDLE);
        }
        else if (getAttackState() == STATE_FLY) {
            event.setAnimation(ANIMATION_FLY);
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
