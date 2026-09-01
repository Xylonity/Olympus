package dev.xylonity.olympus.network.payload;

import dev.xylonity.olympus.Olympus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

/**
 * A custom payload that sends relevant info to the client so the lightning bolt particle can infer the correct positions when spawning
 */
public record LightningBoltPayload(
        Vec3 start,
        Vec3 end,
        boolean skyStrike
) implements CustomPacketPayload {

    public static final Type<LightningBoltPayload> TYPE = new Type<>(Olympus.of("lightning_bolt"));

    public static final StreamCodec<RegistryFriendlyByteBuf, LightningBoltPayload> STREAM_CODEC = StreamCodec.composite(
            Vec3.STREAM_CODEC,
            LightningBoltPayload::start,
            Vec3.STREAM_CODEC,
            LightningBoltPayload::end,
            ByteBufCodecs.BOOL,
            LightningBoltPayload::skyStrike,
            LightningBoltPayload::new
    );

    @Override
    public @NonNull Type<LightningBoltPayload> type() {
        return TYPE;
    }

}