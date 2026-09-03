package dev.xylonity.olympus.common.item;

import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.common.util.OlympusTooltip;
import dev.xylonity.olympus.config.OlympusConfig;
import dev.xylonity.olympus.network.payload.SoulSalvationPayload;
import dev.xylonity.olympus.network.OlympusNetwork;
import dev.xylonity.olympus.registry.OlympusItems;
import dev.xylonity.olympus.registry.OlympusMobEffects;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class HelmetOfHadesItem extends Item implements ICurioItem {

    public HelmetOfHadesItem(final Properties properties) {
        super(properties);
    }

    @Override
    public boolean canEquip(final SlotContext slotContext, final ItemStack stack) {
        return "head".equals(slotContext.identifier());
    }

    @Override
    public boolean canEquipFromUse(final SlotContext slotContext, final ItemStack stack) {
        return canEquip(slotContext, stack);
    }

    @Override
    public void appendHoverText(final ItemStack stack, final Level level, final List<Component> tooltip, final TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        OlympusTooltip.append(tooltip::add, "helmet_of_hades", 0x9B7FD3,
                OlympusTooltip.ability(1,
                        OlympusTooltip.property("armor", "+" + OlympusTooltip.number(OlympusConfig.HELMET_OF_HADES_ARMOR)),
                        OlympusTooltip.property("restored_health", OlympusTooltip.percent(OlympusConfig.HELMET_OF_HADES_RESTORED_HEALTH_PERCENTAGE)),
                        OlympusTooltip.property("invisibility", OlympusTooltip.seconds(OlympusConfig.HELMET_OF_HADES_INVISIBILITY_SECONDS)),
                        OlympusTooltip.property("cooldown", OlympusTooltip.seconds(OlympusConfig.HELMET_OF_HADES_COOLDOWN_SECONDS))
                ));

    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(final SlotContext slotContext, final UUID uuid, final ItemStack stack) {
        final Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create();
        modifiers.put(Attributes.ARMOR, new AttributeModifier(uuid, "olympus.helmet_of_hades_armor", OlympusConfig.HELMET_OF_HADES_ARMOR, AttributeModifier.Operation.ADDITION));
        return modifiers;
    }

    /// Applies the special effect of the helmet of hades (when receiving a mortal hit)
    public static boolean tryActivateAbility(final ServerPlayer player) {
        // Checks if the helmet of hades is equipped
        final Optional<ItemStack> equippedHelmet = CuriosApi.getCuriosInventory(player)
                .resolve()
                .flatMap(handler -> handler.findFirstCurio(OlympusItems.HELMET_OF_HADES.get()))
                .map(SlotResult::stack);
        if (equippedHelmet.isEmpty() || player.getCooldowns().isOnCooldown(equippedHelmet.get().getItem())) {
            return false;
        }

        // Heals
        player.setHealth(player.getMaxHealth() * (float) OlympusConfig.HELMET_OF_HADES_RESTORED_HEALTH_PERCENTAGE);
        // Particles
        OlympusNetwork.sendToTrackingAndSelf(player, SoulSalvationPayload.TYPE, new SoulSalvationPayload(player.getId(), 16, false));

        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_THUNDER, SoundSource.PLAYERS, 0.3F, 1);
        final int invisibilityTicks = OlympusConfig.secondsToTicks(OlympusConfig.HELMET_OF_HADES_INVISIBILITY_SECONDS);
        if (invisibilityTicks > 0) {
            player.addEffect(new MobEffectInstance(OlympusMobEffects.INVISIBILITY_OF_HADES.get(), invisibilityTicks, 0, false, false, true));
        }

        // Cooldown
        final int cooldownTicks = OlympusConfig.secondsToTicks(OlympusConfig.HELMET_OF_HADES_COOLDOWN_SECONDS);
        if (cooldownTicks > 0) {
            player.getCooldowns().addCooldown(equippedHelmet.get().getItem(), cooldownTicks);
        }

        return true;
    }

}
