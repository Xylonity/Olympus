package dev.xylonity.olympus.common.item;

import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.config.OlympusConfig;
import dev.xylonity.olympus.network.payload.SoulSalvationPayload;
import dev.xylonity.olympus.registry.OlympusItems;
import dev.xylonity.olympus.registry.OlympusMobEffects;
import java.util.Optional;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import top.theillusivec4.curios.api.CurioAttributeModifiers;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosSlotTypes;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class HelmetOfHadesItem extends Item implements ICurioItem {

    public HelmetOfHadesItem(final Properties properties) {
        super(properties);
    }

    @Override
    public boolean canEquip(final SlotContext slotContext, final ItemStack stack) {
        return CuriosSlotTypes.Preset.HEAD.id().equals(slotContext.identifier());
    }

    @Override
    public boolean canEquipFromUse(final SlotContext slotContext, final ItemStack stack) {
        return canEquip(slotContext, stack);
    }

    @Override
    public CurioAttributeModifiers getDefaultCurioAttributeModifiers(final ItemStack stack) {
        return CurioAttributeModifiers.builder()
                .addModifier(
                        Attributes.ARMOR,
                        new AttributeModifier(Olympus.of("helmet_of_hades_armor"), 2, AttributeModifier.Operation.ADD_VALUE),
                        CuriosSlotTypes.Preset.HEAD.id()
                )
                .build();
    }

    /// Applies the special effect of the helmet of hades (when receiving a mortal hit)
    public static boolean tryActivateAbility(final ServerPlayer player) {
        // Checks if the helmet of hades is equipped
        final Optional<ItemStack> equippedHelmet = CuriosApi.getCuriosInventory(player)
                .flatMap(handler -> handler.findFirstCurio(OlympusItems.HELMET_OF_HADES.get()))
                .map(SlotResult::stack);
        if (equippedHelmet.isEmpty() || player.getCooldowns().isOnCooldown(equippedHelmet.get())) {
            return false;
        }

        // Heals
        player.setHealth(player.getMaxHealth() * 0.5f);
        // Particles
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SoulSalvationPayload(player.getId(), 16, false));

        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_THUNDER, SoundSource.PLAYERS, 0.3F, 1);
        player.addEffect(new MobEffectInstance(OlympusMobEffects.INVISIBILITY_OF_HADES, 600, 0, false, false, true));

        // Cooldown
        final int cooldownTicks = Math.max(0, (int) Math.round(OlympusConfig.INSTANCE.helmetOfHadesCooldownSeconds.get() * 20));
        if (cooldownTicks > 0) {
            player.getCooldowns().addCooldown(equippedHelmet.get(), cooldownTicks);
        }

        return true;
    }

}