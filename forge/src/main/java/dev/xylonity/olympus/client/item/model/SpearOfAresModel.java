package dev.xylonity.olympus.client.item.model;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.model.DefaultedItemGeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import dev.xylonity.olympus.Olympus;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public final class SpearOfAresModel<T extends GeoAnimatable> extends DefaultedItemGeoModel<T> {

    public static final DataTicket<Boolean> SPECIAL_ABILITY_CHARGED = DataTicket.create("olympus_spear_of_ares_special_ability_charged", Boolean.class);
    public static final DataTicket<Float> DISSOLVE_VISIBILITY = DataTicket.create("olympus_spear_of_ares_dissolve_visibility", Float.class);

    public static final Identifier BASE_TEXTURE = Olympus.of("textures/item/spear_of_ares.png");
    public static final Identifier CHARGED_TEXTURE = Olympus.of("textures/item/spear_of_ares_charged.png");

    public SpearOfAresModel() {
        super(Olympus.of("spear_of_ares"));
    }

    @Override
    public @NonNull Identifier getModelResource(final @NonNull GeoRenderState renderState) {
        return isSpecialAbilityCharged(renderState) ? Olympus.of("item/spear_of_ares_charged") : super.getModelResource(renderState);
    }

    @Override
    public @NonNull Identifier getTextureResource(final @NonNull GeoRenderState renderState) {
        return isSpecialAbilityCharged(renderState) ? CHARGED_TEXTURE : super.getTextureResource(renderState);
    }

    public static boolean isSpecialAbilityCharged(final GeoRenderState renderState) {
        return renderState.getOrDefaultGeckolibData(SPECIAL_ABILITY_CHARGED, false);
    }

}
