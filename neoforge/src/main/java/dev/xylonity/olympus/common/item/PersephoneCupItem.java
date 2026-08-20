package dev.xylonity.olympus.common.item;

import dev.xylonity.olympus.Olympus;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.TooltipDisplay;
import top.theillusivec4.curios.api.CurioAttributeModifiers;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public final class PersephoneCupItem extends Item implements ICurioItem {

    private static final String TAG_SOUL_CHARGES = "olympus_soul_charges";

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

}