package dev.xylonity.olympus.network.payload;

import dev.xylonity.olympus.Olympus;
import dev.xylonity.knightlib.network.ClientPacketDispatcher;
import dev.xylonity.knightlib.network.ClientboundPacketType;
import dev.xylonity.knightlib.network.PacketCodec;
import dev.xylonity.knightlib.network.PacketType;
import net.minecraft.network.FriendlyByteBuf;

public record LyreMusicPayload(
        int musicianId,
        boolean playing
) {

    public static final ClientboundPacketType<LyreMusicPayload> TYPE = PacketType.clientbound(
            Olympus.of("lyre_music"), LyreMusicPayload.class,
            PacketCodec.of(LyreMusicPayload::encode, LyreMusicPayload::decode),
            ClientPacketDispatcher::dispatch
    );

    private static void encode(final LyreMusicPayload payload, final FriendlyByteBuf buffer) {
        buffer.writeVarInt(payload.musicianId);
        buffer.writeBoolean(payload.playing);
    }

    private static LyreMusicPayload decode(final FriendlyByteBuf buffer) {
        return new LyreMusicPayload(buffer.readVarInt(), buffer.readBoolean());
    }

}
