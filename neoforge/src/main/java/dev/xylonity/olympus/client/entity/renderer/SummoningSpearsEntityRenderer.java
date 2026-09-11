package dev.xylonity.olympus.client.entity.renderer;

import dev.xylonity.knightlib.client.animation.KnightLibAnimationSource;
import dev.xylonity.knightlib.client.animation.KnightLibModelSource;
import dev.xylonity.knightlib.client.animation.model.KnightLibModel;
import dev.xylonity.knightlib.client.animation.renderer.KnightLibEntityRenderer;
import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.client.texture.SpearDissolveTextures;
import dev.xylonity.olympus.common.entity.projectile.SummoningSpearsEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.NonNull;

public final class SummoningSpearsEntityRenderer extends KnightLibEntityRenderer<SummoningSpearsEntity> {

    public SummoningSpearsEntityRenderer(final EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0.0F;
    }

    @Override
    protected KnightLibModelSource defineModel(final SummoningSpearsEntity entity) {
        return KnightLibModelSource.geo(Olympus.of("geckolib/models/entity/summoning_spears.geo.json"));
    }

    @Override
    protected KnightLibAnimationSource defineAnimations(final SummoningSpearsEntity entity) {
        return KnightLibAnimationSource.geo(Olympus.of("animations/entity/summoning_spears.animation.json"));
    }

    @Override
    public @NonNull ResourceLocation getTextureLocation(final @NonNull SummoningSpearsEntity entity) {
        return Olympus.of("textures/item/spear_of_ares.png");
    }

    @Override
    protected void setupBone(final SummoningSpearsEntity entity, final KnightLibModel model, final String boneName, final float partialTick) {
        if (!boneName.startsWith("spear_of_ares_")) {
            return;
        }

        final int index;
        try {
            index = Integer.parseInt(boneName.substring("spear_of_ares_".length())) - 1;
        }
        catch (final NumberFormatException exception) {
            return;
        }

        if (index < 0 || index >= 12) {
            return;
        }

        final int groundState = (int) ((entity.getSpearGroundStates() >>> (index * 5)) & 0x1F);
        if (groundState == 0) {
            model.setBoneVisible(boneName, false);
            return;
        }

        model.applyPosition(boneName, 0, (groundState - 12) * 4f, 0);
    }

    @Override
    protected RenderType getRenderType(final SummoningSpearsEntity entity, final ResourceLocation texture) {
        return RenderType.entityCutout(SpearDissolveTextures.textureFor(texture, entity.getDissolveVisibility(0.0F)));
    }

}
