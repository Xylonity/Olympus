package dev.xylonity.olympus.client.util;

import dev.xylonity.olympus.Olympus;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.util.context.ContextKey;

public final class HadesInvisibilityRenderState {

    public static final ContextKey<Boolean> ACTIVE = new ContextKey<>(Olympus.of("hades_invisibility_active"));

    public static boolean isActive(final AvatarRenderState renderState) {
        return Boolean.TRUE.equals(renderState.getRenderData(ACTIVE));
    }

}