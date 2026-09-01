package dev.xylonity.olympus.client.entity.renderer;

import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import dev.xylonity.olympus.client.entity.model.HarpyModel;
import dev.xylonity.olympus.common.entity.HarpyEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.Mth;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class HarpyEntityRenderer extends GeoEntityRenderer<HarpyEntity, EntityRenderState> {

    private static final DataTicket<Float> HEAD_YAW = DataTicket.create("olympus_harpy_head_yaw", Float.class);
    private static final DataTicket<Float> HEAD_PITCH = DataTicket.create("olympus_harpy_head_pitch", Float.class);

    public HarpyEntityRenderer(final EntityRendererProvider.Context context) {
        this(context, false);
    }

    public HarpyEntityRenderer(final EntityRendererProvider.Context context, final boolean elite) {
        super(context, new HarpyModel(elite));
        withScale(0.9F);
        shadowRadius = 0.45F;
    }

    @Override
    public void addRenderData(final HarpyEntity animatable, final @Nullable Void relatedObject, final EntityRenderState renderState, final float partialTick) {
        final float headYaw = Mth.rotLerp(partialTick, animatable.yHeadRotO, animatable.yHeadRot);
        final float bodyYaw = Mth.rotLerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);
        final GeoRenderState geoRenderState = (GeoRenderState) renderState;

        geoRenderState.addGeckolibData(HEAD_YAW, Mth.wrapDegrees(headYaw - bodyYaw));
        geoRenderState.addGeckolibData(HEAD_PITCH, animatable.getXRot(partialTick));
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void adjustModelBonesForRender(final @NonNull RenderPassInfo renderPassInfo, final BoneSnapshots snapshots) {
        snapshots.get("head").ifPresent(head -> {
            final float yaw = Mth.clamp((Float) renderPassInfo.getOrDefaultGeckolibData(HEAD_YAW, 0.0F), -55, 55);
            final float pitch = Mth.clamp((Float) renderPassInfo.getOrDefaultGeckolibData(HEAD_PITCH, 0.0F), -35, 35);

            head.setRotX(head.getRotX() - pitch * Mth.DEG_TO_RAD);
            head.setRotY(head.getRotY() - yaw * Mth.DEG_TO_RAD);
        });

    }

}
