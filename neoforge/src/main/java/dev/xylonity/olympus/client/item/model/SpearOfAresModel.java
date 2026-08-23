package dev.xylonity.olympus.client.item.model;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.model.DefaultedItemGeoModel;
import dev.xylonity.olympus.Olympus;

public final class SpearOfAresModel<T extends GeoAnimatable> extends DefaultedItemGeoModel<T> {

    public SpearOfAresModel() {
        super(Olympus.of("spear_of_ares"));
    }

}