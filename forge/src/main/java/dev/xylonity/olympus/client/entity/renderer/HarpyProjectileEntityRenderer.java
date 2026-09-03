package dev.xylonity.olympus.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xylonity.knightlib.client.KnightLibRenderTypes;
import dev.xylonity.knightlib.client.animation.KnightLibModelSource;
import dev.xylonity.knightlib.client.animation.layer.KnightLibRenderLayer;
import dev.xylonity.knightlib.client.animation.layer.KnightLibRenderLayerContext;
import dev.xylonity.knightlib.client.animation.model.KnightLibModel;
import dev.xylonity.knightlib.client.animation.renderer.KnightLibEntityRenderer;
import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.common.entity.projectile.AbsorbedSoulEntity;
import dev.xylonity.olympus.common.entity.projectile.HarpyProjectileEntity;
import dev.xylonity.olympus.registry.OlympusRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;

public final class HarpyProjectileEntityRenderer extends KnightLibEntityRenderer<HarpyProjectileEntity> {

    private static final ResourceLocation TEXTURE = Olympus.of("textures/entity/harpy_projectile.png");

    private static final RenderType RENDER_TYPE_INVERTED_CUBES = OlympusRenderTypes.invertedCubesGlow(TEXTURE);

    private static final String OUTLINE_BONE = "cube_outline";
    private static final Set<String> OUTLINE_BONES = Set.of(OUTLINE_BONE);

    public HarpyProjectileEntityRenderer(final EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0.1F;
        shadowStrength = 0.7F;

        // Inverted cubes glow
        addRenderLayer(new KnightLibRenderLayer<>() {
            @Override
            public void render(final KnightLibRenderLayerContext<HarpyProjectileEntity> context) {
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
    protected KnightLibModelSource defineModel(HarpyProjectileEntity harpyProjectileEntity) {
        return KnightLibModelSource.geo(Olympus.of("geckolib/models/entity/harpy_projectile.geo.json"));
    }

    @Override
    protected void setupBone(HarpyProjectileEntity entity, KnightLibModel model, String boneName, float partialTicks) {
        if (OUTLINE_BONE.equals(boneName)) {
            model.setBoneVisible(OUTLINE_BONE, false);
        }

    }

    @Override
    protected void actuallyRender(HarpyProjectileEntity entity, KnightLibModel model, ResourceLocation baseTexture, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay, int renderColor) {

        poseStack.popPose();

        poseStack.mulPose(Axis.YP.rotationDegrees((entity.tickCount + partialTicks) * 5));
        poseStack.mulPose(Axis.XP.rotationDegrees((entity.tickCount + partialTicks) * 5));
        poseStack.scale(1.5F, 1.5F, 1.5F);

        poseStack.pushPose();

        super.actuallyRender(entity, model, baseTexture, partialTicks, poseStack, buffers, packedLight, packedOverlay, renderColor);
    }

    @Override
    protected int getBlockLightLevel(final HarpyProjectileEntity entity, final BlockPos blockPos) {
        return 15;
    }

    @Override
    protected int getSkyLightLevel(final HarpyProjectileEntity entity, final BlockPos blockPos) {
        return 15;
    }

    @Override
    protected RenderType getRenderType(HarpyProjectileEntity entity, ResourceLocation texture) {
        return KnightLibRenderTypes.entityEmissive(TEXTURE);
    }

    @Override
    public ResourceLocation getTextureLocation(final HarpyProjectileEntity entity) {
        return TEXTURE;
    }

}