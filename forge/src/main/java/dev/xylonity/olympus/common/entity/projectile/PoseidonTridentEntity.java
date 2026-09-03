package dev.xylonity.olympus.common.entity.projectile;

import dev.xylonity.knightlib.api.animation.KnightLibAnimatable;
import dev.xylonity.knightlib.api.animation.KnightLibAnimationControllerRegistrar;
import dev.xylonity.knightlib.api.animation.KnightLibAnimationHandler;
import dev.xylonity.olympus.config.OlympusConfig;
import dev.xylonity.olympus.registry.OlympusEntities;
import dev.xylonity.olympus.registry.OlympusItems;
import dev.xylonity.olympus.registry.OlympusParticles;
import dev.xylonity.olympus.registry.OlympusSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.Level;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class PoseidonTridentEntity extends ThrownTrident implements KnightLibAnimatable {

    private final KnightLibAnimationHandler animations = KnightLibAnimationHandler.of(this);

    private static final EntityDataAccessor<Boolean> RETURNING = SynchedEntityData.defineId(PoseidonTridentEntity.class, EntityDataSerializers.BOOLEAN);

    private ItemStack tridentStack = new ItemStack(OlympusItems.POSEIDON_TRIDENT.get());

    private boolean hasSplashed;

    public PoseidonTridentEntity(final EntityType<? extends PoseidonTridentEntity> entityType, final Level level) {
        super(entityType, level);
    }

    public PoseidonTridentEntity(final Level level, final LivingEntity owner, final ItemStack stack) {
        this(OlympusEntities.POSEIDON_TRIDENT.get(), level);
        init(stack);
        setOwner(owner);
        setPos(owner.getX(), owner.getEyeY() - 0.1F, owner.getZ());
    }

    public PoseidonTridentEntity(final Level level, final double x, final double y, final double z, final ItemStack stack) {
        this(OlympusEntities.POSEIDON_TRIDENT.get(), level);
        init(stack);
        setPos(x, y, z);
    }

    private void init(final ItemStack stack) {
        tridentStack = stack.copy();
        tridentStack.setCount(1);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(RETURNING, false);
    }

    @Override
    public void tick() {
        // Returns after 20 ticks
        if (!level().isClientSide && !isReturning() && inGroundTime >= 20) {
            entityData.set(RETURNING, true);
        }

        if (isReturning()) {
            returnToOwner();
            if (!isAlive()) {
                return;
            }

        }

        super.tick();
    }

    private void returnToOwner() {
        final Entity owner = getOwner();
        if (!isValidReturnOwner(owner)) {
            if (level() instanceof ServerLevel serverLevel && pickup == AbstractArrow.Pickup.ALLOWED) {
                spawnAtLocation(getPickupItem(), 0.1F);
            }

            discard();

            return;
        }

        if (!(owner instanceof Player) && position().distanceTo(owner.getEyePosition()) < owner.getBbWidth() + 1.0) {
            discard();
            return;
        }

        setNoPhysics(true);

        final Vec3 direction = owner.getEyePosition().subtract(position());
        setPosRaw(getX(), getY() + direction.y * 0.015, getZ());
        setDeltaMovement(getDeltaMovement().scale(0.95).add(direction.normalize().scale(0.075)));
        if (clientSideReturnTridentTickCount == 0) {
            playSound(SoundEvents.TRIDENT_RETURN, 10.0F, 1.0F);
        }

        clientSideReturnTridentTickCount++;
    }

    private static boolean isValidReturnOwner(final Entity owner) {
        return owner != null && owner.isAlive() && (!(owner instanceof ServerPlayer serverPlayer) || !serverPlayer.isSpectator());
    }

    @Override
    protected void onHitEntity(final EntityHitResult hitResult) {
        final Entity target = hitResult.getEntity();
        final boolean firstImpact = !hasSplashed;
        hasSplashed = true;

        // On entity hit
        final Entity currentOwner = getOwner();
        final DamageSource damageSource = damageSources().trident(this, currentOwner == null ? this : currentOwner);
        boolean targetWasHurt = false;
        if (level() instanceof ServerLevel serverLevel) {
            // Applies the same damage as the trident item
            final float projectileDamage = (float) OlympusConfig.POSEIDON_TRIDENT_PROJECTILE_DAMAGE;
            final float damage = projectileDamage + (target instanceof LivingEntity livingTarget ? EnchantmentHelper.getDamageBonus(getTridentStack(), livingTarget.getMobType()) : 0.0F);
            if (firstImpact) {
                // Splash particle
                createSplash(serverLevel, target.getBoundingBox().getCenter(), target);
            }

            if (target.getType() != EntityType.ENDERMAN) {
                summonChannelingLightning(serverLevel, target.position(), target.blockPosition(), target);
            }

            targetWasHurt = target.hurt(damageSource, damage);
        }

        if (targetWasHurt) {
            if (target.getType() == EntityType.ENDERMAN) {
                return;
            }

            if (level() instanceof ServerLevel serverLevel) {
                if (target instanceof LivingEntity livingTarget && currentOwner instanceof LivingEntity livingOwner) {
                    EnchantmentHelper.doPostHurtEffects(livingTarget, livingOwner);
                    EnchantmentHelper.doPostDamageEffects(livingOwner, livingTarget);
                }

                if (target instanceof LivingEntity livingTarget) {
                    // Extra knockback
                    applyKnockback(serverLevel, livingTarget, damageSource);
                }

            }

            if (target instanceof LivingEntity livingTarget) {
                doPostHurtEffects(livingTarget);
            }

        }

        setDeltaMovement(getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));

        playSound(SoundEvents.TRIDENT_HIT, 1.0F, 1.0F);
    }

    @Override
    protected @Nullable EntityHitResult findHitEntity(final Vec3 from, final Vec3 to) {
        return hasSplashed ? null : super.findHitEntity(from, to);
    }

    @Override
    protected void onHitBlock(final BlockHitResult hitResult) {
        if (level() instanceof ServerLevel level) {
            final BlockPos hitPos = hitResult.getBlockPos();
            if (level.getBlockState(hitPos).is(Blocks.LIGHTNING_ROD)) {
                summonChannelingLightning(level, hitResult.getLocation(), hitPos, this);
            }

            if (!hasSplashed) {
                hasSplashed = true;
                createSplash(level, hitResult.getLocation(), null);
            }

        }

        super.onHitBlock(hitResult);
    }

    private void createSplash(final ServerLevel level, final Vec3 center, final @Nullable Entity directTarget) {
        final boolean underwater = isWaterImpact(center, directTarget);
        final double splashRadius = OlympusConfig.POSEIDON_TRIDENT_SPLASH_RADIUS;
        // Particles
        spawnSplashEffects(level, center, directTarget, underwater, splashRadius);

        final Entity currentOwner = getOwner();
        final DamageSource damageSource = damageSources().trident(this, currentOwner == null ? this : currentOwner);
        final AABB bounds = new AABB(
                center.x - splashRadius, center.y - splashRadius, center.z - splashRadius,
                center.x + splashRadius, center.y + splashRadius, center.z + splashRadius
        );

        // Damage to the entities in the radius
        for (final LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, bounds, LivingEntity::isAlive)) {
            if (!canSplashAffect(target, directTarget, currentOwner)) {
                continue;
            }

            final Vec3 targetCenter = target.getBoundingBox().getCenter();
            final double distance = targetCenter.distanceTo(center);
            if (distance > splashRadius) {
                continue;
            }

            target.hurt(damageSource, (float) OlympusConfig.POSEIDON_TRIDENT_SPLASH_DAMAGE);
            applySplashKnockback(level, target, targetCenter.subtract(center), distance, damageSource, underwater, splashRadius);
        }

    }

    private boolean isWaterImpact(final Vec3 center, final @Nullable Entity entity) {
        final boolean isWater = level().getFluidState(BlockPos.containing(center)).is(FluidTags.WATER);
        if (entity != null) {
            return entity.isUnderWater() || isWater;
        }

        return isInWater() || isWater;
    }

    private void spawnSplashEffects(final ServerLevel level, final Vec3 center, final @Nullable Entity directTarget, final boolean underwater, final double splashRadius) {
        if (underwater) {
            level.sendParticles(OlympusParticles.TRIDENT_UNDERWATER_SPLASH.get(), center.x, center.y, center.z, 0, splashRadius, 0, 0, 1);
            level.sendParticles(ParticleTypes.BUBBLE, center.x, center.y, center.z, 72, 0.2, 0.2, 0.2, 0.18);
            level.playSound(null, center.x, center.y, center.z, SoundEvents.ELDER_GUARDIAN_HURT, getSoundSource(), 1.4F, 1.0F);
            return;
        }

        final double effectY = directTarget == null ? center.y + 0.25D : center.y;
        level.sendParticles(OlympusParticles.TRIDENT_SPLASH_OF_WATER.get(), center.x, effectY, center.z, 0, splashRadius, 0, 0, 1);
        level.sendParticles(OlympusParticles.TRIDENT_WATER_DROP.get(), center.x, effectY + 0.15, center.z, 32, 0.35, 0.28, 0.35, 0.32);

        level.playSound(null, center.x, effectY, center.z, OlympusSounds.POSEIDONS_TRIDENT_HIT.get(), getSoundSource(), 0.85F, 1.0F);
    }

    private static boolean canSplashAffect(final LivingEntity target, final @Nullable Entity directTarget, final @Nullable Entity owner) {
        if (target == directTarget || target == owner || target.isSpectator()) {
            return false;
        }

        return !(owner instanceof Player player) || !(target instanceof Player targett) || player.canHarmPlayer(targett);
    }

    private void applyKnockback(final ServerLevel level, final LivingEntity target, final DamageSource damageSource) {
        final float knockback = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.KNOCKBACK, getTridentStack());
        if (knockback <= 0.0F) {
            return;
        }

        final double resistance = Math.max(0, 1 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
        final Vec3 direction = getDeltaMovement().multiply(1, 0, 1).normalize();
        if (direction.lengthSqr() > 0) {
            final Vec3 impulse = direction.scale(knockback * 0.6D * resistance);
            target.push(impulse.x, 0.1, impulse.z);
        }

    }

    private void applySplashKnockback(final ServerLevel level, final LivingEntity target, final Vec3 offset, final double distance, final DamageSource damageSource, final boolean underwater, final double splashRadius) {
        final float knockback = 1.35F + EnchantmentHelper.getItemEnchantmentLevel(Enchantments.KNOCKBACK, getTridentStack());
        final double resistance = Math.max(0, 1f - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
        final double falloff = 1f - distance / splashRadius;
        final double strength = knockback * 0.6 * resistance * (0.35 + falloff * 0.65);
        if (strength <= 0) {
            return;
        }

        if (underwater) {
            final Vec3 direction = offset.lengthSqr() > 1.0E-6D ? offset.normalize() : getDeltaMovement().normalize();
            final Vec3 impulse = direction.scale(strength);
            target.push(impulse.x, impulse.y, impulse.z);
            return;
        }

        Vec3 direction = offset.multiply(1, 0, 1);
        if (direction.lengthSqr() <= 1.0E-6D) {
            direction = getDeltaMovement().multiply(1, 0, 1);
        }

        direction = direction.normalize().scale(strength);
        target.push(direction.x, 0.18D + falloff * 0.12D, direction.z);
    }

    private void summonChannelingLightning(final ServerLevel level, final Vec3 position, final BlockPos skyCheckPosition, final Entity soundSource) {
        if (!EnchantmentHelper.hasChanneling(getTridentStack()) || !level.isThundering() || !level.canSeeSky(skyCheckPosition)) {
            return;
        }

        final LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);
        if (lightning == null) {
            return;
        }

        if (getOwner() instanceof ServerPlayer serverPlayer) {
            lightning.setCause(serverPlayer);
        }

        lightning.moveTo(position);
        level.addFreshEntity(lightning);
        if (!soundSource.isSilent()) {
            level.playSound(null, position.x, position.y, position.z, SoundEvents.TRIDENT_THUNDER, soundSource.getSoundSource(), 5.0F, 1.0F);
        }

    }

    public boolean isReturning() {
        return entityData.get(RETURNING);
    }

    @Override
    protected @NonNull ItemStack getPickupItem() {
        return tridentStack.copy();
    }

    public ItemStack getTridentStack() {
        return tridentStack;
    }

    @Override
    public void readAdditionalSaveData(final CompoundTag input) {
        super.readAdditionalSaveData(input);
        entityData.set(RETURNING, input.getBoolean("Returning"));
        hasSplashed = input.getBoolean("HasSplashed");
        if (input.contains("Weapon", 10)) {
            tridentStack = ItemStack.of(input.getCompound("Weapon"));
        }

    }

    @Override
    public void addAdditionalSaveData(final CompoundTag output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("Returning", isReturning());
        output.putBoolean("HasSplashed", hasSplashed);
        output.put("Weapon", tridentStack.save(new CompoundTag()));
    }

    @Override
    public KnightLibAnimationHandler getAnimationHandler() {
        return animations;
    }

}
