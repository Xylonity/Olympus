package dev.xylonity.olympus.client.entity.model;

import com.geckolib.model.DefaultedEntityGeoModel;
import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.common.entity.HarpyEntity;

public final class HarpyModel extends DefaultedEntityGeoModel<HarpyEntity> {

    public HarpyModel() {
        this(false);
    }

    public HarpyModel(final boolean elite) {
        super(Olympus.of(elite ? "elite_harpy" : "harpy"));
        if (elite) {
            withAltAnimations(Olympus.of("harpy"));
        }

    }

}
