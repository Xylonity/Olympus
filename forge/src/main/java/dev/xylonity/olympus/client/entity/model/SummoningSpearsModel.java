package dev.xylonity.olympus.client.entity.model;

import com.geckolib.model.DefaultedEntityGeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.common.entity.projectile.SummoningSpearsEntity;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public final class SummoningSpearsModel extends DefaultedEntityGeoModel<SummoningSpearsEntity> {

    public SummoningSpearsModel() {
        super(Olympus.of("summoning_spears"));
    }

    @Override
    public @NonNull Identifier getTextureResource(final @NonNull GeoRenderState renderState) {
        return Olympus.of("textures/item/spear_of_ares.png");
    }

}
