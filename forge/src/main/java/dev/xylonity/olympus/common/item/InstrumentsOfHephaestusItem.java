package dev.xylonity.olympus.common.item;

import dev.xylonity.olympus.config.OlympusConfig;
import dev.xylonity.olympus.common.util.OlympusTooltip;
import dev.xylonity.olympus.registry.OlympusItems;
import dev.xylonity.olympus.registry.OlympusParticles;
import dev.xylonity.olympus.registry.OlympusSounds;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public final class InstrumentsOfHephaestusItem extends Item implements ICurioItem {

    private static final String TAG_COOLDOWN_END = "olympus_hephaestus_cooldown_end";

    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    public InstrumentsOfHephaestusItem(final Properties properties) {
        super(properties);
    }

    @Override
    public boolean canEquip(final SlotContext slotContext, final ItemStack stack) {
        return "belt".equals(slotContext.identifier());
    }

    @Override
    public boolean canEquipFromUse(final SlotContext slotContext, final ItemStack stack) {
        return canEquip(slotContext, stack);
    }

    @Override
    public void appendHoverText(final ItemStack stack, final Level level, final List<Component> tooltip, final TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        OlympusTooltip.append(tooltip::add, "instruments_of_hephaestus", 0xE8894D,
                OlympusTooltip.ability(1,
                        OlympusTooltip.property("repair_amount", Integer.toString(OlympusConfig.HEPHAESTUS_INSTRUMENTS_REPAIR_AMOUNT)),
                        OlympusTooltip.property("repair_cooldown", OlympusTooltip.seconds(OlympusConfig.HEPHAESTUS_INSTRUMENTS_REPAIR_COOLDOWN_SECONDS)),
                        OlympusTooltip.property("kill_reduction", OlympusTooltip.seconds(OlympusConfig.HEPHAESTUS_INSTRUMENTS_KILL_COOLDOWN_REDUCTION_SECONDS))
                ));

    }

    @Override
    public void curioTick(final SlotContext slotContext, final ItemStack instruments) {
        // Equipped stack is inferred
        if (slotContext.cosmetic() || !(slotContext.entity() instanceof ServerPlayer player)) {
            return;
        }

        // Cooldown computation (if it's present)
        final int remainingCooldown = getRemainingCooldown(instruments, player.level());
        if (remainingCooldown > 0) {
            syncCooldown(player, instruments, remainingCooldown);
            return;
        }

        clearCooldown(instruments);

        // Finds a random item in the inventory that has a durability bar (with the most damage)
        final ItemStack repairTarget = findRepairTarget(player);
        if (repairTarget.isEmpty()) {
            return;
        }

        // Repairs the target item a certain amount of durability
        repairTarget.setDamageValue(Math.max(0, repairTarget.getDamageValue() - OlympusConfig.HEPHAESTUS_INSTRUMENTS_REPAIR_AMOUNT));

        // Cooldown
        startCooldown(player, instruments);

        // Particles and sound
        playForgingEffects(player);
    }

    public static void reduceCooldownOnKill(final ServerPlayer player) {
        // Checks that the instruments are equipped
        final Optional<ItemStack> equippedInstruments = CuriosApi.getCuriosInventory(player)
                .resolve()
                .flatMap(handler -> handler.findFirstCurio(OlympusItems.INSTRUMENTS_OF_HEPHAESTUS.get()))
                .map(SlotResult::stack);
        if (equippedInstruments.isEmpty()) {
            return;
        }

        final ItemStack instruments = equippedInstruments.get();
        final int remainingCooldown = getRemainingCooldown(instruments, player.level());
        if (remainingCooldown <= 0) {
            clearCooldown(instruments);
            return;
        }

        // Reduces the cooldown of the instruments by x amount
        final int reductionTicks = OlympusConfig.secondsToTicks(OlympusConfig.HEPHAESTUS_INSTRUMENTS_KILL_COOLDOWN_REDUCTION_SECONDS);
        final int reducedCooldown = Math.max(0, remainingCooldown - reductionTicks);
        if (reducedCooldown == 0) {
            clearCooldown(instruments);
            player.getCooldowns().removeCooldown(instruments.getItem());
            return;
        }

        setCooldownEnd(instruments, player.level().getGameTime() + reducedCooldown);
        player.getCooldowns().addCooldown(instruments.getItem(), reducedCooldown);
    }

    /// Finds the item in the inventory of the player with the most damage
    private static ItemStack findRepairTarget(final ServerPlayer player) {
        ItemStack repairTarget = ItemStack.EMPTY;
        int mostDamage = 0;

        // Inventory
        for (final ItemStack stack : player.getInventory().items) {
            if (isDamagedTool(stack) && stack.getDamageValue() > mostDamage) {
                repairTarget = stack;
                mostDamage = stack.getDamageValue();
            }

        }

        // Equipped
        final ItemStack offhandStack = player.getOffhandItem();
        if (isDamagedTool(offhandStack) && offhandStack.getDamageValue() > mostDamage) {
            repairTarget = offhandStack;
            mostDamage = offhandStack.getDamageValue();
        }

        // Equipped armor
        for (final EquipmentSlot slot : ARMOR_SLOTS) {
            final ItemStack stack = player.getItemBySlot(slot);
            if (isDamagedArmor(stack, slot) && stack.getDamageValue() > mostDamage) {
                repairTarget = stack;
                mostDamage = stack.getDamageValue();
            }

        }

        return repairTarget;
    }

    private static boolean isDamagedTool(final ItemStack stack) {
        return stack.isDamaged() && !(stack.getItem() instanceof ArmorItem);
    }

    private static boolean isDamagedArmor(final ItemStack stack, final EquipmentSlot slot) {
        if (!stack.isDamaged()) {
            return false;
        }

        return stack.getItem() instanceof ArmorItem armor && armor.getEquipmentSlot() == slot;

    }

    private static int getRemainingCooldown(final ItemStack instruments, final Level level) {
        final long cooldownEnd = instruments.hasTag() ? instruments.getTag().getLong(TAG_COOLDOWN_END) : 0;
        return (int) Math.max(0, Math.min(cooldownEnd - level.getGameTime(), Integer.MAX_VALUE));
    }

    private static void startCooldown(final ServerPlayer player, final ItemStack instruments) {
        final int cooldownTicks = OlympusConfig.secondsToTicks(OlympusConfig.HEPHAESTUS_INSTRUMENTS_REPAIR_COOLDOWN_SECONDS);
        setCooldownEnd(instruments, player.level().getGameTime() + cooldownTicks);
        if (cooldownTicks > 0) {
            player.getCooldowns().addCooldown(instruments.getItem(), cooldownTicks);
        }

    }

    private static void syncCooldown(ServerPlayer player, ItemStack instruments, int remainingCooldown) {
        if (!player.getCooldowns().isOnCooldown(instruments.getItem())) {
            player.getCooldowns().addCooldown(instruments.getItem(), remainingCooldown);
        }

    }

    private static void setCooldownEnd(final ItemStack stack, final long cooldownEnd) {
        stack.getOrCreateTag().putLong(TAG_COOLDOWN_END, cooldownEnd);
    }

    private static void clearCooldown(final ItemStack stack) {
        final long cooldownEnd = stack.hasTag() ? stack.getTag().getLong(TAG_COOLDOWN_END) : 0;
        if (cooldownEnd == 0) {
            return;
        }

        stack.removeTagKey(TAG_COOLDOWN_END);
    }

    private static void playForgingEffects(final ServerPlayer player) {
        final ServerLevel level = player.serverLevel();
        level.playSound(null, player.getX(), player.getY(), player.getZ(), OlympusSounds.HEPHAESTUS_FORGING.get(), SoundSource.PLAYERS, 1, 1);

        // Randomized speed and directions for each particle
        final RandomSource random = level.getRandom();
        final double spawnY = player.getY() + player.getBbHeight() * 0.45;
        for (int index = 0; index < 6; index++) {
            final double angle = random.nextDouble() * Math.PI * 2;
            final double hSpeed = 0.12 + random.nextDouble() * 0.12;
            final double vSpeed = 0.25 + random.nextDouble() * 0.15;
            level.sendParticles(OlympusParticles.FORGING_SPARK.get(), player.getX(), spawnY, player.getZ(), 0, Math.cos(angle) * hSpeed, vSpeed, Math.sin(angle) * hSpeed, 1);
        }

    }

}
