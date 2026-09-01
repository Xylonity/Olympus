package dev.xylonity.olympus.network.payload;

import dev.xylonity.olympus.Olympus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

public record CameraShakePayload(
        Vec3 origin,
        float radius,
        float strength,
        int duration
) implements CustomPacketPayload {

    public static final Type<CameraShakePayload> TYPE = new Type<>(Olympus.of("camera_shake"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CameraShakePayload> STREAM_CODEC = StreamCodec.composite(
            Vec3.STREAM_CODEC,
            CameraShakePayload::origin,
            ByteBufCodecs.FLOAT,
            CameraShakePayload::radius,
            ByteBufCodecs.FLOAT,
            CameraShakePayload::strength,
            ByteBufCodecs.VAR_INT,
            CameraShakePayload::duration,
            CameraShakePayload::new
    );

    @Override
    public @NonNull Type<CameraShakePayload> type() {
        return TYPE;
    }

}
