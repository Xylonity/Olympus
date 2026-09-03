package dev.xylonity.olympus.client.entity.renderer;

import dev.xylonity.knightlib.client.animation.KnightLibAnimationSource;
import dev.xylonity.knightlib.client.animation.KnightLibModelSource;
import dev.xylonity.knightlib.client.animation.model.KnightLibModel;
import dev.xylonity.knightlib.client.animation.renderer.KnightLibMobRenderer;
import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.common.entity.HarpyEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class HarpyEntityRenderer extends KnightLibMobRenderer<HarpyEntity> {

    private final boolean elite;

    public HarpyEntityRenderer(final EntityRendererProvider.Context context) {
        this(context, false);
    }

    public HarpyEntityRenderer(final EntityRendererProvider.Context context, final boolean elite) {
        super(context, 0.45F);
        this.elite = elite;
    }

    @Override
    protected KnightLibModelSource defineModel(final HarpyEntity entity) {
        return KnightLibModelSource.geo(Olympus.of("geckolib/models/entity/" + (elite ? "elite_harpy" : "harpy") + ".geo.json"));
    }

    @Override
    protected KnightLibAnimationSource defineAnimations(final HarpyEntity entity) {
        return KnightLibAnimationSource.geo(Olympus.of("animations/entity/harpy.animation.json"));
    }

    @Override
    public ResourceLocation getTextureLocation(final HarpyEntity entity) {
        return Olympus.of("textures/entity/" + (elite ? "elite_harpy" : "harpy") + ".png");
    }

    @Override
    protected float getScale(final HarpyEntity entity) {
        return 0.9F;
    }

    @Override
    protected void setupPose(final HarpyEntity entity, final KnightLibModel model, final float limbSwing, final float limbSwingAmount, final float partialTick, final float netHeadYaw, final float headPitch) {
        model.applyRotation("head", Mth.clamp(headPitch, -35, 35), Mth.clamp(netHeadYaw, -55, 55), 0);
    }

}