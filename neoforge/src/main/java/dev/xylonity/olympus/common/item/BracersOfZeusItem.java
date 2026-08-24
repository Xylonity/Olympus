package dev.xylonity.olympus.common.item;

import dev.xylonity.olympus.config.OlympusConfig;
import dev.xylonity.olympus.network.payload.LightningBoltPayload;
import dev.xylonity.olympus.registry.OlympusDamageTypes;
import dev.xylonity.olympus.registry.OlympusItems;
import dev.xylonity.olympus.registry.OlympusMobEffects;
import dev.xylonity.olympus.registry.OlympusParticles;
import dev.xylonity.olympus.registry.OlympusSounds;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosSlotTypes;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class BracersOfZeusItem extends Item implements ICurioItem {

    public BracersOfZeusItem(final Properties properties) {
        super(properties);
    }

    @Override
    public boolean canEquip(final SlotContext slotContext, final ItemStack stack) {
        return CuriosSlotTypes.Preset.BRACELET.id().equals(slotContext.identifier());
    }

    @Override
    public boolean canEquipFromUse(final SlotContext slotContext, final ItemStack stack) {
        return canEquip(slotContext, stack);
    }

    /// Applies the special effect of the bracers (thunder damage)
    public static void tryActivateAbility(Mob firstTarget, ServerPlayer player, DamageSource triggeringSource, float healthDamage) {
        // Only applied by the bracers damage type
        if (!(firstTarget.level() instanceof ServerLevel level) || triggeringSource.is(OlympusDamageTypes.LIGHTNING) || healthDamage <= 0) {
            return;
        }

        // Checks if the bracers of zeus are equipped
        final Optional<ItemStack> equippedBracers = CuriosApi.getCuriosInventory(player)
                .flatMap(handler -> handler.findFirstCurio(OlympusItems.BRACERS_OF_ZEUS.get()))
                .map(SlotResult::stack);
        if (equippedBracers.isEmpty() || player.getCooldowns().isOnCooldown(equippedBracers.get())) {
            return;
        }

        // Cooldown
        final int cooldownTicks = Math.max(0, (int) Math.round(OlympusConfig.INSTANCE.zeusBracersCooldownSeconds.get() * 20.0D));
        if (cooldownTicks > 0) {
            player.getCooldowns().addCooldown(equippedBracers.get(), cooldownTicks);
        }

        // Damage accumulators
        final float lightningDamage = OlympusConfig.INSTANCE.zeusBracersDamage.get().floatValue();
        final DamageSource damageSource = level.damageSources().source(OlympusDamageTypes.LIGHTNING, player);
        final Set<Integer> struckEntities = new HashSet<>();
        struckEntities.add(firstTarget.getId());

        // First lightning bolt from the sky to the hit entity
        final Vec3 ground = firstTarget.position();
        final Vec3 sky = new Vec3(firstTarget.getX(), level.getMaxY() + 8, firstTarget.getZ());
        strike(level, player, firstTarget, sky, ground, lightningDamage, damageSource, true);

        // For each entity nearby, in chain (normal iterator)
        Mob current = firstTarget;
        for (int jump = 0; jump < 3; jump++) {
            final Mob next = findNextTarget(level, player, current, struckEntities);
            // Instance checking also checks if the instance is not null, so double condition here
            if (!(next instanceof Enemy)) {
                break;
            }

            // Won't affect the same entity again
            struckEntities.add(next.getId());
            // Lightning bolt
            strike(level, player, next, midpoint(current), midpoint(next), lightningDamage * 0.5F, damageSource, false);
            current = next;
        }

    }

    private static Mob findNextTarget(ServerLevel level, ServerPlayer player, Mob origin, Set<Integer> struckEntities) {
        return level.getEntitiesOfClass(Mob.class, origin.getBoundingBox().inflate(8),
                        candidate -> candidate.isAlive() && !struckEntities.contains(candidate.getId()) && !player.isAlliedTo(candidate)
                )
                .stream()
                .min(Comparator.comparingDouble(origin::distanceToSqr))
                .orElse(null);
    }

    private static void strike(ServerLevel level, ServerPlayer player, Mob target, Vec3 start, Vec3 end, float damage, DamageSource damageSource, boolean skyStrike) {
        // Lightning bolt particle and sound
        PacketDistributor.sendToPlayersNear(level, null, end.x, end.y, end.z, 128, new LightningBoltPayload(start, end, skyStrike));
        level.playSound(null, end.x, end.y, end.z, OlympusSounds.ZEUS_BRACERS_LIGHTNING_STRIKE.get(), SoundSource.PLAYERS, skyStrike ? 2.5F : 0.8F, 0.9F + level.getRandom().nextFloat() * 0.2F);

        // Additional particles around
        spawnLightningSparks(level, target);

        // Applying the actual damage and status effect
        if (target.isAlive()) {
            target.hurtServer(level, damageSource, damage);
            target.addEffect(new MobEffectInstance(OlympusMobEffects.LIGHTNING_STUN, player.getRandom().nextInt(30, 70)), player);
        }

    }

    private static void spawnLightningSparks(final ServerLevel level, final Mob target) {
        final RandomSource random = level.getRandom();
        final int count = 5 + random.nextInt(6);
        final double horizontalRadius = target.getBbWidth() * 0.5D + 0.25D;
        final double height = Math.max(0.5D, target.getBbHeight());

        // Conical distribution (looking upwards)
        for (int index = 0; index < count; index++) {
            final double angle = random.nextDouble() * Math.PI * 2.0D;
            final double radius = horizontalRadius * (0.65D + random.nextDouble() * 0.5D);
            level.sendParticles(OlympusParticles.LIGHTNING_SPARKS.get(), target.getX() + Math.cos(angle) * radius, target.getY() + random.nextDouble() * height, target.getZ() + Math.sin(angle) * radius, 1, 0, 0, 0, 0);
        }

    }

    private static Vec3 midpoint(final Mob entity) {
        return entity.position().add(0, entity.getBbHeight() * 0.5, 0);
    }

}