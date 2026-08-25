package dev.xylonity.olympus.common.event;

import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.common.effect.InvisibilityOfHadesEffect;
import dev.xylonity.olympus.common.util.ArtemisArrow;
import dev.xylonity.olympus.common.item.BracersOfZeusItem;
import dev.xylonity.olympus.common.item.HelmetOfHadesItem;
import dev.xylonity.olympus.common.item.HermesSandalsItem;
import dev.xylonity.olympus.common.item.InstrumentsOfHephaestusItem;
import dev.xylonity.olympus.common.item.PersephoneCupItem;
import dev.xylonity.olympus.common.item.SpearOfAresItem;
import dev.xylonity.olympus.registry.OlympusDamageTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.enchanting.EnchantedEntityLootEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = Olympus.MOD_ID)
public final class OlympusServerEvents {

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
        if (event.getEntity() != player && !event.getSource().is(OlympusDamageTypes.LIGHTNING) && event.getHealthDamage() > 0) {
            InvisibilityOfHadesEffect.shortenAfterAttack(player);
        }

        // Bracers of zeus lightning bolt
        if (event.getEntity() instanceof Mob target) {
            BracersOfZeusItem.tryActivateAbility(target, player, event.getSource(), event.getHealthDamage());
        }

    }

    @SubscribeEvent
    public static void onEnchantedEntityLoot(final EnchantedEntityLootEvent event) {
        if (event.getDamageSource() != null && event.getDamageSource().getDirectEntity() instanceof ArtemisArrow artemisArrow && artemisArrow.olympus$isArtemisArrow() && event.getEnchantment().is(Enchantments.LOOTING)) {
            event.setEnchantmentLevel(event.getEnchantmentLevel() + 1);
        }

    }

}
