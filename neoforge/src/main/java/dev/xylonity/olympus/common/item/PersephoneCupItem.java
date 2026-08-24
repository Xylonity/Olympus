package dev.xylonity.olympus.common.item;

import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.common.entity.AbsorbedSoulEntity;
import dev.xylonity.olympus.config.OlympusConfig;
import dev.xylonity.olympus.network.payload.SoulSalvationPayload;
import dev.xylonity.olympus.registry.OlympusItems;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.neoforged.neoforge.network.PacketDistributor;
import top.theillusivec4.curios.api.CurioAttributeModifiers;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public final class PersephoneCupItem extends Item implements ICurioItem {

    private static final String TAG_SOUL_CHARGES = "olympus_soul_charges";
    private static final String TAG_SOUL_DROPPED = "olympus_soul_dropped";

    public static final int MAX_SOUL_CHARGES = 40;

    public PersephoneCupItem(final Properties properties) {
        super(properties);
    }

    @Override
    public boolean canEquip(final SlotContext slotContext, final ItemStack stack) {
        return "jewelry".equals(slotContext.identifier());
    }

    @Override
    public boolean canEquipFromUse(final SlotContext slotContext, final ItemStack stack) {
        return canEquip(slotContext, stack);
    }

    @Override
    public CurioAttributeModifiers getDefaultCurioAttributeModifiers(final ItemStack stack) {
        final int soulCharges = getSoulCharges(stack);
        if (soulCharges == 0) {
            return CurioAttributeModifiers.EMPTY;
        }

        // Additional 0.2 damage per soul acquired
        return CurioAttributeModifiers.builder()
                .addModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier(Olympus.of("persephone_cup_damage"), soulCharges * 0.2, AttributeModifier.Operation.ADD_VALUE), "jewelry")
                .build();
    }

    @Override
    public void appendHoverText(final ItemStack stack, final TooltipContext context, final TooltipDisplay display, final Consumer<Component> builder, final TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, display, builder, tooltipFlag);
        builder.accept(Component.translatable("item.olympus.persephone_cup.soul_charges", getSoulCharges(stack), MAX_SOUL_CHARGES).withStyle(ChatFormatting.DARK_PURPLE));
    }

    /// Applies the special effect of the cup (increased damage and canceled first mortal hit on max charge amount)
    public static boolean tryActivateAbility(final ServerPlayer player) {
        // Checks that the cup is equipped
        final Optional<ItemStack> equippedCup = findEquippedCup(player);
        if (equippedCup.isEmpty() || getSoulCharges(equippedCup.get()) < MAX_SOUL_CHARGES) {
            return false;
        }

        // Reduces the charges amount
        final ItemStack cup = equippedCup.get();
        setSoulCharges(cup, MAX_SOUL_CHARGES / 2);
        // Saves the player on a mortal hit
        player.setHealth(player.getMaxHealth() * 0.3f);
        // Particles and sound
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SoulSalvationPayload(player.getId(), 16, false));
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_THUNDER, SoundSource.PLAYERS, 0.3F, 1);

        final int regenerationTicks = Math.max(0, (int) Math.round(OlympusConfig.INSTANCE.persephoneCupRegenerationSeconds.get() * 20));
        if (regenerationTicks > 0) {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, regenerationTicks, 1));
        }

        return true;
    }

    public static void spawnSoulOnKill(final Mob mob, final ServerPlayer player) {
        if (!(mob.level() instanceof ServerLevel level) || findEquippedCup(player).isEmpty() || mob.getPersistentData().getBooleanOr(TAG_SOUL_DROPPED, false)) {
            return;
        }

        // Spawns just a single absorbed soul
        mob.getPersistentData().putBoolean(TAG_SOUL_DROPPED, true);
        level.addFreshEntity(new AbsorbedSoulEntity(level, mob.position().add(0, mob.getBbHeight() * 0.5, 0), player));
    }

    public static int getSoulCharges(final ItemStack stack) {
        return Mth.clamp(stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getIntOr(TAG_SOUL_CHARGES, 0), 0, MAX_SOUL_CHARGES);
    }

    public static void addSoulCharge(final ItemStack stack) {
        final int soulCharges = getSoulCharges(stack);
        if (soulCharges >= MAX_SOUL_CHARGES) {
            return;
        }

        setSoulCharges(stack, soulCharges + 1);
    }

    public static void setSoulCharges(final ItemStack stack, final int soulCharges) {
        final int clampedCharges = Mth.clamp(soulCharges, 0, MAX_SOUL_CHARGES);
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            if (clampedCharges == 0) {
                tag.remove(TAG_SOUL_CHARGES);
            }
            else {
                tag.putInt(TAG_SOUL_CHARGES, clampedCharges);
            }

        });

        if (clampedCharges == 0) {
            stack.remove(DataComponents.CUSTOM_MODEL_DATA);
        }
        else {
            stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of((float) clampedCharges), List.of(), List.of(), List.of()));
        }

    }

    private static Optional<ItemStack> findEquippedCup(final ServerPlayer player) {
        return CuriosApi.getCuriosInventory(player)
                .flatMap(handler -> handler.findFirstCurio(OlympusItems.PERSEPHONE_CUP.get()))
                .map(SlotResult::stack);
    }

}