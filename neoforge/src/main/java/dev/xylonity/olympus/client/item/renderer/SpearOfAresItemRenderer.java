package dev.xylonity.olympus.client.item.renderer;

import com.geckolib.animatable.GeoItem;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.base.RenderPassInfo;
import com.mojang.math.Axis;
import dev.xylonity.olympus.client.item.model.SpearOfAresModel;
import dev.xylonity.olympus.common.entity.SpearOfAresEntity;
import dev.xylonity.olympus.common.item.SpearOfAresItem;
import dev.xylonity.olympus.registry.OlympusRenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import org.jspecify.annotations.Nullable;

public final class SpearOfAresItemRenderer extends GeoItemRenderer<SpearOfAresItem> {

    public SpearOfAresItemRenderer() {
        super(new SpearOfAresModel<>());
    }

    @Override
    public void adjustRenderPose(RenderPassInfo<GeoRenderState> renderPassInfo) {
        final LocalPlayer player = Minecraft.getInstance().player;
        final ItemDisplayContext context = renderPassInfo.getGeckolibData(DataTickets.ITEM_RENDER_PERSPECTIVE);
        final long stackId = renderPassInfo.getOrDefaultGeckolibData(DataTickets.ANIMATABLE_INSTANCE_ID, Long.MIN_VALUE);

        if (player != null) {
            if (player.isUsingItem() && GeoItem.getId(player.getUseItem()) == stackId) {
                if (context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND || context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
                    renderPassInfo.poseStack().mulPose(Axis.XP.rotationDegrees(180));
                    renderPassInfo.poseStack().translate(0, -0.75, -1);
                }
                else if (context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
                    renderPassInfo.poseStack().translate(0.5, -0.9, 0.15);
                    renderPassInfo.poseStack().mulPose(Axis.XP.rotationDegrees(-25));
                }
                else if (context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
                    renderPassInfo.poseStack().translate(-0.5, -0.9, 0.15);
                    renderPassInfo.poseStack().mulPose(Axis.XP.rotationDegrees(-25));
                }

            }

        }

        super.adjustRenderPose(renderPassInfo);
    }

    @Override
    public int getRenderColor(final SpearOfAresItem animatable, final @Nullable RenderData renderData, final float partialTick) {
        // Using the spear's dissolve visibility as alpha
        if (renderData != null && renderData.itemOwner() instanceof SpearOfAresEntity spear) {
            final int visibility = Mth.clamp(Math.round(spear.getDissolveVisibility(partialTick) * 255F), 0, 255);
            return visibility << 24 | 0xFFFFFF;
        }

        // Renders regular itemstacks fully opaque
        return 0xFFFFFFFF;
    }

    @Override
    public @Nullable RenderType getRenderType(final GeoRenderState renderState, final Identifier texture) {
        // Switches to the dissolve shader when alpha starts decreasing
        final int renderColor = renderState.getOrDefaultGeckolibData(DataTickets.RENDER_COLOR, 0xFFFFFFFF);
        return renderColor >>> 24 < 255 ? OlympusRenderTypes.aresSpearDissolve(texture) : RenderTypes.entityCutout(texture);
    }

}