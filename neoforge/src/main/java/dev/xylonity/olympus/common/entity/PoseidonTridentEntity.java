package dev.xylonity.olympus.common.entity;

import dev.xylonity.olympus.registry.OlympusEntities;
import dev.xylonity.olympus.registry.OlympusItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jspecify.annotations.NonNull;

public final class PoseidonTridentEntity extends ThrownTrident {

    private static final EntityDataAccessor<Boolean> RETURNING = SynchedEntityData.defineId(PoseidonTridentEntity.class, EntityDataSerializers.BOOLEAN);

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
        final ItemStack projectileStack = stack.copy();
        setPickupItemStack(projectileStack);
        applyComponentsFromItemStack(projectileStack);
    }

    @Override
    protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
        super.defineSynchedData(entityData);
        entityData.define(RETURNING, false);
    }

    @Override
    public void tick() {
        // Returns after 20 ticks
        if (!level().isClientSide() && !isReturning() && inGroundTime >= 20) {
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
                spawnAtLocation(serverLevel, getPickupItem(), 0.1F);
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
        super.onHitEntity(hitResult);

        final Entity target = hitResult.getEntity();
        if (level() instanceof ServerLevel serverLevel && !target.is(EntityType.ENDERMAN)) {
            summonChannelingLightning(serverLevel, target.position(), target.blockPosition(), target);
        }

    }

    @Override
    protected void hitBlockEnchantmentEffects(final ServerLevel level, final BlockHitResult hitResult, final ItemStack weapon) {
        super.hitBlockEnchantmentEffects(level, hitResult, weapon);
        final BlockPos hitPos = hitResult.getBlockPos();
        if (level.getBlockState(hitPos).is(BlockTags.LIGHTNING_RODS)) {
            summonChannelingLightning(level, hitPos.clampLocationWithin(hitResult.getLocation()), hitPos, this);
        }

    }

    private void summonChannelingLightning(final ServerLevel level, final Vec3 position, final BlockPos skyCheckPosition, final Entity soundSource) {
        final Holder<Enchantment> channeling = level.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.CHANNELING);
        if (getWeaponItem().getEnchantmentLevel(channeling) <= 0 || !level.isThundering() || !level.canSeeSky(skyCheckPosition)) {
            return;
        }

        final LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
        if (getOwner() instanceof ServerPlayer serverPlayer) {
            lightning.setCause(serverPlayer);
        }

        lightning.snapTo(position.x, position.y, position.z, 0.0F, 0.0F);
        level.addFreshEntity(lightning);
        if (!soundSource.isSilent()) {
            level.playSound(null, position.x, position.y, position.z, SoundEvents.TRIDENT_THUNDER, soundSource.getSoundSource(), 5.0F, 1.0F);
        }

    }

    public boolean isReturning() {
        return entityData.get(RETURNING);
    }

    @Override
    protected @NonNull ItemStack getDefaultPickupItem() {
        return new ItemStack(OlympusItems.POSEIDON_TRIDENT.get());
    }

    @Override
    protected void readAdditionalSaveData(final ValueInput input) {
        super.readAdditionalSaveData(input);
        entityData.set(RETURNING, input.getBooleanOr("Returning", false));
    }

    @Override
    protected void addAdditionalSaveData(final ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("Returning", isReturning());
    }

}