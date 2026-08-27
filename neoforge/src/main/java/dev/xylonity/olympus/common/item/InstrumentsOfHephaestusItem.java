package dev.xylonity.olympus.common.item;

import dev.xylonity.olympus.config.OlympusConfig;
import dev.xylonity.olympus.registry.OlympusItems;
import dev.xylonity.olympus.registry.OlympusParticles;
import dev.xylonity.olympus.registry.OlympusSounds;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosSlotTypes;
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
        return CuriosSlotTypes.Preset.BELT.id().equals(slotContext.identifier());
    }

    @Override
    public boolean canEquipFromUse(final SlotContext slotContext, final ItemStack stack) {
        return canEquip(slotContext, stack);
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
        repairTarget.setDamageValue(Math.max(0, repairTarget.getDamageValue() - OlympusConfig.INSTANCE.hephaestusInstrumentsRepairAmount.get()));

        // Cooldown
        startCooldown(player, instruments);

        // Particles and sound
        playForgingEffects(player);
    }

    public static void reduceCooldownOnKill(final ServerPlayer player) {
        // Checks that the instruments are equipped
        final Optional<ItemStack> equippedInstruments = CuriosApi.getCuriosInventory(player)
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
        final int reductionTicks = OlympusConfig.secondsToTicks(OlympusConfig.INSTANCE.hephaestusInstrumentsKillCooldownReductionSeconds.get());
        final int reducedCooldown = Math.max(0, remainingCooldown - reductionTicks);
        if (reducedCooldown == 0) {
            clearCooldown(instruments);
            player.getCooldowns().removeCooldown(player.getCooldowns().getCooldownGroup(instruments));
            return;
        }

        setCooldownEnd(instruments, player.level().getGameTime() + reducedCooldown);
        player.getCooldowns().addCooldown(instruments, reducedCooldown);
    }

    /// Finds the item in the inventory of the player with the most damage
    private static ItemStack findRepairTarget(final ServerPlayer player) {
        ItemStack repairTarget = ItemStack.EMPTY;
        int mostDamage = 0;

        // Inventory
        for (final ItemStack stack : player.getInventory().getNonEquipmentItems()) {
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
        return stack.isDamaged() && (stack.has(DataComponents.TOOL) || stack.is(ItemTags.SPEARS));
    }

    private static boolean isDamagedArmor(final ItemStack stack, final EquipmentSlot slot) {
        if (!stack.isDamaged()) {
            return false;
        }

        return switch (slot) {
            case HEAD -> stack.is(ItemTags.HEAD_ARMOR);
            case CHEST -> stack.is(ItemTags.CHEST_ARMOR);
            case LEGS -> stack.is(ItemTags.LEG_ARMOR);
            case FEET -> stack.is(ItemTags.FOOT_ARMOR);
            default -> false;
        };

    }

    private static int getRemainingCooldown(final ItemStack instruments, final ServerLevel level) {
        final long cooldownEnd = instruments.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getLongOr(TAG_COOLDOWN_END, 0);
        return Math.clamp(cooldownEnd - level.getGameTime(), 0, Integer.MAX_VALUE);
    }

    private static void startCooldown(final ServerPlayer player, final ItemStack instruments) {
        final int cooldownTicks = OlympusConfig.secondsToTicks(OlympusConfig.INSTANCE.hephaestusInstrumentsRepairCooldownSeconds.get());
        setCooldownEnd(instruments, player.level().getGameTime() + cooldownTicks);
        if (cooldownTicks > 0) {
            player.getCooldowns().addCooldown(instruments, cooldownTicks);
        }
    }

    private static void syncCooldown(ServerPlayer player, ItemStack instruments, int remainingCooldown) {
        if (!player.getCooldowns().isOnCooldown(instruments)) {
            player.getCooldowns().addCooldown(instruments, remainingCooldown);
        }

    }

    private static void setCooldownEnd(final ItemStack stack, final long cooldownEnd) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putLong(TAG_COOLDOWN_END, cooldownEnd));
    }

    private static void clearCooldown(final ItemStack stack) {
        final long cooldownEnd = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getLongOr(TAG_COOLDOWN_END, 0);
        if (cooldownEnd == 0) {
            return;
        }

        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.remove(TAG_COOLDOWN_END));
    }

    private static void playForgingEffects(final ServerPlayer player) {
        final ServerLevel level = player.level();
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
