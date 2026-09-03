package dev.xylonity.olympus.registry;

import dev.xylonity.olympus.Olympus;
import dev.xylonity.knightlib.api.entity.data.AttachmentSerializer;
import dev.xylonity.knightlib.api.entity.data.AttachmentType;

public final class OlympusAttachments {

    public static final AttachmentType<Boolean> ARTEMIS_ARROW = AttachmentType.builder(
            Olympus.of("artemis_arrow"), () -> false, AttachmentSerializer.BOOLEAN
    ).synced().build();

}
