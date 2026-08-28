package dev.xylonity.olympus.common.entity;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public class HarpyEntity extends Monster implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation FLY_ANIMATION = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation MELEE_ANIMATION = RawAnimation.begin().thenPlayAndHold("attack");
    private static final RawAnimation SHOT_ANIMATION = RawAnimation.begin().thenPlayAndHold("shot");
    private static final RawAnimation DASH_PREPARING_ANIMATION = RawAnimation.begin().thenPlayAndHold("dash_preparing");
    private static final RawAnimation DASH_ANIMATION = RawAnimation.begin().thenLoop("dash");
    private static final RawAnimation DASH_ENDING_ANIMATION = RawAnimation.begin().thenPlay("dash_ending").thenLoop("idle");

    private static final EntityDataAccessor<Integer> ATTACK_STATE = SynchedEntityData.defineId(HarpyEntity.class, EntityDataSerializers.INT);

    public static final int STATE_IDLE = 0;
    public static final int STATE_MELEE = 1;
    public static final int STATE_SHOT = 2;
    public static final int STATE_DASH_PREPARING = 3;
    public static final int STATE_DASHING = 4;
    public static final int STATE_DASH_ENDING = 5;

    private int attackStateTicks;

    public HarpyEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
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
        ;;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

}
