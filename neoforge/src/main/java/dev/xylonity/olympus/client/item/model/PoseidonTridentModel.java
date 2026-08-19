package dev.xylonity.olympus.client.item.model;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.model.DefaultedItemGeoModel;
import dev.xylonity.olympus.Olympus;

public final class PoseidonTridentModel<T extends GeoAnimatable> extends DefaultedItemGeoModel<T> {

    public PoseidonTridentModel() {
        super(Olympus.of("poseidon_trident"));
    }

}