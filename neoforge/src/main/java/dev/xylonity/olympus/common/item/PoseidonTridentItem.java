package dev.xylonity.olympus.common.item;

import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.util.GeckoLibUtil;
import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.client.item.renderer.PoseidonTridentItemRenderer;
import dev.xylonity.olympus.common.entity.PoseidonTridentEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public final class PoseidonTridentItem extends TridentItem implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final AttributeModifier SWIM_SPEED_MODIFIER = new AttributeModifier(Olympus.of("poseidon_trident_swim_speed"), 0.4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");

    public PoseidonTridentItem(final Properties properties) {
        super(properties);
        GeoItem.registerSyncedAnimatable(this);
    }

    public static @NonNull ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 9D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.8D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(NeoForgeMod.SWIM_SPEED, SWIM_SPEED_MODIFIER, EquipmentSlotGroup.HAND)
                .build();
    }

    @Override
    public boolean releaseUsing(final ItemStack stack, final Level level, final LivingEntity user, final int remainingUseDuration) {
        if (!(user instanceof Player player)) {
            return false;
        }

        final int useTicks = getUseDuration(stack, user) - remainingUseDuration;
        if (useTicks < THROW_THRESHOLD_TIME || stack.nextDamageWillBreak()) {
            return false;
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }

        stack.hurtWithoutBreaking(1, player);
        final ItemStack poseidonTrident = stack.consumeAndReturn(1, player);
        final PoseidonTridentEntity trident = Projectile.spawnProjectileFromRotation(
                PoseidonTridentEntity::new, serverLevel, poseidonTrident, player, 0.0F, PROJECTILE_SHOOT_POWER, 1.0F
        );
        if (player.hasInfiniteMaterials()) {
            trident.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        }

        level.playSound(null, trident, SoundEvents.TRIDENT_THROW.value(), SoundSource.PLAYERS, 1.0F, 1.0F);

        return true;
    }

    @Override
    public InteractionResult use(final Level level, final Player player, final InteractionHand hand) {
        final ItemStack stack = player.getItemInHand(hand);
        if (stack.nextDamageWillBreak()) {
            return InteractionResult.FAIL;
        }

        player.startUsingItem(hand);

        return InteractionResult.CONSUME;
    }

    @Override
    public Projectile asProjectile(final Level level, final Position position, final ItemStack stack, final Direction direction) {
        final PoseidonTridentEntity trident = new PoseidonTridentEntity(level, position.x(), position.y(), position.z(), stack.copyWithCount(1));

        trident.pickup = AbstractArrow.Pickup.ALLOWED;

        return trident;
    }

    @Override
    public boolean supportsEnchantment(final ItemStack stack, final Holder<Enchantment> enchantment) {
        return enchantment.is(Enchantments.CHANNELING)
                || enchantment.is(Enchantments.IMPALING)
                || enchantment.is(Enchantments.MENDING)
                || enchantment.is(Enchantments.KNOCKBACK)
                || enchantment.is(Enchantments.UNBREAKING)
                || enchantment.is(Enchantments.LOOTING);
    }

    @Override
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("idle", state -> state.setAndContinue(IDLE)));
    }

    @Override
    public void createGeoRenderer(final Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private PoseidonTridentItemRenderer renderer;

            @Override
            public GeoItemRenderer<?> getGeoItemRenderer() {
                if (renderer == null) {
                    renderer = new PoseidonTridentItemRenderer();
                }

                return renderer;
            }

        });

    }

    @Override
    public @NonNull AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

}
