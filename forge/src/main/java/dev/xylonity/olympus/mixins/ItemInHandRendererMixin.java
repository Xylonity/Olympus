package dev.xylonity.olympus.mixins;

import dev.xylonity.olympus.registry.OlympusItems;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/// Doesn't lower the spear when attacking
@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"
            )
    )
    private float olympus$keepAresSpearEquippedDuringAttack(final LocalPlayer player, final float partialTick) {
        return player.getMainHandItem().is(OlympusItems.SPEAR_OF_ARES.get()) ? 1 : player.getAttackStrengthScale(partialTick);
    }

}
