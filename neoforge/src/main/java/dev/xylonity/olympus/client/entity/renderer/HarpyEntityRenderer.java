package dev.xylonity.olympus.client.entity.renderer;

import com.geckolib.renderer.GeoEntityRenderer;
import dev.xylonity.olympus.client.entity.model.HarpyModel;
import dev.xylonity.olympus.common.entity.HarpyEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public final class HarpyEntityRenderer extends GeoEntityRenderer<HarpyEntity, EntityRenderState> {

    public HarpyEntityRenderer(final EntityRendererProvider.Context context) {
        super(context, new HarpyModel());
        withScale(0.9F);
        shadowRadius = 0.45F;
    }

}
