package dev.xylonity.olympus.network;

import dev.xylonity.olympus.network.payload.LightningBoltPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class OlympusNetwork {

    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(LightningBoltPayload.TYPE, LightningBoltPayload.STREAM_CODEC);
    }

}