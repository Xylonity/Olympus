package dev.xylonity.olympus.network.payload;

import dev.xylonity.olympus.Olympus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public record SoulSalvationPayload(
        int entityId,
        int particleCount,
        boolean sphericalBurst
) implements CustomPacketPayload {

    public static final Type<SoulSalvationPayload> TYPE = new Type<>(Olympus.of("soul_salvation"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SoulSalvationPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            SoulSalvationPayload::entityId,
            ByteBufCodecs.VAR_INT,
            SoulSalvationPayload::particleCount,
            ByteBufCodecs.BOOL,
            SoulSalvationPayload::sphericalBurst,
            SoulSalvationPayload::new
    );

    @Override
    public @NonNull Type<SoulSalvationPayload> type() {
        return TYPE;
    }

}
