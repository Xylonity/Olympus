package dev.xylonity.olympus.common.item;

import dev.xylonity.olympus.config.OlympusConfig;
import dev.xylonity.olympus.common.util.OlympusTooltip;
import dev.xylonity.olympus.network.payload.LyreMusicPayload;
import dev.xylonity.olympus.network.OlympusNetwork;
import dev.xylonity.olympus.registry.OlympusParticles;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;

public final class AphroditeLyreItem extends Item {

    public static final int DURATION_TICKS = 8 * 20;

    private static final Map<UUID, Set<UUID>> ENTITIES = new ConcurrentHashMap<>();

    public AphroditeLyreItem(final Properties properties) {
        super(properties);
    }

    @Override
    public @NonNull InteractionResultHolder<ItemStack> use(final @NonNull Level level, final @NonNull Player player, final @NonNull InteractionHand hand) {
        // No direct use
        final ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(stack.getItem())) {
            return InteractionResultHolder.fail(stack);
        }

        // Using the item
        player.startUsingItem(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            ENTITIES.put(serverPlayer.getUUID(), new HashSet<>());
            // Enables the lyre music
            OlympusNetwork.sendToTrackingAndSelf(serverPlayer, LyreMusicPayload.TYPE, new LyreMusicPayload(serverPlayer.getId(), true));
        }

        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(final @NonNull Level level, final @NonNull LivingEntity user, final @NonNull ItemStack stack, final int remainingUseDuration) {
        if (!(level instanceof ServerLevel serverLevel) || !(user instanceof ServerPlayer player)) {
            return;
        }

        final int useTicks = DURATION_TICKS - remainingUseDuration;
        // Breed nearby entities
        if (useTicks % 5 == 0) {
            breedNearby(serverLevel, player);
        }
        // particles
        if (useTicks % 10 == 0) {
            particles(serverLevel, player);
        }

    }

    @Override
    public void releaseUsing(final @NonNull ItemStack stack, final @NonNull Level level, final @NonNull LivingEntity user, final int remainingUseDuration) {
        stopPlaying(stack, user);
    }

    @Override
    public @NonNull ItemStack finishUsingItem(final @NonNull ItemStack stack, final @NonNull Level level, final @NonNull LivingEntity user) {
        stopPlaying(stack, user);
        return stack;
    }

    @Override
    public int getUseDuration(final @NonNull ItemStack stack) {
        return DURATION_TICKS;
    }

    @Override
    public @NonNull UseAnim getUseAnimation(final @NonNull ItemStack stack) {
        return UseAnim.BOW;
    }

    private static void stopPlaying(final ItemStack stack, final LivingEntity user) {
        if (!(user instanceof ServerPlayer player)) {
            return;
        }

        ENTITIES.remove(player.getUUID());
        if (player.getCooldowns().isOnCooldown(stack.getItem())) {
            return;
        }

        final int cooldownTicks = OlympusConfig.secondsToTicks(OlympusConfig.APHRODITE_LYRE_COOLDOWN_SECONDS);
        if (cooldownTicks > 0) {
            player.getCooldowns().addCooldown(stack.getItem(), cooldownTicks);
        }

        // Stops the music
        OlympusNetwork.sendToTrackingAndSelf(player, LyreMusicPayload.TYPE, new LyreMusicPayload(player.getId(), false));
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        OlympusTooltip.append(tooltip::add, "aphrodite_lyre", 0xE8A0BF,
                OlympusTooltip.ability(1,
                        OlympusTooltip.property("radius", OlympusTooltip.number(OlympusConfig.APHRODITE_LYRE_BREEDING_RADIUS)),
                        OlympusTooltip.property("cooldown", OlympusTooltip.seconds(OlympusConfig.APHRODITE_LYRE_COOLDOWN_SECONDS))
                ));

    }

    private static void breedNearby(final ServerLevel level, final ServerPlayer player) {
        final double radius = OlympusConfig.APHRODITE_LYRE_BREEDING_RADIUS;
        final Set<UUID> entities = ENTITIES.computeIfAbsent(player.getUUID(), ignored -> new HashSet<>());
        level.getEntitiesOfClass(Animal.class, player.getBoundingBox().inflate(radius),
                animal -> animal.isAlive() && !animal.isBaby() && animal.canFallInLove() && !entities.contains(animal.getUUID()) && animal.distanceToSqr(player) <= radius * radius
        ).forEach(animal -> {
            animal.setInLove(player);
            entities.add(animal.getUUID());
        });

    }

    private static void particles(final ServerLevel level, final ServerPlayer player) {
        final RandomSource random = level.getRandom();
        final double angle = random.nextDouble() * Math.PI * 2;
        final double radius = 0.7 + random.nextDouble() * 1.1;
        level.sendParticles(OlympusParticles.LYRE_NOTE.get(),
                player.getX() + Math.cos(angle) * radius, player.getY() + 0.25 + random.nextDouble() * 2.25, player.getZ() + Math.sin(angle) * radius,
                0, (random.nextDouble() - 0.5) * 0.006, 0.012 + random.nextDouble() * 0.018, (random.nextDouble() - 0.5) * 0.006, 1
        );

    }

}
