package dev.xylonity.olympus.network.payload;

import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.common.item.HermesSandalsItem;
import dev.xylonity.knightlib.network.PacketCodec;
import dev.xylonity.knightlib.network.PacketType;
import dev.xylonity.knightlib.network.ServerboundPacketType;
import net.minecraft.network.FriendlyByteBuf;

public record HermesJumpPayload() {

    public static final HermesJumpPayload INSTANCE = new HermesJumpPayload();

    public static final ServerboundPacketType<HermesJumpPayload> TYPE = PacketType.serverbound(
            Olympus.of("hermes_jump"), HermesJumpPayload.class,
            PacketCodec.of(HermesJumpPayload::encode, HermesJumpPayload::decode),
            (payload, player) -> HermesSandalsItem.tryActiveAbility(player)
    );

    private static void encode(final HermesJumpPayload payload, final FriendlyByteBuf buffer) {
        ;;
    }

    private static HermesJumpPayload decode(final FriendlyByteBuf buffer) {
        return INSTANCE;
    }

}