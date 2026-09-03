package dev.xylonity.olympus.common.event;

import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.common.effect.InvisibilityOfHadesEffect;
import dev.xylonity.olympus.common.entity.HarpyEntity;
import dev.xylonity.olympus.common.util.ArtemisArrow;
import dev.xylonity.olympus.common.item.BracersOfZeusItem;
import dev.xylonity.olympus.common.item.HelmetOfHadesItem;
import dev.xylonity.olympus.common.item.HermesSandalsItem;
import dev.xylonity.olympus.common.item.InstrumentsOfHephaestusItem;
import dev.xylonity.olympus.common.item.PersephoneCupItem;
import dev.xylonity.olympus.common.item.SpearOfAresItem;
import dev.xylonity.olympus.config.OlympusConfig;
import dev.xylonity.olympus.registry.OlympusDamageTypes;
import dev.xylonity.olympus.registry.OlympusEntities;
import dev.xylonity.olympus.registry.OlympusItems;
import dev.xylonity.olympus.registry.OlympusMobEffects;
import java.util.List;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.living.PotionColorCalculationEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.entity.projectile.AbstractArrow;

@Mod.EventBusSubscriber(modid = Olympus.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class OlympusServerEvents {

    private static final UUID BASE_ATTACK_DAMAGE_UUID = UUID.fromString("cb3f55d3-645c-4f38-a497-9c13a33db5cf");
    private static final UUID BASE_ATTACK_SPEED_UUID = UUID.fromString("fa233e1c-4180-4865-b01b-bcce9785aca3");
    private static final UUID ARES_SPEAR_REACH_UUID = UUID.fromString("7513f095-dcdb-4e0e-b9ab-8b9514f6ba20");

    @SubscribeEvent
    public static void registerAttributes(final EntityAttributeCreationEvent event) {
        event.put(OlympusEntities.HARPY.get(), HarpyEntity.createAttributes().build());
        event.put(OlympusEntities.ELITE_HARPY.get(), HarpyEntity.createEliteAttributes().build());
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(final SpawnPlacementRegisterEvent event) {
        event.register(OlympusEntities.HARPY.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(OlympusEntities.ELITE_HARPY.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
    }

    @Mod.EventBusSubscriber(modid = Olympus.MOD_ID)
    public static final class ForgeEvents {

        @SubscribeEvent
        public static void onItemAttributeModifiers(final ItemAttributeModifierEvent event) {
            final ItemStack stack = event.getItemStack();

            if (stack.is(OlympusItems.POSEIDON_TRIDENT.get())) {
                if (event.getSlotType() == EquipmentSlot.MAINHAND) {
                    event.removeAttribute(Attributes.ATTACK_DAMAGE);
                    event.removeAttribute(Attributes.ATTACK_SPEED);
                    event.addModifier(
                            Attributes.ATTACK_DAMAGE,
                            new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "olympus.poseidon_trident_damage", OlympusConfig.POSEIDON_TRIDENT_ATTACK_DAMAGE_BONUS, AttributeModifier.Operation.ADDITION)
                    );
                    event.addModifier(
                            Attributes.ATTACK_SPEED,
                            new AttributeModifier(BASE_ATTACK_SPEED_UUID, "olympus.poseidon_trident_speed", -2.8D, AttributeModifier.Operation.ADDITION)
                    );

                }
                if (event.getSlotType() == EquipmentSlot.MAINHAND || event.getSlotType() == EquipmentSlot.OFFHAND) {
                    event.addModifier(
                            ForgeMod.SWIM_SPEED.get(),
                            new AttributeModifier(UUID.fromString("2260df9a-7d89-4d88-a910-caf1f9203841"), "olympus.poseidon_trident_swim_speed", OlympusConfig.POSEIDON_TRIDENT_SWIM_SPEED_BONUS, AttributeModifier.Operation.MULTIPLY_TOTAL)
                    );

                }

            }
            else if (event.getSlotType() == EquipmentSlot.MAINHAND && stack.is(OlympusItems.SPEAR_OF_ARES.get())) {
                event.removeAttribute(Attributes.ATTACK_DAMAGE);
                event.removeAttribute(Attributes.ATTACK_SPEED);
                event.addModifier(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "olympus.ares_spear_damage", OlympusConfig.ARES_SPEAR_ATTACK_DAMAGE_BONUS, AttributeModifier.Operation.ADDITION)
                );
                event.addModifier(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_UUID, "olympus.ares_spear_speed", -2.7D, AttributeModifier.Operation.ADDITION)
                );
                event.addModifier(
                        ForgeMod.ENTITY_REACH.get(),
                        new AttributeModifier(ARES_SPEAR_REACH_UUID, "olympus.ares_spear_reach", 1.5D, AttributeModifier.Operation.ADDITION)
                );

            }

        }

        @SubscribeEvent
        public static void onAttackEntity(final AttackEntityEvent event) {
            final var player = event.getEntity();
            if (!player.getMainHandItem().is(OlympusItems.SPEAR_OF_ARES.get())) {
                return;
            }

            // Backport of the mc26+ AttackRange (for the spears)
            final double distance = Math.sqrt(event.getTarget().getBoundingBox().distanceToSqr(player.getEyePosition()));
            final double maximumReach = player.isCreative() ? 6.5 : 4.5;
            final float chargeValidationTicks = player.level().isClientSide ? 0.0F : 5.0F;
            if (distance < 2 - 0.125 || distance > maximumReach + 0.125 || player.getAttackStrengthScale(chargeValidationTicks) < 1.0F) {
                event.setCanceled(true);
            }

        }

        @SubscribeEvent
        public static void onEntityJoinLevel(final EntityJoinLevelEvent event) {
            if (event.getLevel().isClientSide || !(event.getEntity() instanceof AbstractArrow arrow) || !(arrow.getOwner() instanceof LivingEntity owner)) {
                return;
            }

            final boolean artemisBow = owner.getMainHandItem().is(OlympusItems.BOW_OF_ARTEMIS.get()) || owner.getOffhandItem().is(OlympusItems.BOW_OF_ARTEMIS.get());
            if (artemisBow && arrow instanceof ArtemisArrow artemisArrow) {
                artemisArrow.olympus$setArtemisArrow(true);
                arrow.setDeltaMovement(arrow.getDeltaMovement().scale(OlympusConfig.ARTEMIS_BOW_PROJECTILE_SPEED_MULTIPLIER));
            }

        }

        @SubscribeEvent
        public static void onLivingIncomingDamage(final LivingHurtEvent event) {
            if (!OlympusConfig.PERSEPHONE_CUP_DAMAGE_APPLIES_TO_WEAPONS || !(event.getSource().getEntity() instanceof ServerPlayer player)) {
                return;
            }

            // Applies the extra damage given by the cup to the weapon in use
            final ItemStack weapon = player.getMainHandItem();
            final boolean isDirectAttack = event.getSource().getDirectEntity() == player;
            final boolean isRangedWeaponAttack = event.getSource().getDirectEntity() != player && weapon != null && !weapon.isEmpty();
            if (!isDirectAttack && !isRangedWeaponAttack) {
                return;
            }

            final float damage = PersephoneCupItem.getEquippedDamageBonus(player);
            if (damage > 0) {
                event.setAmount(event.getAmount() + damage);
            }

        }

        @SubscribeEvent
        public static void onMobEffectAdded(final MobEffectEvent.Added event) {
            if (event.getEffectInstance().getEffect() == OlympusMobEffects.INVISIBILITY_OF_HADES.get()) {
                InvisibilityOfHadesEffect.onEffectStarted(event.getEntity());
            }

        }

        @SubscribeEvent
        public static void onPotionColorCalculation(final PotionColorCalculationEvent event) {
            if (event.getEffects().stream().noneMatch(instance -> instance.getEffect() == OlympusMobEffects.LIGHTNING_STUN.get())) {
                return;
            }

            final List<MobEffectInstance> effect = event.getEffects().stream()
                    .filter(instance -> instance.getEffect() != OlympusMobEffects.LIGHTNING_STUN.get())
                    .toList();
            event.setColor(PotionUtils.getColor(effect));
            event.shouldHideParticles(LivingEntity.areAllEffectsAmbient(effect));
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onLivingDamagePre(final LivingDamageEvent event) {
            if (!(event.getEntity() instanceof ServerPlayer player) || event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY) || event.getAmount() < player.getHealth() + player.getAbsorptionAmount()) {
                return;
            }

            // Prevents the player from dying on a mortal hit if the helmet or the cup are present
            if (HelmetOfHadesItem.tryActivateAbility(player) || PersephoneCupItem.tryActivateAbility(player)) {
                event.setAmount(0);
            }

        }

        @SubscribeEvent
        public static void onLivingChangeTarget(final LivingChangeTargetEvent event) {
            final LivingEntity target = event.getNewTarget();
            // Clears the current target when the invisibility of hades status effect is applied
            if (target != null && InvisibilityOfHadesEffect.preventsTargeting(target)) {
                event.setNewTarget(null);
            }

        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onLivingDeath(final LivingDeathEvent event) {
            if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
                return;
            }

            // Spawns a soul if the cup is equipped
            if (event.getEntity() instanceof Mob defeatedMob) {
                PersephoneCupItem.spawnSoulOnKill(defeatedMob, player);
            }

            // Charges the spear of ares active ability
            SpearOfAresItem.chargeSpecialAbilityForKill(player, event.getSource());
            // Reduces the instruments of hephaestus repair cooldown
            InstrumentsOfHephaestusItem.reduceCooldownOnKill(player);
        }

        @SubscribeEvent
        public static void onLivingFall(final LivingFallEvent event) {
            // If the spear of ares active ability is active, cancels the damage
            if (event.getEntity() instanceof ServerPlayer player && SpearOfAresItem.handleSpecialLanding(player, event.getDistance())) {
                event.setCanceled(true);
            }

        }

        @SubscribeEvent
        public static void onPlayerTick(final TickEvent.PlayerTickEvent event) {
            if (event.phase == TickEvent.Phase.END && event.player instanceof ServerPlayer player) {
                HermesSandalsItem.rechargeExtraJumps(player);
                SpearOfAresItem.updateSpecialFall(player);
            }

        }

        @SubscribeEvent
        public static void onLivingDamagePost(final LivingDamageEvent event) {
            if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
                return;
            }

            // On entity hit, if the player has the invisibility of hades affect, the duration is halved
            if (event.getEntity() != player && !event.getSource().is(OlympusDamageTypes.LIGHTNING) && event.getAmount() > 0) {
                InvisibilityOfHadesEffect.shortenAfterAttack(player);
            }

            // Bracers of zeus lightning bolt
            if (event.getEntity() instanceof Mob target) {
                BracersOfZeusItem.tryActivateAbility(target, player, event.getSource(), event.getAmount());
            }

        }

        @SubscribeEvent
        public static void onEnchantedEntityLoot(final LootingLevelEvent event) {
            if (event.getDamageSource() != null && event.getDamageSource().getDirectEntity() instanceof ArtemisArrow artemisArrow && artemisArrow.olympus$isArtemisArrow()) {
                event.setLootingLevel(event.getLootingLevel() + OlympusConfig.ARTEMIS_BOW_LOOTING_BONUS);
            }

        }

    }

}