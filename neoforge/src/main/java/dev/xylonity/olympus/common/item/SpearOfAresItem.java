package dev.xylonity.olympus.common.item;

import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.util.GeckoLibUtil;
import dev.xylonity.olympus.client.item.renderer.SpearOfAresItemRenderer;
import dev.xylonity.olympus.common.entity.SpearOfAresEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.Consumer;

public final class SpearOfAresItem extends TridentItem implements GeoItem {

    private static final String TAG_SPECIAL_ABILITY_CHARGED = "olympus_special_ability_charged";
    private static final String TAG_SPECIAL_ABILITY_COOLDOWN_END = "olympus_special_ability_cooldown_end";

    // 6 seconds after using the ability (inclusive)
    public static final int SPECIAL_ABILITY_COOLDOWN = 6 * 20;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public SpearOfAresItem(final Properties properties) {
        super(properties);
        GeoItem.registerSyncedAnimatable(this);
    }

    public static boolean isSpecialAbilityCharged(final ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBooleanOr(TAG_SPECIAL_ABILITY_CHARGED, false);
    }

    public static void setSpecialAbilityCharged(final ItemStack stack, final boolean charged) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            if (charged) {
                tag.putBoolean(TAG_SPECIAL_ABILITY_CHARGED, true);
            }
            else {
                tag.remove(TAG_SPECIAL_ABILITY_CHARGED);
            }

        });

        if (charged) {
            stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of(1.0F), List.of(), List.of(), List.of()));
        }
        else {
            stack.remove(DataComponents.CUSTOM_MODEL_DATA);
        }

    }

    public static boolean isSpecialAbilityReady(final ItemStack stack, final Level level) {
        final long cooldownEnd = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getLongOr(TAG_SPECIAL_ABILITY_COOLDOWN_END, 0L);
        return level.getGameTime() >= cooldownEnd;
    }

    public static void startSpecialAbilityCooldown(final ItemStack stack, final Level level) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag ->
                tag.putLong(TAG_SPECIAL_ABILITY_COOLDOWN_END, level.getGameTime() + SPECIAL_ABILITY_COOLDOWN)
        );

    }

    /// Same code over again {@link PoseidonTridentItem}
    @Override
    public boolean releaseUsing(final @NonNull ItemStack stack, final @NonNull Level level, final @NonNull LivingEntity user, final int remainingUseDuration) {
        if (!(user instanceof Player player)) {
            return false;
        }

        final int useTicks = getUseDuration(stack, user) - remainingUseDuration;
        if (useTicks < THROW_THRESHOLD_TIME || stack.nextDamageWillBreak() || player.getCooldowns().isOnCooldown(stack)) {
            return false;
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }

        // Reduces the item durability and doesn't delete the stack
        stack.hurtWithoutBreaking(1, player);
        final ItemStack aresSpear = stack.copyWithCount(1);
        final SpearOfAresEntity spear = Projectile.spawnProjectileFromRotation(
                SpearOfAresEntity::new, serverLevel, aresSpear, player, 0.0F, PROJECTILE_SHOOT_POWER, 1.0F
        );
        spear.pickup = AbstractArrow.Pickup.DISALLOWED;

        player.getCooldowns().addCooldown(stack, 50);
        level.playSound(null, spear, SoundEvents.TRIDENT_THROW.value(), SoundSource.PLAYERS, 1.0F, 1.0F);

        return true;
    }

    @Override
    public @NonNull InteractionResult use(final @NonNull Level level, final @NonNull Player player, final @NonNull InteractionHand hand) {
        final ItemStack stack = player.getItemInHand(hand);
        if (stack.nextDamageWillBreak() || player.getCooldowns().isOnCooldown(stack)) {
            return InteractionResult.FAIL;
        }

        player.startUsingItem(hand);

        return InteractionResult.CONSUME;
    }

    @Override
    public @NonNull Projectile asProjectile(final @NonNull Level level, final Position position, final ItemStack stack, final @NonNull Direction direction) {
        final SpearOfAresEntity spear = new SpearOfAresEntity(level, position.x(), position.y(), position.z(), stack.copyWithCount(1));
        spear.pickup = AbstractArrow.Pickup.DISALLOWED;
        return spear;
    }

    @Override
    public void createGeoRenderer(final Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private SpearOfAresItemRenderer renderer;

            @Override
            public GeoItemRenderer<?> getGeoItemRenderer() {
                if (renderer == null) {
                    renderer = new SpearOfAresItemRenderer();
                }

                return renderer;
            }

        });

    }

    @Override
    public void registerControllers(final AnimatableManager.@NonNull ControllerRegistrar controllers) {
        ;;
    }

    @Override
    public @NonNull AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

}