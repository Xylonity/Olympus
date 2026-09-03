package dev.xylonity.olympus.common.item;

import dev.xylonity.olympus.common.entity.projectile.AbsorbedSoulEntity;
import dev.xylonity.olympus.common.util.OlympusTooltip;
import dev.xylonity.olympus.config.OlympusConfig;
import dev.xylonity.olympus.network.payload.SoulSalvationPayload;
import dev.xylonity.olympus.network.OlympusNetwork;
import dev.xylonity.olympus.registry.OlympusItems;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
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
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(final SlotContext slotContext, final UUID uuid, final ItemStack stack) {
        final int soulCharges = getSoulCharges(stack);
        if (soulCharges == 0 || OlympusConfig.PERSEPHONE_CUP_DAMAGE_APPLIES_TO_WEAPONS) {
            return HashMultimap.create();
        }

        // Additional configurable damage per soul acquired
        final double damagePerSoul = OlympusConfig.PERSEPHONE_CUP_DAMAGE_PER_SOUL;
        final Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create();
        modifiers.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(uuid, "olympus.persephone_cup_damage", soulCharges * damagePerSoul, AttributeModifier.Operation.ADDITION));
        return modifiers;
    }

    public static float getEquippedDamageBonus(final ServerPlayer player) {
        final float damagePerSoul = (float) OlympusConfig.PERSEPHONE_CUP_DAMAGE_PER_SOUL;
        return findEquippedCup(player)
                .map(cup -> getSoulCharges(cup) * damagePerSoul)
                .orElse(0f);
    }

    @Override
    public void appendHoverText(final ItemStack stack, final Level level, final List<Component> builder, final TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, level, builder, tooltipFlag);
        final Component soulCharges = Component.translatable("item.olympus.persephone_cup.soul_charges", getSoulCharges(stack), MAX_SOUL_CHARGES).withStyle(ChatFormatting.DARK_PURPLE);
        OlympusTooltip.appendWithStatus(builder::add, "persephone_cup", 0xC987D4, soulCharges,
                OlympusTooltip.ability(1,
                        OlympusTooltip.property("damage_per_soul", "+" + OlympusTooltip.number(OlympusConfig.PERSEPHONE_CUP_DAMAGE_PER_SOUL)),
                        OlympusTooltip.property("maximum_souls", Integer.toString(MAX_SOUL_CHARGES))
                ),
                OlympusTooltip.ability(2,
                        OlympusTooltip.property("charge_cost", Integer.toString(OlympusConfig.PERSEPHONE_CUP_DEATH_PROTECTION_CHARGE_COST)),
                        OlympusTooltip.property("restored_health", OlympusTooltip.percent(OlympusConfig.PERSEPHONE_CUP_RESTORED_HEALTH_PERCENTAGE)),
                        OlympusTooltip.property("regeneration", OlympusTooltip.seconds(OlympusConfig.PERSEPHONE_CUP_REGENERATION_SECONDS))
                ));

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
        setSoulCharges(cup, MAX_SOUL_CHARGES - OlympusConfig.PERSEPHONE_CUP_DEATH_PROTECTION_CHARGE_COST);
        // Saves the player on a mortal hit
        player.setHealth(player.getMaxHealth() * (float) OlympusConfig.PERSEPHONE_CUP_RESTORED_HEALTH_PERCENTAGE);
        // Particles and sound
        OlympusNetwork.sendToTrackingAndSelf(player, SoulSalvationPayload.TYPE, new SoulSalvationPayload(player.getId(), 16, false));
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_THUNDER, SoundSource.PLAYERS, 0.3F, 1);

        final int regenerationTicks = OlympusConfig.secondsToTicks(OlympusConfig.PERSEPHONE_CUP_REGENERATION_SECONDS);
        if (regenerationTicks > 0) {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, regenerationTicks, 1));
        }

        return true;
    }

    public static void spawnSoulOnKill(final Mob mob, final ServerPlayer player) {
        if (!(mob.level() instanceof ServerLevel level) || findEquippedCup(player).isEmpty() || mob.getPersistentData().getBoolean(TAG_SOUL_DROPPED)) {
            return;
        }

        // Spawns just a single absorbed soul
        mob.getPersistentData().putBoolean(TAG_SOUL_DROPPED, true);
        level.addFreshEntity(new AbsorbedSoulEntity(level, mob.position().add(0, mob.getBbHeight() * 0.5, 0), player));
    }

    public static int getSoulCharges(final ItemStack stack) {
        return Mth.clamp(stack.hasTag() ? stack.getTag().getInt(TAG_SOUL_CHARGES) : 0, 0, MAX_SOUL_CHARGES);
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
        if (clampedCharges == 0) {
            stack.removeTagKey(TAG_SOUL_CHARGES);
        }
        else {
            stack.getOrCreateTag().putInt(TAG_SOUL_CHARGES, clampedCharges);
        }

        if (clampedCharges == 0) {
            stack.removeTagKey("CustomModelData");
        }
        else {
            stack.getOrCreateTag().putInt("CustomModelData", clampedCharges);
        }

    }

    private static Optional<ItemStack> findEquippedCup(final ServerPlayer player) {
        return CuriosApi.getCuriosInventory(player)
                .resolve()
                .flatMap(handler -> handler.findFirstCurio(OlympusItems.PERSEPHONE_CUP.get()))
                .map(SlotResult::stack);
    }

}
