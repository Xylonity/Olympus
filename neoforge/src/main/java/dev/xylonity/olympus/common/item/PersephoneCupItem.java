package dev.xylonity.olympus.common.item;

import dev.xylonity.olympus.common.entity.projectile.AbsorbedSoulEntity;
import dev.xylonity.olympus.common.util.OlympusTooltip;
import dev.xylonity.olympus.config.OlympusConfig;
import dev.xylonity.olympus.network.payload.SoulSalvationPayload;
import dev.xylonity.olympus.network.OlympusNetwork;
import dev.xylonity.olympus.registry.OlympusItems;
import java.util.List;
import java.util.Optional;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;
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
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(final SlotContext slotContext, final ResourceLocation uuid, final ItemStack stack) {
        final int soulCharges = getSoulCharges(stack);
        if (!OlympusConfig.PERSEPHONE_CUP_SOUL_HARVEST_ENABLED || soulCharges == 0 || OlympusConfig.PERSEPHONE_CUP_DAMAGE_APPLIES_TO_WEAPONS) {
            return HashMultimap.create();
        }

        // Additional configurable damage per soul acquired
        final double damagePerSoul = OlympusConfig.PERSEPHONE_CUP_DAMAGE_PER_SOUL;
        final Multimap<Holder<Attribute>, AttributeModifier> modifiers = HashMultimap.create();
        modifiers.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(uuid, soulCharges * damagePerSoul, AttributeModifier.Operation.ADD_VALUE));
        return modifiers;
    }

    public static float getEquippedDamageBonus(final ServerPlayer player) {
        if (!OlympusConfig.PERSEPHONE_CUP_SOUL_HARVEST_ENABLED) {
            return 0;
        }

        final float damagePerSoul = (float) OlympusConfig.PERSEPHONE_CUP_DAMAGE_PER_SOUL;
        return findEquippedCup(player)
                .map(cup -> getSoulCharges(cup) * damagePerSoul)
                .orElse(0f);
    }

    @Override
    public void appendHoverText(final ItemStack stack, final Item.TooltipContext context, final List<Component> builder, final TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, builder, tooltipFlag);
        final Component soulCharges = Component.translatable("item.olympus.persephone_cup.soul_charges", getSoulCharges(stack), MAX_SOUL_CHARGES).withStyle(ChatFormatting.DARK_PURPLE);
        OlympusTooltip.appendWithStatus(builder::add, "persephone_cup", 0xC987D4, soulCharges,
                OlympusTooltip.abilityIf(OlympusConfig.PERSEPHONE_CUP_SOUL_HARVEST_ENABLED, 1,
                        OlympusTooltip.property("damage_per_soul", "+" + OlympusTooltip.number(OlympusConfig.PERSEPHONE_CUP_DAMAGE_PER_SOUL)),
                        OlympusTooltip.property("maximum_souls", Integer.toString(MAX_SOUL_CHARGES))
                ),
                OlympusTooltip.abilityIf(OlympusConfig.PERSEPHONE_CUP_SPRINGS_RETURN_ENABLED, 2,
                        OlympusTooltip.property("charge_cost", Integer.toString(OlympusConfig.PERSEPHONE_CUP_DEATH_PROTECTION_CHARGE_COST)),
                        OlympusTooltip.property("restored_health", OlympusTooltip.percent(OlympusConfig.PERSEPHONE_CUP_RESTORED_HEALTH_PERCENTAGE)),
                        OlympusTooltip.property("regeneration", OlympusTooltip.seconds(OlympusConfig.PERSEPHONE_CUP_REGENERATION_SECONDS))
                ));

    }

    /// Applies the special effect of the cup (increased damage and canceled first mortal hit on max charge amount)
    public static boolean tryActivateAbility(final ServerPlayer player) {
        if (!OlympusConfig.PERSEPHONE_CUP_SPRINGS_RETURN_ENABLED) {
            return false;
        }

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
        if (!OlympusConfig.PERSEPHONE_CUP_SOUL_HARVEST_ENABLED || !(mob.level() instanceof ServerLevel level) || findEquippedCup(player).isEmpty() || mob.getPersistentData().getBoolean(TAG_SOUL_DROPPED)) {
            return;
        }

        // Spawns just a single absorbed soul
        mob.getPersistentData().putBoolean(TAG_SOUL_DROPPED, true);
        level.addFreshEntity(new AbsorbedSoulEntity(level, mob.position().add(0, mob.getBbHeight() * 0.5, 0), player));
    }

    public static int getSoulCharges(final ItemStack stack) {
        return Mth.clamp(stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getInt(TAG_SOUL_CHARGES), 0, MAX_SOUL_CHARGES);
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
            CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.remove(TAG_SOUL_CHARGES));
        }
        else {
            CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putInt(TAG_SOUL_CHARGES, clampedCharges));
        }

        if (clampedCharges == 0) {
            stack.remove(DataComponents.CUSTOM_MODEL_DATA);
        }
        else {
            stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(clampedCharges));
        }

    }

    private static Optional<ItemStack> findEquippedCup(final ServerPlayer player) {
        return CuriosApi.getCuriosInventory(player)
                .flatMap(handler -> handler.findFirstCurio(OlympusItems.PERSEPHONE_CUP.get()))
                .map(SlotResult::stack);
    }

}
