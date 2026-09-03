package dev.xylonity.olympus.network.payload;

import dev.xylonity.olympus.Olympus;
import dev.xylonity.knightlib.network.ClientPacketDispatcher;
import dev.xylonity.knightlib.network.ClientboundPacketType;
import dev.xylonity.knightlib.network.PacketCodec;
import dev.xylonity.knightlib.network.PacketType;
import net.minecraft.network.FriendlyByteBuf;

public record SoulSalvationPayload(
        int entityId,
        int particleCount,
        boolean sphericalBurst
) {

    public static final ClientboundPacketType<SoulSalvationPayload> TYPE = PacketType.clientbound(
            Olympus.of("soul_salvation"), SoulSalvationPayload.class,
            PacketCodec.of(SoulSalvationPayload::encode, SoulSalvationPayload::decode),
            ClientPacketDispatcher::dispatch
    );

    private static void encode(final SoulSalvationPayload payload, final FriendlyByteBuf buffer) {
        buffer.writeVarInt(payload.entityId);
        buffer.writeVarInt(payload.particleCount);
        buffer.writeBoolean(payload.sphericalBurst);
    }

    private static SoulSalvationPayload decode(final FriendlyByteBuf buffer) {
        return new SoulSalvationPayload(buffer.readVarInt(), buffer.readVarInt(), buffer.readBoolean());
    }

}
