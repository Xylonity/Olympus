package dev.xylonity.olympus.common.item;

import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.common.util.OlympusTooltip;
import dev.xylonity.olympus.config.OlympusConfig;
import dev.xylonity.olympus.registry.OlympusItems;
import dev.xylonity.olympus.registry.OlympusSounds;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.particles.ParticleTypes;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public final class HermesSandalsItem extends Item implements ICurioItem {

    private static final String TAG_EXTRA_JUMPS = "olympus_hermes_sandals_extra_jumps";

    public HermesSandalsItem(final Properties properties) {
        super(properties);
    }

    @Override
    public boolean canEquip(final SlotContext slotContext, final ItemStack stack) {
        return "feet".equals(slotContext.identifier());
    }

    @Override
    public boolean canEquipFromUse(final SlotContext slotContext, final ItemStack stack) {
        return canEquip(slotContext, stack);
    }

    @Override
    public void appendHoverText(final ItemStack stack, final Level level, final List<Component> tooltip, final TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        OlympusTooltip.append(tooltip::add, "hermes_sandals", 0x72D5E8,
                OlympusTooltip.ability(1,
                        OlympusTooltip.property("extra_jumps", Integer.toString(getExtraJumps())),
                        OlympusTooltip.property("movement_speed", "+" + OlympusTooltip.percent(OlympusConfig.HERMES_SANDALS_MOVEMENT_SPEED_BONUS)),
                        OlympusTooltip.property("armor", "+" + OlympusTooltip.number(OlympusConfig.HERMES_SANDALS_ARMOR))
                ));

    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(final SlotContext slotContext, final UUID uuid, final ItemStack stack) {
        final Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create();
        final UUID speedUuid = UUID.nameUUIDFromBytes((uuid + ":speed").getBytes(StandardCharsets.UTF_8));
        modifiers.put(Attributes.ARMOR, new AttributeModifier(uuid, "olympus.hermes_sandals_armor", OlympusConfig.HERMES_SANDALS_ARMOR, AttributeModifier.Operation.ADDITION));
        modifiers.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(speedUuid, "olympus.hermes_sandals_speed", OlympusConfig.HERMES_SANDALS_MOVEMENT_SPEED_BONUS, AttributeModifier.Operation.MULTIPLY_BASE));
        return modifiers;
    }

    public static boolean findEquippedSandals(final LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity)
                .resolve()
                .map(handler -> handler.isEquipped(OlympusItems.HERMES_SANDALS.get()))
                .orElse(false);
    }

    public static boolean canExtraJump(final Player player) {
        return findEquippedSandals(player) && !player.onGround() && !player.isPassenger() && !player.isFallFlying() && !player.getAbilities().flying && !player.isInWater() && !player.isInLava() && !player.onClimbable() && !player.isSpectator();
    }

    public static int getExtraJumps() {
        return OlympusConfig.HERMES_SANDALS_JUMP_AMOUNT;
    }

    public static void rechargeExtraJumps(final ServerPlayer player) {
        // Jumps are always recharged on ground collision
        if (player.onGround()) {
            player.getPersistentData().putInt(TAG_EXTRA_JUMPS, getExtraJumps());
        }

    }

    public static void tryActiveAbility(final ServerPlayer player) {
        final int maxJumps = getExtraJumps();
        final int remainingJumps = Math.min(player.getPersistentData().contains(TAG_EXTRA_JUMPS) ? player.getPersistentData().getInt(TAG_EXTRA_JUMPS) : maxJumps, maxJumps);
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
        final ServerLevel level = player.serverLevel();
        if (!findEquippedSandals(player)) {
            return;
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(), OlympusSounds.HERMES_JUMP.get(), SoundSource.PLAYERS, 0.6f, 1.1f);
        for (int direction = 0; direction < 16; direction++) {
            final double angle = Math.PI * 2D * direction / 16;
            level.sendParticles(ParticleTypes.POOF, player.getX(), player.getY() + 0.08D, player.getZ(), 0, Math.cos(angle) * 0.23225D, 0, Math.sin(angle) * 0.23225D, 1);
        }

    }

}
