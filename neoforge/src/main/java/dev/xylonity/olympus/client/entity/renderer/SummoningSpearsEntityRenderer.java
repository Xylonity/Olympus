package dev.xylonity.olympus.client.entity.renderer;

import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
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

    private static final DataTicket<Long> SPEAR_GROUND_STATES = DataTicket.create("olympus:spear_ground_states", Long.class);

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
    public void addRenderData(final SummoningSpearsEntity animatable, final @Nullable Void relatedObject, final EntityRenderState renderState, final float partialTick) {
        ((GeoRenderState) renderState).addGeckolibData(SPEAR_GROUND_STATES, animatable.getSpearGroundStates());
    }

    @Override
    public void adjustModelBonesForRender(final RenderPassInfo renderPassInfo, final BoneSnapshots snapshots) {
        final long groundStates = renderPassInfo.renderState().getOrDefaultGeckolibData(SPEAR_GROUND_STATES, 0L);
        for (int index = 0; index < 12; index++) {
            // Moves each spear upward or downward based on the ground state computed on the first tick on the entity itself
            // Each spear weights five bits so this shifts its segment to the right and masks out every other state
            final int groundState = (int) ((groundStates >>> (index * 5)) & 0x1F);
            snapshots.ifPresent("spear_of_ares_" + (index + 1), snapshot -> {
                if (groundState == 0) {
                    snapshot.skipRender(true);
                    return;
                }

                snapshot.setTranslateY(snapshot.getTranslateY() + (groundState - 12) * 4);
            });
        }

    }

    @Override
    public @Nullable RenderType getRenderType(final EntityRenderState renderState, final Identifier texture) {
        // Switches to the dissolve shader when alpha starts decreasing
        final int renderColor = ((GeoRenderState) renderState).getOrDefaultGeckolibData(DataTickets.RENDER_COLOR, 0xFFFFFFFF);
        return renderColor >>> 24 < 255 ? OlympusRenderTypes.aresSpearDissolve(texture) : RenderTypes.entityCutout(texture);
    }

}
