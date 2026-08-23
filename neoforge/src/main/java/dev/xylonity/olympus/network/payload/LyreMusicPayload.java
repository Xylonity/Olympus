package dev.xylonity.olympus.network.payload;

import dev.xylonity.olympus.Olympus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public record LyreMusicPayload(
        int musicianId,
        boolean playing
) implements CustomPacketPayload {

    public static final Type<LyreMusicPayload> TYPE = new Type<>(Olympus.of("lyre_music"));

    public static final StreamCodec<RegistryFriendlyByteBuf, LyreMusicPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            LyreMusicPayload::musicianId,
            ByteBufCodecs.BOOL,
            LyreMusicPayload::playing,
            LyreMusicPayload::new
    );

    @Override
    public @NonNull Type<LyreMusicPayload> type() {
        return TYPE;
    }

}
