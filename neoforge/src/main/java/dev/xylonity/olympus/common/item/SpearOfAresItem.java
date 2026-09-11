package dev.xylonity.olympus.common.item;

import dev.xylonity.knightlib.api.item.KnightLibRenderedItem;
import dev.xylonity.olympus.client.item.renderer.SpearOfAresItemRenderer;
import dev.xylonity.olympus.client.item.SpearAttackTransforms;
import dev.xylonity.olympus.common.entity.projectile.SpearOfAresEntity;
import dev.xylonity.olympus.common.util.OlympusTooltip;
import dev.xylonity.olympus.common.entity.projectile.SummoningSpearsEntity;
import dev.xylonity.olympus.config.OlympusConfig;
import dev.xylonity.olympus.network.OlympusNetwork;
import dev.xylonity.olympus.registry.OlympusItems;
import dev.xylonity.olympus.registry.OlympusParticles;
import dev.xylonity.olympus.registry.OlympusSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.HumanoidArm;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class SpearOfAresItem extends TridentItem implements KnightLibRenderedItem {

    public static final int SWING_DURATION = 10;

    public static ResourceLocation ARES_SPEAR_REACH_UUID = ResourceLocation.fromNamespaceAndPath("olympus", "ares_spear_reach");

    private static final String TAG_SPECIAL_ABILITY_CHARGED = "olympus_special_ability_charged";
    private static final String TAG_SPECIAL_ABILITY_COOLDOWN_END = "olympus_special_ability_cooldown_end";
    private static final String TAG_PLAYER_SPECIAL_FALL = "olympus_ares_special_fall";

    public SpearOfAresItem(final Properties properties) {
        super(properties);
    }

    @Override
    public boolean shouldCauseReequipAnimation(final ItemStack oldStack, final ItemStack newStack, final boolean slotChanged) {
        // Otherwise the item will play the animation when using the spear
        return slotChanged || !ItemStack.isSameItem(oldStack, newStack);
    }

    @Override
    public void initializeClient(final Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {

            private BlockEntityWithoutLevelRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new SpearOfAresItemRenderer();
                }
                return renderer;
            }

            @Override
            public boolean applyForgeHandTransform(final com.mojang.blaze3d.vertex.PoseStack poseStack, final LocalPlayer player, final HumanoidArm arm, final ItemStack itemInHand, final float partialTick, final float equipProgress, final float swingProgress) {
                if (player.isUsingItem() && player.getUseItem() == itemInHand) {
                    return false;
                }

                // Applies the display mutations from mc26+
                SpearAttackTransforms.applyFirstPersonBase(poseStack, arm, equipProgress);
                SpearAttackTransforms.applyFirstPersonAttack(poseStack, arm, swingProgress);

                return true;
            }

        });

    }

    @Override
    public boolean hurtEnemy(final ItemStack stack, final LivingEntity target, final LivingEntity attacker) {
        final boolean result = super.hurtEnemy(stack, target, attacker);
        if (!attacker.level().isClientSide) {
            attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), OlympusSounds.ARES_SPEAR_HIT.get(), attacker.getSoundSource(), 1.0F, 1.0F);
        }

        return result;
    }

    @Override
    public boolean canAttackBlock(final BlockState state, final Level level, final BlockPos pos, final Player player) {
        return false;
    }

    public static boolean isSpecialAbilityCharged(final ItemStack stack) {
        return OlympusConfig.ARES_SPEAR_DESCENT_ENABLED && stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean(TAG_SPECIAL_ABILITY_CHARGED);
    }

    public static void setSpecialAbilityCharged(final ItemStack stack, final Player player, final boolean charged) {
        if (charged) {
            if (!isSpecialAbilityCharged(stack)) {
                player.level().playSound(null, player.blockPosition(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1.4f, 1f);
            }

            CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putBoolean(TAG_SPECIAL_ABILITY_CHARGED, true));
        }
        else {
            CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.remove(TAG_SPECIAL_ABILITY_CHARGED));
        }

        if (charged) {
            stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(1));
        }
        else {
            stack.remove(DataComponents.CUSTOM_MODEL_DATA);
        }

    }

    public static boolean isSpecialAbilityReady(final ItemStack stack, final Level level) {
        final long cooldownEnd = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getLong(TAG_SPECIAL_ABILITY_COOLDOWN_END);
        return level.getGameTime() >= cooldownEnd;
    }

    public static void startSpecialAbilityCooldown(final ItemStack stack, final Level level) {
        final int cooldownTicks = OlympusConfig.secondsToTicks(OlympusConfig.ARES_SPEAR_ABILITY_COOLDOWN_SECONDS);
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putLong(TAG_SPECIAL_ABILITY_COOLDOWN_END, level.getGameTime() + cooldownTicks));

    }

    public static void chargeSpecialAbilityForKill(final ServerPlayer player, final DamageSource damageSource) {
        if (!OlympusConfig.ARES_SPEAR_DESCENT_ENABLED) {
            return;
        }

        // On entity kill, checks if the spear was the reason
        final ItemStack spear = findSpearUsedForKill(player, damageSource);
        if (!spear.isEmpty()) {
            setSpecialAbilityCharged(spear, player, true);
        }

    }

    public static void updateSpecialFall(final ServerPlayer player) {
        if (!OlympusConfig.ARES_SPEAR_DESCENT_ENABLED) {
            player.getPersistentData().remove(TAG_PLAYER_SPECIAL_FALL);
            return;
        }

        // If the player is executing the special ability
        if (isActiveFallActive(player)) {
            // On ground hit
            if (player.onGround()) {
                finishActiveFallAbility(player);
                return;
            }

            // On ground hit (if it's water or if the player starts flying), the ability is canceled
            if (player.isInWater() || player.isFallFlying() || player.getAbilities().flying) {
                player.getPersistentData().remove(TAG_PLAYER_SPECIAL_FALL);
                return;
            }

            // Extra speed for the player
            acceleratePlayer(player);

            return;
        }

        // Checks if the player should activate the active ability
        if (player.onGround() || !tryActiveAbility(player, player.fallDistance)) {
            return;
        }

        acceleratePlayer(player);
    }

    public static boolean handleSpecialLanding(final ServerPlayer player, final double fallDistance) {
        if (!OlympusConfig.ARES_SPEAR_DESCENT_ENABLED) {
            player.getPersistentData().remove(TAG_PLAYER_SPECIAL_FALL);
            return false;
        }

        if (!isActiveFallActive(player) && !tryActiveAbility(player, fallDistance)) {
            return false;
        }

        return finishActiveFallAbility(player);
    }

    /// Applies the special effect of the helmet of spear (summoning spears come from the ground on ground hit on certain conditions)
    private static boolean tryActiveAbility(ServerPlayer player, double fallDistance) {
        if (!OlympusConfig.ARES_SPEAR_DESCENT_ENABLED || fallDistance < OlympusConfig.ARES_SPEAR_ABILITY_MINIMUM_FALL_DISTANCE || !player.isShiftKeyDown() || player.isInWater() || player.isFallFlying() || player.getAbilities().flying) {
            return false;
        }

        final ItemStack spear = findUsableHeldSpear(player);
        if (spear.isEmpty()) {
            return false;
        }

        // Starts the special ability
        setSpecialAbilityCharged(spear, player, false);
        // The active ability has a different cooldown
        startSpecialAbilityCooldown(spear, player.level());
        player.getPersistentData().putBoolean(TAG_PLAYER_SPECIAL_FALL, true);

        return true;
    }

    private static boolean finishActiveFallAbility(final ServerPlayer player) {
        if (!isActiveFallActive(player) || player.isInWater() || player.isFallFlying() || player.getAbilities().flying) {
            return false;
        }

        player.getPersistentData().remove(TAG_PLAYER_SPECIAL_FALL);
        player.resetFallDistance();

        final ServerLevel level = player.serverLevel();
        final Vec3 pos = player.position();
        level.playSound(null, pos.x, pos.y, pos.z, OlympusSounds.ARES_SPEAR_LANDING.get(), SoundSource.PLAYERS, 1, 1);
        level.addFreshEntity(new SummoningSpearsEntity(level, player));
        spawnLandingParticles(level, pos);

        // Camera shake
        OlympusNetwork.shake(level, pos, 6, 0.9f, 20);

        return true;
    }

    private static void spawnLandingParticles(final ServerLevel level, final Vec3 center) {
        final RandomSource random = level.getRandom();
        final double radius = 3;
        final int particles = 6;
        final BlockPos pos = findParticleGround(level, center.x, center.y, center.z, radius);
        if (pos == null) {
            return;
        }

        for (int cluster = 0; cluster < 20; cluster++) {
            final double angle = random.nextDouble() * Math.PI * 2;
            final double distance = Math.sqrt(random.nextDouble()) * radius;
            final double x = center.x + Math.cos(angle) * distance;
            final double z = center.z + Math.sin(angle) * distance;
            final BlockPos particleGround = findParticleGround(level, x, center.y, z, radius);
            final BlockPos sanitizedPos = particleGround != null ? particleGround : pos;
            final double particleX = particleGround != null ? x : center.x;
            final double particleZ = particleGround != null ? z : center.z;

            final BlockState state = level.getBlockState(sanitizedPos);
            final double y = sanitizedPos.getY() + state.getCollisionShape(level, sanitizedPos).max(Direction.Axis.Y) + 0.05;
            level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state), particleX, y, particleZ, particles, 0.12, 0.04, 0.12, 0.16);
        }

    }

    private static @Nullable BlockPos findParticleGround(final ServerLevel level, final double x, final double y, final double z, final double radius) {
        final int blockX = (int) Math.floor(x);
        final int blockZ = (int) Math.floor(z);

        // Searches a few blocks down so particles follow nearby slopes instead of floating
        for (int blockY = (int) Math.floor(y); blockY >= Math.floor(y - radius); blockY--) {
            final BlockPos blockPos = new BlockPos(blockX, blockY, blockZ);
            if (!level.getBlockState(blockPos).getCollisionShape(level, blockPos).isEmpty()) {
                return blockPos;
            }

        }

        return null;
    }

    private static boolean isActiveFallActive(final ServerPlayer player) {
        // Checked on player tick, whether it is executing the spear special ability or not
        return player.getPersistentData().getBoolean(TAG_PLAYER_SPECIAL_FALL);
    }

    private static void acceleratePlayer(final ServerPlayer player) {
        // Moves the player downwards
        final Vec3 movement = player.getDeltaMovement();
        player.setDeltaMovement(movement.x, Math.min(movement.y, -2), movement.z);
        player.hurtMarked = true;

        final double angle = Math.PI * 2 * (player.tickCount % 10) / 10;
        final double x = player.getX() + Math.cos(angle);
        final double z = player.getZ() + Math.sin(angle);
        player.serverLevel().sendParticles(OlympusParticles.ARES_SPEAR_TRACE.get(), x, player.getY(), z, 1, 0, 0, 0, 0);
        player.serverLevel().sendParticles(OlympusParticles.ARES_SPEAR_TRACE.get(), x, player.getY(), z, 1, 0.1, 0.1, 0.1, 0);
    }

    private static ItemStack findSpearUsedForKill(final ServerPlayer player, final DamageSource damageSource) {
        // If the damage is caused by the thrown entity
        if (damageSource.getDirectEntity() instanceof SpearOfAresEntity thrownSpear) {
            final ItemStack projectileStack = thrownSpear.getSpearStack();
            // If the spear is in the hand
            for (final InteractionHand hand : InteractionHand.values()) {
                final ItemStack stack = player.getItemInHand(hand);
                if (stack.is(OlympusItems.SPEAR_OF_ARES.get()) && ItemStack.isSameItemSameComponents(stack, projectileStack)) {
                    return stack;
                }

            }

            // If the player changed to another slot before the entity died
            final int slotIdx = player.getInventory().findSlotMatchingItem(projectileStack);
            if (slotIdx >= 0) {
                final ItemStack stack = player.getInventory().getItem(slotIdx);
                if (stack.is(OlympusItems.SPEAR_OF_ARES.get())) {
                    return stack;
                }

            }

            return ItemStack.EMPTY;
        }

        // If it's a direct hit (killed with the spear item itself)
        final ItemStack mainHandStack = player.getMainHandItem();
        return damageSource.getDirectEntity() == player && mainHandStack.is(OlympusItems.SPEAR_OF_ARES.get()) ? mainHandStack : ItemStack.EMPTY;
    }

    private static ItemStack findUsableHeldSpear(final ServerPlayer player) {
        for (final InteractionHand hand : InteractionHand.values()) {
            final ItemStack stack = player.getItemInHand(hand);
            if (stack.is(OlympusItems.SPEAR_OF_ARES.get()) && isSpecialAbilityCharged(stack) && isSpecialAbilityReady(stack, player.level())) {
                return stack;
            }

        }

        return ItemStack.EMPTY;
    }

    /// Same code over again {@link PoseidonTridentItem}
    @Override
    public void releaseUsing(final ItemStack stack, final Level level, final LivingEntity user, final int remainingUseDuration) {
        if (!OlympusConfig.ARES_SPEAR_THROW_ENABLED || !(user instanceof Player player)) {
            return;
        }

        final int useTicks = getUseDuration(stack, user) - remainingUseDuration;
        if (useTicks < THROW_THRESHOLD_TIME || stack.getDamageValue() >= stack.getMaxDamage() - 1 || player.getCooldowns().isOnCooldown(stack.getItem())) {
            return;
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!(level instanceof ServerLevel)) {
            return;
        }

        // Reduces the item durability and doesn't delete the stack
        stack.hurtAndBreak(1, player, player.getUsedItemHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        final ItemStack aresSpear = stack.copy();
        aresSpear.setCount(1);
        final SpearOfAresEntity spear = new SpearOfAresEntity(level, player, aresSpear);
        spear.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, SHOOT_POWER, 1.0F);
        spear.pickup = AbstractArrow.Pickup.DISALLOWED;
        level.addFreshEntity(spear);

        final int cooldownTicks = OlympusConfig.secondsToTicks(OlympusConfig.ARES_SPEAR_THROW_COOLDOWN_SECONDS);
        if (cooldownTicks > 0) {
            player.getCooldowns().addCooldown(stack.getItem(), cooldownTicks);
        }

        level.playSound(null, spear, SoundEvents.TRIDENT_THROW.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
        final ItemStack stack = player.getItemInHand(hand);
        if (!OlympusConfig.ARES_SPEAR_THROW_ENABLED || stack.getDamageValue() >= stack.getMaxDamage() - 1 || player.getCooldowns().isOnCooldown(stack.getItem())) {
            return InteractionResultHolder.fail(stack);
        }

        player.startUsingItem(hand);

        return InteractionResultHolder.consume(stack);
    }

    @Override
    public boolean isPrimaryItemFor(final ItemStack stack, final Holder<Enchantment> enchantment) {
        return !enchantment.is(Enchantments.LOYALTY) && !enchantment.is(Enchantments.RIPTIDE) && super.isPrimaryItemFor(stack, enchantment);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        OlympusTooltip.append(tooltip::add, "spear_of_ares", 0xE06C6C,
                OlympusTooltip.abilityIf(OlympusConfig.ARES_SPEAR_DESCENT_ENABLED, 1,
                        OlympusTooltip.property("fall_distance", OlympusTooltip.number(OlympusConfig.ARES_SPEAR_ABILITY_MINIMUM_FALL_DISTANCE)),
                        OlympusTooltip.property("cooldown", OlympusTooltip.seconds(OlympusConfig.ARES_SPEAR_ABILITY_COOLDOWN_SECONDS))
                ),
                OlympusTooltip.abilityIf(OlympusConfig.ARES_SPEAR_THROW_ENABLED, 2,
                        OlympusTooltip.property("projectile_damage", OlympusTooltip.number(OlympusConfig.ARES_SPEAR_PROJECTILE_DAMAGE)),
                        OlympusTooltip.property("cooldown", OlympusTooltip.seconds(OlympusConfig.ARES_SPEAR_THROW_COOLDOWN_SECONDS))
                ),
                OlympusTooltip.abilityIf(OlympusConfig.ARES_SPEAR_THROW_ENABLED && OlympusConfig.ARES_SPEAR_REAPERS_JAVELIN_ENABLED, 3,
                        OlympusTooltip.property("pinned_distance", OlympusTooltip.number(OlympusConfig.ARES_SPEAR_PINNED_ENTITY_DISTANCE)),
                        OlympusTooltip.property("wall_damage", OlympusTooltip.number(OlympusConfig.ARES_SPEAR_WALL_IMPACT_DAMAGE)),
                        OlympusTooltip.property("slowness", OlympusTooltip.seconds(OlympusConfig.ARES_SPEAR_WALL_SLOWNESS_SECONDS))
                ));

    }

    @Override
    public Supplier<Object> rendererFactory() {
        return SpearOfAresItemRenderer::new;
    }

}
