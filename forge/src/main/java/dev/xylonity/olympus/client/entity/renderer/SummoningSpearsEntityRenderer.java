package dev.xylonity.olympus.client.entity.renderer;

import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import dev.xylonity.olympus.client.entity.model.SummoningSpearsModel;
import dev.xylonity.olympus.client.texture.SpearDissolveTextures;
import dev.xylonity.olympus.common.entity.projectile.SummoningSpearsEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public final class SummoningSpearsEntityRenderer extends GeoEntityRenderer<SummoningSpearsEntity, EntityRenderState> {

    private static final DataTicket<Long> SPEAR_GROUND_STATES = DataTicket.create("olympus_spear_ground_states", Long.class);
    private static final DataTicket<Float> DISSOLVE_VISIBILITY = DataTicket.create("olympus_summoning_spears_dissolve_visibility", Float.class);

    public SummoningSpearsEntityRenderer(final EntityRendererProvider.Context context) {
        super(context, new SummoningSpearsModel());
        shadowRadius = 0;
    }

    @Override
    public int getRenderColor(final SummoningSpearsEntity animatable, final @Nullable Void relatedObject, final float partialTick) {
        return 0xFFFFFFFF;
    }

    @Override
    public void addRenderData(final SummoningSpearsEntity animatable, final @Nullable Void relatedObject, final EntityRenderState renderState, final float partialTick) {
        ((GeoRenderState) renderState).addGeckolibData(SPEAR_GROUND_STATES, animatable.getSpearGroundStates());
        ((GeoRenderState) renderState).addGeckolibData(DISSOLVE_VISIBILITY, animatable.getDissolveVisibility(partialTick));
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
        final float visibility = ((GeoRenderState) renderState).getOrDefaultGeckolibData(DISSOLVE_VISIBILITY, 1f);
        return RenderTypes.entityCutout(SpearDissolveTextures.textureFor(texture, visibility));
    }

}
