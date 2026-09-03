package dev.xylonity.olympus.client.entity.renderer;

import dev.xylonity.knightlib.client.animation.KnightLibModelSource;
import dev.xylonity.knightlib.client.animation.layer.KnightLibRenderLayer;
import dev.xylonity.knightlib.client.animation.model.KnightLibModel;
import dev.xylonity.knightlib.client.animation.renderer.KnightLibEntityRenderer;
import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.common.entity.projectile.AbsorbedSoulEntity;
import dev.xylonity.olympus.registry.OlympusRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;

public final class AbsorbedSoulEntityRenderer extends KnightLibEntityRenderer<AbsorbedSoulEntity> {

    private static final ResourceLocation TEXTURE = Olympus.of("textures/entity/absorbed_soul.png");

    private static final RenderType RENDER_TYPE_INVERTED_CUBES = OlympusRenderTypes.invertedCubesGlow(TEXTURE);

    private static final String OUTLINE_BONE = "cube_outline";
    private static final Set<String> OUTLINE_BONES = Set.of(OUTLINE_BONE);

    public AbsorbedSoulEntityRenderer(final EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0.1F;
        shadowStrength = 0.7F;

        // Inverted cubes glow
        addRenderLayer(new KnightLibRenderLayer<>() {
            @Override
            public void render(final dev.xylonity.knightlib.client.animation.layer.KnightLibRenderLayerContext<AbsorbedSoulEntity> context) {
                context.model().setBoneVisible(OUTLINE_BONE, true);
                try {
                    context.renderBones(OUTLINE_BONES, RENDER_TYPE_INVERTED_CUBES,
                            LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, context.renderColor());
                }
                finally {
                    context.model().setBoneVisible(OUTLINE_BONE, false);
                }

            }
        });

    }

    @Override
    protected KnightLibModelSource defineModel(final AbsorbedSoulEntity entity) {
        return KnightLibModelSource.geo(Olympus.of("geckolib/models/entity/absorbed_soul.geo.json"));
    }

    //@Override
    //protected RenderType getRenderType(final AbsorbedSoulEntity entity, final ResourceLocation texture) {
    //    return RENDER_TYPE_BASE;
    //}

    @Override
    protected void setupBone(final AbsorbedSoulEntity entity, final KnightLibModel model, final String boneName, final float partialTick) {
        if (OUTLINE_BONE.equals(boneName)) {
            model.setBoneVisible(OUTLINE_BONE, false);
        }

    }

    @Override
    protected int getBlockLightLevel(final AbsorbedSoulEntity entity, final BlockPos blockPos) {
        return 15;
    }

    @Override
    protected int getSkyLightLevel(final AbsorbedSoulEntity entity, final BlockPos blockPos) {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(final AbsorbedSoulEntity entity) {
        return TEXTURE;
    }

}