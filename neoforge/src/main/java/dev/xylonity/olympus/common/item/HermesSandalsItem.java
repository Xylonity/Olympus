package dev.xylonity.olympus.common.item;

import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.registry.OlympusItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CurioAttributeModifiers;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosSlotTypes;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public final class HermesSandalsItem extends Item implements ICurioItem {

    private static final String TAG_EXTRA_JUMPS = "olympus_hermes_sandals_extra_jumps";

    public static final int EXTRA_JUMPS = 3;

    public HermesSandalsItem(final Properties properties) {
        super(properties);
    }

    @Override
    public boolean canEquip(final SlotContext slotContext, final ItemStack stack) {
        return CuriosSlotTypes.Preset.FEET.id().equals(slotContext.identifier());
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
                        new AttributeModifier(Olympus.of("hermes_sandals_armor"), 1, AttributeModifier.Operation.ADD_VALUE),
                        CuriosSlotTypes.Preset.FEET.id()
                )
                .addModifier(
                        Attributes.MOVEMENT_SPEED,
                        new AttributeModifier(Olympus.of("hermes_sandals_speed"), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
                        CuriosSlotTypes.Preset.FEET.id()
                )
                .build();
    }

    public static boolean findEquippedSandals(final LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity)
                .map(handler -> handler.isEquipped(OlympusItems.HERMES_SANDALS.get()))
                .orElse(false);
    }

    public static boolean canExtraJump(final Player player) {
        return findEquippedSandals(player) && !player.onGround() && !player.isPassenger() && !player.isFallFlying() && !player.getAbilities().flying && !player.isInWater() && !player.isInLava() && !player.onClimbable() && !player.isSpectator();
    }

    public static void rechargeExtraJumps(final ServerPlayer player) {
        // Jumps are always recharged on ground collision
        if (player.onGround()) {
            player.getPersistentData().putInt(TAG_EXTRA_JUMPS, EXTRA_JUMPS);
        }

    }

    public static void tryActiveAbility(final ServerPlayer player) {
        final int remainingJumps = player.getPersistentData().getIntOr(TAG_EXTRA_JUMPS, EXTRA_JUMPS);
        if (remainingJumps <= 0 || !canExtraJump(player)) {
            return;
        }

        // Normal jump, 1 less jump
        player.getPersistentData().putInt(TAG_EXTRA_JUMPS, remainingJumps - 1);
        player.jumpFromGround();

        // particles and sound
        playJumpEffects(player);
    }

    public static void playJumpEffects(final ServerPlayer player) {
        final ServerLevel level = player.level();
        if (!findEquippedSandals(player)) {
            return;
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BREEZE_WIND_CHARGE_BURST, SoundSource.PLAYERS, 0.8f, 1.1f);
        for (int direction = 0; direction < 16; direction++) {
            final double angle = Math.PI * 2D * direction / 16;
            level.sendParticles(ParticleTypes.POOF, player.getX(), player.getY() + 0.08D, player.getZ(), 0, Math.cos(angle) * 0.23225D, 0, Math.sin(angle) * 0.23225D, 1);
        }

    }

}
