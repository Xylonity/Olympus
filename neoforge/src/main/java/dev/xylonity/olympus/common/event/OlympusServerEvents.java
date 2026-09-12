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
import dev.xylonity.olympus.common.item.PoseidonTridentItem;
import dev.xylonity.olympus.common.item.SpearOfAresItem;
import dev.xylonity.olympus.config.OlympusConfig;
import dev.xylonity.olympus.common.worldgen.ParthenonWorldgen;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.event.level.LevelEvent;
import dev.xylonity.olympus.registry.OlympusDamageTypes;
import dev.xylonity.olympus.registry.OlympusEntities;
import dev.xylonity.olympus.registry.OlympusItems;
import dev.xylonity.olympus.registry.OlympusMobEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.living.EffectParticleModificationEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.minecraft.world.entity.projectile.AbstractArrow;

@EventBusSubscriber(modid = Olympus.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class OlympusServerEvents {

    @SubscribeEvent
    public static void registerAttributes(final EntityAttributeCreationEvent event) {
        event.put(OlympusEntities.HARPY.get(), HarpyEntity.createAttributes().build());
        event.put(OlympusEntities.ELITE_HARPY.get(), HarpyEntity.createEliteAttributes().build());
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(final RegisterSpawnPlacementsEvent event) {
        event.register(OlympusEntities.HARPY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(OlympusEntities.ELITE_HARPY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    @EventBusSubscriber(modid = Olympus.MOD_ID)
    public static final class ForgeEvents {

        @SubscribeEvent
        public static void onLevelLoad(final LevelEvent.Load event) {
            if (event.getLevel() instanceof ServerLevel level) {
                ParthenonWorldgen.loadLevel(level);
            }

        }

        @SubscribeEvent
        public static void onLevelUnload(final LevelEvent.Unload event) {
            if (event.getLevel() instanceof ServerLevel level) {
                ParthenonWorldgen.unloadLevel(level);
            }

        }

        @SubscribeEvent
        public static void onItemAttributeModifiers(final ItemAttributeModifierEvent event) {
            final ItemStack stack = event.getItemStack();

            if (stack.is(OlympusItems.POSEIDON_TRIDENT.get())) {
                {
                    event.removeAllModifiersFor(Attributes.ATTACK_DAMAGE);
                    event.removeAllModifiersFor(Attributes.ATTACK_SPEED);
                    event.addModifier(
                            Attributes.ATTACK_DAMAGE,
                            new AttributeModifier(PoseidonTridentItem.VANILLA_ATTACK_DAMAGE_MODIFIER_UUID, OlympusConfig.POSEIDON_TRIDENT_ATTACK_DAMAGE_BONUS, AttributeModifier.Operation.ADD_VALUE),
                            EquipmentSlotGroup.MAINHAND
                    );
                    event.addModifier(
                            Attributes.ATTACK_SPEED,
                            new AttributeModifier(PoseidonTridentItem.VANILLA_ATTACK_SPEED_MODIFIER_UUID, OlympusConfig.POSEIDON_TRIDENT_ATTACK_SPEED_MODIFIER, AttributeModifier.Operation.ADD_VALUE),
                            EquipmentSlotGroup.MAINHAND
                    );

                }
                if (OlympusConfig.POSEIDON_TRIDENT_LORD_OF_THE_SEA_ENABLED) {
                    event.addModifier(
                            NeoForgeMod.SWIM_SPEED,
                            new AttributeModifier(Olympus.of("poseidon_trident_swim_speed"), OlympusConfig.POSEIDON_TRIDENT_SWIM_SPEED_BONUS, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
                            EquipmentSlotGroup.HAND
                    );

                }

            }
            else if (stack.is(OlympusItems.SPEAR_OF_ARES.get())) {
                event.removeAllModifiersFor(Attributes.ATTACK_DAMAGE);
                event.removeAllModifiersFor(Attributes.ATTACK_SPEED);
                event.addModifier(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(PoseidonTridentItem.VANILLA_ATTACK_DAMAGE_MODIFIER_UUID, OlympusConfig.ARES_SPEAR_ATTACK_DAMAGE_BONUS, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                );
                event.addModifier(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(PoseidonTridentItem.VANILLA_ATTACK_SPEED_MODIFIER_UUID, OlympusConfig.ARES_SPEAR_ATTACK_SPEED_MODIFIER, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                );
                event.addModifier(
                        Attributes.ENTITY_INTERACTION_RANGE,
                        new AttributeModifier(SpearOfAresItem.ARES_SPEAR_REACH_UUID, 1.5D, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
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
            final boolean hasEnabledArrowAbility = OlympusConfig.ARTEMIS_BOW_MOONLIT_HUNT_ENABLED || OlympusConfig.ARTEMIS_BOW_GUARDIAN_ARROW_ENABLED;
            if (artemisBow && hasEnabledArrowAbility && arrow instanceof ArtemisArrow artemisArrow) {
                artemisArrow.olympus$setArtemisArrow(true);
                if (OlympusConfig.ARTEMIS_BOW_MOONLIT_HUNT_ENABLED) {
                    arrow.setDeltaMovement(arrow.getDeltaMovement().scale(OlympusConfig.ARTEMIS_BOW_PROJECTILE_SPEED_MULTIPLIER));
                }

            }

        }

        @SubscribeEvent
        public static void onLivingIncomingDamage(final LivingIncomingDamageEvent event) {
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
            if (event.getEffectInstance().getEffect().value() == OlympusMobEffects.INVISIBILITY_OF_HADES.get()) {
                InvisibilityOfHadesEffect.onEffectStarted(event.getEntity());
            }

        }

        @SubscribeEvent
        public static void onPotionColorCalculation(final EffectParticleModificationEvent event) {
            if (event.getEffect().getEffect().value() == OlympusMobEffects.LIGHTNING_STUN.get()) {
                event.setVisible(false);
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onLivingDamagePre(final LivingDamageEvent.Pre event) {
            if (!(event.getEntity() instanceof ServerPlayer player) || event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY) || event.getNewDamage() < player.getHealth() + player.getAbsorptionAmount()) {
                return;
            }

            // Prevents the player from dying on a mortal hit if the helmet or the cup are present
            if (HelmetOfHadesItem.tryActivateAbility(player) || PersephoneCupItem.tryActivateAbility(player)) {
                event.setNewDamage(0);
            }

        }

        @SubscribeEvent
        public static void onLivingChangeTarget(final LivingChangeTargetEvent event) {
            final LivingEntity target = event.getNewAboutToBeSetTarget();
            // Clears the current target when the invisibility of hades status effect is applied
            if (target != null && InvisibilityOfHadesEffect.preventsTargeting(target)) {
                event.setNewAboutToBeSetTarget(null);
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
        public static void onPlayerTick(final PlayerTickEvent.Post event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                HermesSandalsItem.rechargeExtraJumps(player);
                SpearOfAresItem.updateSpecialFall(player);
            }

        }

        @SubscribeEvent
        public static void onLivingDamagePost(final LivingDamageEvent.Post event) {
            if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
                return;
            }

            // On entity hit, if the player has the invisibility of hades affect, the duration is halved
            if (event.getEntity() != player && !event.getSource().is(OlympusDamageTypes.LIGHTNING) && event.getNewDamage() > 0) {
                InvisibilityOfHadesEffect.shortenAfterAttack(player);
            }

            // Bracers of zeus lightning bolt
            if (event.getEntity() instanceof Mob target) {
                BracersOfZeusItem.tryActivateAbility(target, player, event.getSource(), event.getNewDamage());
            }

        }

        @SubscribeEvent
        public static void onEnchantedEntityLoot(final GetEnchantmentLevelEvent event) {
            if (OlympusConfig.ARTEMIS_BOW_MOONLIT_HUNT_ENABLED && event.getStack().is(OlympusItems.BOW_OF_ARTEMIS.get()) && event.isTargetting(Enchantments.LOOTING)) {
                event.getHolder(Enchantments.LOOTING).ifPresent(looting -> event.getEnchantments().set(looting, event.getEnchantments().getLevel(looting) + OlympusConfig.ARTEMIS_BOW_LOOTING_BONUS));
            }

        }

    }

}
