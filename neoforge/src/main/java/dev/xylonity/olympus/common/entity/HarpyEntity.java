package dev.xylonity.olympus.common.entity;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.util.GeckoLibUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
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

    private static final int TICKS_ANIMATION_IDLE = 35;
    private static final int TICKS_ANIMATION_FLY = 25;
    private static final int TICKS_ANIMATION_MELEE = 20;
    private static final int TICKS_ANIMATION_SHOT = 30;
    private static final int TICKS_ANIMATION_DASH_PREPARING = 15;
    private static final int TICKS_ANIMATION_DASH = 10;
    private static final int TICKS_ANIMATION_DASH_ENDING = 15;

    private static final int STATE_IDLE = 0;
    private static final int STATE_MELEE = 1;
    private static final int STATE_SHOT = 2;
    private static final int STATE_DASH_PREPARING = 3;
    private static final int STATE_DASHING = 4;
    private static final int STATE_DASH_ENDING = 5;

    private int attackStateTicks;

    public HarpyEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 35.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.FLYING_SPEED, 0.34D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void defineSynchedData(final SynchedEntityData.@NonNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACK_STATE, STATE_IDLE);
    }

    private void setAttackState(final int state) {
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
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

}
