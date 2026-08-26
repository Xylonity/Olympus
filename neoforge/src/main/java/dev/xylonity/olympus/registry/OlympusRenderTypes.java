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
import java.util.function.Function;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

public final class OlympusRenderTypes {

    public static final RenderPipeline INVERTED_CUBE_GLOW_PIPELINE = createInvertedCubeGlowPipeline("inverted_cube_glow");
    public static final RenderPipeline FIRST_PERSON_INVERTED_CUBE_GLOW_PIPELINE = createInvertedCubeGlowPipeline("first_person_inverted_cube_glow");

    /// Based off my own inverted cubes render type (if the code is not visible then the mod is not public yet)
    /// https://github.com/Xylonity/Arcane/blob/v1.20.1/common/src/main/java/dev/xylonity/arcane/registry/ArcaneRenderTypes.java#16
    private static RenderPipeline createInvertedCubeGlowPipeline(final String name) {
        return RenderPipeline.builder()
            .withLocation(Olympus.of("pipeline/" + name))
            .withVertexShader("core/entity")
            .withFragmentShader("core/entity")
            .withShaderDefine("ALPHA_CUTOUT", 0.1F)
            .withShaderDefine("EMISSIVE")
            .withShaderDefine("NO_OVERLAY")
            .withShaderDefine("NO_CARDINAL_LIGHTING")
            .withSampler("Sampler0")
            .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
            .withUniform("Projection", UniformType.UNIFORM_BUFFER)
            .withUniform("Fog", UniformType.UNIFORM_BUFFER)
            .withColorTargetState(new ColorTargetState(BlendFunction.LIGHTNING))
            .withCull(true)
            .withVertexFormat(DefaultVertexFormat.ENTITY, VertexFormat.Mode.QUADS)
            .withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, true))
            .build();
    }

    /// Rendertype applied to a simple spherical geom, mostly created to make it actually visible underwater
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

    private static final RenderType UNDERWATER_SPLASH = RenderTypeAccessor.olympus$create(
            "olympus_underwater_splash",
            RenderSetup.builder(UNDERWATER_SPLASH_PIPELINE)
                    .sortOnUpload()
                    .bufferSize(65536)
                    .createRenderSetup()
    );

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

    private static final Function<Identifier, RenderType> FIRST_PERSON_INVERTED_CUBE_GLOW = Util.memoize(texture ->
            RenderTypeAccessor.olympus$create(
                    "olympus_first_person_inverted_cube_glow",
                    RenderSetup.builder(FIRST_PERSON_INVERTED_CUBE_GLOW_PIPELINE)
                            .withTexture("Sampler0", texture)
                            .sortOnUpload()
                            .bufferSize(256)
                            .createRenderSetup()
            )

    );

    public static RenderType invertedCubeGlow(final Identifier texture) {
        return INVERTED_CUBE_GLOW.apply(texture);
    }

    public static RenderType firstPersonInvertedCubeGlow(final Identifier texture) {
        return FIRST_PERSON_INVERTED_CUBE_GLOW.apply(texture);
    }

    public static RenderType lightningBolt() {
        return RenderTypes.dragonRays();
    }

    public static RenderType translucentEntityComposite(final Identifier texture) {
        return RenderTypes.entityTranslucentCullItemTarget(texture);
    }

    public static RenderType underwaterSplash() {
        return UNDERWATER_SPLASH;
    }

}