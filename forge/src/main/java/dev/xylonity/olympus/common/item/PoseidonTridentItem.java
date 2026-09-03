package dev.xylonity.olympus.common.item;

import dev.xylonity.knightlib.api.animation.KnightLibAnim;
import dev.xylonity.knightlib.api.animation.KnightLibItemAnimationControllerRegistrar;
import dev.xylonity.knightlib.api.item.KnightLibRenderedItem;
import dev.xylonity.olympus.client.item.renderer.PoseidonTridentItemRenderer;
import dev.xylonity.olympus.common.entity.projectile.PoseidonTridentEntity;
import dev.xylonity.olympus.common.util.OlympusTooltip;
import dev.xylonity.olympus.config.OlympusConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import java.util.List;
import java.util.function.Supplier;

public final class PoseidonTridentItem extends TridentItem implements KnightLibRenderedItem {

    private static final KnightLibAnim IDLE = KnightLibAnim.begin().thenLoop("idle");

    public PoseidonTridentItem(final Properties properties) {
        super(properties);
    }

    @Override
    public void releaseUsing(final ItemStack stack, final Level level, final LivingEntity user, final int remainingUseDuration) {
        if (!(user instanceof Player player)) {
            return;
        }

        final int useTicks = getUseDuration(stack) - remainingUseDuration;
        if (useTicks < THROW_THRESHOLD_TIME || stack.getDamageValue() >= stack.getMaxDamage() - 1) {
            return;
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!(level instanceof ServerLevel)) {
            return;
        }

        stack.hurtAndBreak(1, player, entity -> entity.broadcastBreakEvent(player.getUsedItemHand()));
        final ItemStack poseidonTrident = stack.copy();
        poseidonTrident.setCount(1);

        final PoseidonTridentEntity trident = new PoseidonTridentEntity(level, player, poseidonTrident);
        trident.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, SHOOT_POWER, 1.0F);
        if (player.getAbilities().instabuild) {
            trident.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        }
        else {
            stack.shrink(1);
        }

        level.addFreshEntity(trident);

        level.playSound(null, trident, SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
        final ItemStack stack = player.getItemInHand(hand);
        if (stack.getDamageValue() >= stack.getMaxDamage() - 1) {
            return InteractionResultHolder.fail(stack);
        }

        player.startUsingItem(hand);

        return InteractionResultHolder.consume(stack);
    }

    @Override
    public boolean canApplyAtEnchantingTable(final ItemStack stack, final Enchantment enchantment) {
        return enchantment == Enchantments.CHANNELING
                || enchantment == Enchantments.IMPALING || enchantment == Enchantments.MENDING || enchantment == Enchantments.KNOCKBACK
                || enchantment == Enchantments.UNBREAKING || enchantment == Enchantments.MOB_LOOTING;
    }

    @Override
    public void appendHoverText(final ItemStack stack, final Level level, final List<Component> tooltip, final TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        OlympusTooltip.append(tooltip::add, "poseidon_trident", 0x64B5E8,
                OlympusTooltip.ability(1,
                        OlympusTooltip.property("swim_speed", "+" + OlympusTooltip.percent(OlympusConfig.POSEIDON_TRIDENT_SWIM_SPEED_BONUS))
                ),
                OlympusTooltip.ability(2,
                        OlympusTooltip.property("projectile_damage", OlympusTooltip.number(OlympusConfig.POSEIDON_TRIDENT_PROJECTILE_DAMAGE)),
                        OlympusTooltip.property("splash_damage", OlympusTooltip.number(OlympusConfig.POSEIDON_TRIDENT_SPLASH_DAMAGE)),
                        OlympusTooltip.property("splash_radius", OlympusTooltip.number(OlympusConfig.POSEIDON_TRIDENT_SPLASH_RADIUS))
                ));

    }

    @Override
    public void registerAnimationControllers(final KnightLibItemAnimationControllerRegistrar controllers) {
        controllers.add("idle", state -> IDLE);
    }

    @Override
    public Supplier<Object> rendererFactory() {
        return PoseidonTridentItemRenderer::new;
    }

}
