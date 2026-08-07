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

    public static RenderType invertedCubeGlow(final Identifier texture) {
        return INVERTED_CUBE_GLOW.apply(texture);
    }

    public static RenderType lightningBolt() {
        return LIGHTNING_BOLT;
    }

}