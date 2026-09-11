package dev.xylonity.olympus.network.payload;

import dev.xylonity.olympus.Olympus;
import dev.xylonity.knightlib.network.ClientPacketDispatcher;
import dev.xylonity.knightlib.network.ClientboundPacketType;
import dev.xylonity.knightlib.network.PacketCodec;
import dev.xylonity.knightlib.network.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

/**
 * A custom payload that sends relevant info to the client so the lightning bolt particle can infer the correct positions when spawning
 */
public record LightningBoltPayload(
        Vec3 start,
        Vec3 end,
        boolean skyStrike
) {

    public static final ClientboundPacketType<LightningBoltPayload> TYPE = PacketType.clientbound(
            Olympus.of("lightning_bolt"), LightningBoltPayload.class,
            PacketCodec.of(LightningBoltPayload::encode, LightningBoltPayload::decode),
            ClientPacketDispatcher::dispatch
    );

    private static void encode(final LightningBoltPayload payload, final FriendlyByteBuf buffer) {
        writeVec3(buffer, payload.start);
        writeVec3(buffer, payload.end);
        buffer.writeBoolean(payload.skyStrike);
    }

    private static LightningBoltPayload decode(final FriendlyByteBuf buffer) {
        return new LightningBoltPayload(readVec3(buffer), readVec3(buffer), buffer.readBoolean());
    }

    private static void writeVec3(final FriendlyByteBuf buffer, final Vec3 value) {
        buffer.writeDouble(value.x);
        buffer.writeDouble(value.y);
        buffer.writeDouble(value.z);
    }

    private static Vec3 readVec3(final FriendlyByteBuf buffer) {
        return new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

}