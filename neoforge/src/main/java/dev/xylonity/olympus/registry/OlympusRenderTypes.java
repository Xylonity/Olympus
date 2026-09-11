package dev.xylonity.olympus.registry;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public final class OlympusRenderTypes extends RenderType {

    private OlympusRenderTypes(final String name, final VertexFormat format, final VertexFormat.Mode mode, final int bufferSize, final boolean affectsCrumbling, final boolean sortOnUpload, final Runnable setupState, final Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
        throw new UnsupportedOperationException();
    }

    /// Based off my own inverted cubes render type (if the code is not visible then the mod is not public yet)
    /// https://github.com/Xylonity/Arcane/blob/v1.20.1/common/src/main/java/dev/xylonity/arcane/registry/ArcaneRenderTypes.java#16
    private static RenderType createInvertedCubeGlow(final String name, final ResourceLocation texture) {
        final CompositeState state = CompositeState.builder()
                .setTextureState(new TextureStateShard(texture, false, false))
                .setShaderState(RENDERTYPE_EYES_SHADER)
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setCullState(CULL)
                .setDepthTestState(LEQUAL_DEPTH_TEST)
                .setWriteMaskState(COLOR_DEPTH_WRITE)
                .createCompositeState(false);

        return RenderType.create(name, DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, state);
    }

    /// Rendertype applied to a simple spherical geom, mostly created to make it actually visible underwater
    private static final RenderType UNDERWATER_SPLASH = RenderType.create(
            "olympus_underwater_splash",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS,
            65536,
            false,
            true,
            CompositeState.builder()
                    .setShaderState(POSITION_COLOR_SHADER)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .setDepthTestState(LEQUAL_DEPTH_TEST)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(false)
    );

    private static final RenderType LIGHTNING_BOLT = RenderType.create(
            "olympus_lightning_bolt",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.TRIANGLES,
            1536,
            false,
            false,
            CompositeState.builder()
                    .setShaderState(RENDERTYPE_LIGHTNING_SHADER)
                    .setWriteMaskState(COLOR_WRITE)
                    .setTransparencyState(LIGHTNING_TRANSPARENCY)
                    .createCompositeState(false)
    );

    private static final Function<ResourceLocation, RenderType> INVERTED_CUBES_GLOW = Util.memoize(texture ->
            createInvertedCubeGlow("olympus_inverted_cube_glow", texture)
    );

    private static final Function<ResourceLocation, RenderType> FIRST_PERSON_INVERTED_CUBES_GLOW = Util.memoize(texture ->
            createInvertedCubeGlow("olympus_first_person_inverted_cube_glow", texture)
    );

    public static RenderType invertedCubesGlow(final ResourceLocation texture) {
        return INVERTED_CUBES_GLOW.apply(texture);
    }

    public static RenderType firstPersonInvertedCubesGlow(final ResourceLocation texture) {
        return FIRST_PERSON_INVERTED_CUBES_GLOW.apply(texture);
    }

    public static RenderType lightningBolt() {
        return LIGHTNING_BOLT;
    }

    public static RenderType translucentEntityComposite(final ResourceLocation texture) {
        return RenderType.itemEntityTranslucentCull(texture);
    }

    public static RenderType underwaterSplash() {
        return UNDERWATER_SPLASH;
    }

}