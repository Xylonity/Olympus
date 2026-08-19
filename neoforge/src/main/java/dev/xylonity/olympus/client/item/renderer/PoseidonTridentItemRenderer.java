package dev.xylonity.olympus.client.item.renderer;

import com.geckolib.renderer.GeoItemRenderer;
import dev.xylonity.olympus.client.item.model.PoseidonTridentModel;
import dev.xylonity.olympus.common.item.PoseidonTridentItem;

public final class PoseidonTridentItemRenderer extends GeoItemRenderer<PoseidonTridentItem> {

    public PoseidonTridentItemRenderer() {
        super(new PoseidonTridentModel<>());
    }

}