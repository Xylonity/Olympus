package dev.xylonity.olympus.registry;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.mixins.RenderTypeAccessor;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

public final class OlympusRenderTypes {

    /// Based off my own inverted cubes render type (if the code is not visible then the mod is not public yet)
    /// https://github.com/Xylonity/Arcane/blob/v1.20.1/common/src/main/java/dev/xylonity/arcane/registry/ArcaneRenderTypes.java#16
    public static final RenderPipeline INVERTED_CUBE_GLOW_PIPELINE = RenderPipeline.builder()
            .withLocation(Olympus.of("pipeline/inverted_cube_glow"))
            .withVertexShader("core/entity")
            .withFragmentShader("core/entity")
            .withShaderDefine("EMISSIVE")
            .withShaderDefine("NO_OVERLAY")
            .withShaderDefine("NO_CARDINAL_LIGHTING")
            .withSampler("Sampler0")
            .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
            .withUniform("Projection", UniformType.UNIFORM_BUFFER)
            .withUniform("Fog", UniformType.UNIFORM_BUFFER)
            .withColorTargetState(new ColorTargetState(
                    Optional.of(BlendFunction.TRANSLUCENT),
                    ColorTargetState.WRITE_COLOR
            ))
            .withCull(true)
            .withVertexFormat(DefaultVertexFormat.ENTITY, VertexFormat.Mode.QUADS)
            .withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false))
            .build();

    /// Based off my own lightning/thunder render type (if the code is not visible then the mod is not public yet)
    /// https://github.com/Xylonity/Hostiles/blob/v1.20.1/common/src/main/java/dev/xylonity/hostiles/registry/HostilesRenderTypes.java#36
    public static final RenderPipeline LIGHTNING_BOLT_PIPELINE = RenderPipeline.builder()
            .withLocation(Olympus.of("pipeline/lightning_bolt"))
            .withVertexShader("core/rendertype_lightning")
            .withFragmentShader("core/rendertype_lightning")
            .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
            .withUniform("Projection", UniformType.UNIFORM_BUFFER)
            .withUniform("Fog", UniformType.UNIFORM_BUFFER)
            .withColorTargetState(new ColorTargetState(BlendFunction.LIGHTNING))
            .withCull(false)
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
            .withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false))
            .build();

    /// Render type applied to a simple spherical geom, mostly created to make it actually visible underwater
    public static final RenderPipeline UNDERWATER_SPLASH_PIPELINE = RenderPipeline.builder()
            .withLocation(Olympus.of("pipeline/underwater_splash"))
            .withVertexShader("core/position_color")
            .withFragmentShader("core/position_color")
            .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
            .withUniform("Projection", UniformType.UNIFORM_BUFFER)
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withCull(false)
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
            .withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false))
            .build();

    /// Dithering pixelation (dissolver) applied to the ares spear
    public static final RenderPipeline ARES_SPEAR_DISSOLVE_PIPELINE = RenderPipeline.builder()
            .withLocation(Olympus.of("pipeline/ares_spear_dissolve"))
            .withVertexShader("core/entity")
            .withFragmentShader(Olympus.of("core/ares_spear_dissolve"))
            .withSampler("Sampler0")
            .withSampler("Sampler1")
            .withSampler("Sampler2")
            .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
            .withUniform("Projection", UniformType.UNIFORM_BUFFER)
            .withUniform("Fog", UniformType.UNIFORM_BUFFER)
            .withColorTargetState(ColorTargetState.DEFAULT)
            .withCull(true)
            .withVertexFormat(DefaultVertexFormat.ENTITY, VertexFormat.Mode.QUADS)
            .withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, true))
            .build();

    private static final Function<Identifier, RenderType> INVERTED_CUBE_GLOW = Util.memoize(texture ->
            RenderTypeAccessor.olympus$create(
                    "olympus_inverted_cube_glow",
                    RenderSetup.builder(INVERTED_CUBE_GLOW_PIPELINE)
                            .withTexture("Sampler0", texture)
                            .sortOnUpload()
                            .bufferSize(256)
                            .createRenderSetup()
            )

    );

    private static final RenderType LIGHTNING_BOLT = RenderTypeAccessor.olympus$create(
            "olympus_lightning_bolt",
            RenderSetup.builder(LIGHTNING_BOLT_PIPELINE)
                    .setOutputTarget(OutputTarget.WEATHER_TARGET)
                    .sortOnUpload()
                    .createRenderSetup()
    );

    private static final RenderType UNDERWATER_SPLASH = RenderTypeAccessor.olympus$create(
            "olympus_underwater_splash",
            RenderSetup.builder(UNDERWATER_SPLASH_PIPELINE)
                    .sortOnUpload()
                    .bufferSize(65536)
                    .createRenderSetup()
    );

    private static final Function<Identifier, RenderType> ARES_SPEAR_DISSOLVE = Util.memoize(texture ->
            RenderTypeAccessor.olympus$create(
                    "olympus_ares_spear_dissolve",
                    RenderSetup.builder(ARES_SPEAR_DISSOLVE_PIPELINE)
                            .withTexture("Sampler0", texture)
                            .useLightmap()
                            .useOverlay()
                            .bufferSize(256)
                            .createRenderSetup()
            )

    );

    public static RenderType invertedCubeGlow(final Identifier texture) {
        return INVERTED_CUBE_GLOW.apply(texture);
    }

    public static RenderType lightningBolt() {
        return LIGHTNING_BOLT;
    }

    public static RenderType underwaterSplash() {
        return UNDERWATER_SPLASH;
    }

    public static RenderType aresSpearDissolve(final Identifier texture) {
        return ARES_SPEAR_DISSOLVE.apply(texture);
    }

}