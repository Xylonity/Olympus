package dev.xylonity.olympus.client.entity.renderer;

import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import dev.xylonity.olympus.client.entity.model.SummoningSpearsModel;
import dev.xylonity.olympus.common.entity.SummoningSpearsEntity;
import dev.xylonity.olympus.registry.OlympusRenderTypes;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;

public final class SummoningSpearsEntityRenderer extends GeoEntityRenderer<SummoningSpearsEntity, EntityRenderState> {

    public SummoningSpearsEntityRenderer(final EntityRendererProvider.Context context) {
        super(context, new SummoningSpearsModel());
        shadowRadius = 0.0F;
    }

    @Override
    public int getRenderColor(final SummoningSpearsEntity animatable, final @Nullable Void relatedObject, final float partialTick) {
        final int visibility = Mth.clamp(Math.round(animatable.getDissolveVisibility(partialTick) * 255.0F), 0, 255);
        return visibility << 24 | 0xFFFFFF;
    }

    @Override
    public @Nullable RenderType getRenderType(final EntityRenderState renderState, final Identifier texture) {
        // Switches to the dissolve shader when alpha starts decreasing
        final int renderColor = ((GeoRenderState) renderState).getOrDefaultGeckolibData(DataTickets.RENDER_COLOR, 0xFFFFFFFF);
        return renderColor >>> 24 < 255 ? OlympusRenderTypes.aresSpearDissolve(texture) : RenderTypes.entityCutout(texture);
    }

}
