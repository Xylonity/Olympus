package dev.xylonity.olympus.mixins;

import dev.xylonity.olympus.registry.OlympusItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/// Doesn't lower the spear when attacking
@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {

    @Shadow @Final private Minecraft minecraft;

    @ModifyVariable(method = "tick", at = @At(value = "STORE", ordinal = 0), ordinal = 0, require = 0)
    private float olympus$keepAresSpearEquippedDuringAttack(final float attackEquipScale) {
        final LocalPlayer player = this.minecraft.player;
        return player != null && player.getMainHandItem().is(OlympusItems.SPEAR_OF_ARES.get()) ? 1 : attackEquipScale;
    }

}
