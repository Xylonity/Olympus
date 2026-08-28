package dev.xylonity.olympus.client.entity.model;

import com.geckolib.model.DefaultedEntityGeoModel;
import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.common.entity.HarpyEntity;

public final class HarpyModel extends DefaultedEntityGeoModel<HarpyEntity> {

    public HarpyModel() {
        super(Olympus.of("harpy"));
    }

}
