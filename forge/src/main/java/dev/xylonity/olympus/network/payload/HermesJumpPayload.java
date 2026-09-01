package dev.xylonity.olympus.network.payload;

import dev.xylonity.olympus.Olympus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public record HermesJumpPayload() implements CustomPacketPayload {

    public static final HermesJumpPayload INSTANCE = new HermesJumpPayload();

    public static final Type<HermesJumpPayload> TYPE = new Type<>(Olympus.of("hermes_jump"));

    public static final StreamCodec<RegistryFriendlyByteBuf, HermesJumpPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public @NonNull Type<HermesJumpPayload> type() {
        return TYPE;
    }

}