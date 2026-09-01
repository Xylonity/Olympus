package dev.xylonity.olympus.network;

import dev.xylonity.olympus.common.item.HermesSandalsItem;
import dev.xylonity.olympus.network.payload.CameraShakePayload;
import dev.xylonity.olympus.network.payload.HermesJumpPayload;
import dev.xylonity.olympus.network.payload.LightningBoltPayload;
import dev.xylonity.olympus.network.payload.LyreMusicPayload;
import dev.xylonity.olympus.network.payload.SoulSalvationPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class OlympusNetwork {

    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        event.registrar("1")
                .playToClient(CameraShakePayload.TYPE, CameraShakePayload.STREAM_CODEC)
                .playToClient(LightningBoltPayload.TYPE, LightningBoltPayload.STREAM_CODEC)
                .playToClient(LyreMusicPayload.TYPE, LyreMusicPayload.STREAM_CODEC)
                .playToClient(SoulSalvationPayload.TYPE, SoulSalvationPayload.STREAM_CODEC)
                .playToServer(HermesJumpPayload.TYPE, HermesJumpPayload.STREAM_CODEC, (_, context) ->
                    context.enqueueWork(() -> {
                        if (context.player() instanceof ServerPlayer player) {
                            HermesSandalsItem.tryActiveAbility(player);
                        }

                    })

                );

    }

}
