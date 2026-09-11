package dev.xylonity.olympus.network;

import dev.xylonity.knightlib.KnightLib;
import dev.xylonity.knightlib.api.camera.shake.ShakeSettings;
import dev.xylonity.knightlib.api.network.Network;
import dev.xylonity.knightlib.api.network.NetworkEndpoint;
import dev.xylonity.knightlib.network.ClientboundPacketType;
import dev.xylonity.knightlib.network.packets.CameraShakeS2C;
import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.network.payload.HermesJumpPayload;
import dev.xylonity.olympus.network.payload.LightningBoltPayload;
import dev.xylonity.olympus.network.payload.LyreMusicPayload;
import dev.xylonity.olympus.network.payload.SoulSalvationPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class OlympusNetwork {

    public static final NetworkEndpoint ENDPOINT = Network.endpoint(Olympus.MOD_ID);

    public static void register() {
        ENDPOINT.register(LightningBoltPayload.TYPE);
        ENDPOINT.register(LyreMusicPayload.TYPE);
        ENDPOINT.register(SoulSalvationPayload.TYPE);
        ENDPOINT.register(HermesJumpPayload.TYPE);
    }

    public static <T> void sendToTrackingAndSelf(final Entity entity, final ClientboundPacketType<T> type, final T payload) {
        ENDPOINT.sendToTracking(entity, type.base(), payload);
        if (entity instanceof ServerPlayer player) {
            ENDPOINT.sendTo(player, type.base(), payload);
        }

    }

    public static <T> void sendNear(final ServerLevel level, final Vec3 origin, final double radius, final ClientboundPacketType<T> type, final T payload) {
        final double radiusSquared = radius * radius;
        for (final ServerPlayer player : level.players()) {
            if (player.distanceToSqr(origin) <= radiusSquared) {
                ENDPOINT.sendTo(player, type.base(), payload);
            }

        }

    }

    /// Helper for sending generic camera shake based on certain distance to a origin
    public static void shake(final ServerLevel level, final Vec3 origin, final float radius, final float strength, final int duration) {
        if (radius <= 0 || strength <= 0 || duration <= 0) {
            return;
        }

        final double radiusSquared = radius * radius;
        for (final ServerPlayer player : level.players()) {
            final double distanceSquared = player.distanceToSqr(origin);
            if (distanceSquared > radiusSquared) {
                continue;
            }

            final float distance = Mth.clamp(1.0F - (float) Math.sqrt(distanceSquared) / radius, 0.0F, 1.0F);
            final float smoothness = distance * distance * (3.0F - 2.0F * distance);
            final float amplitude = strength * smoothness;
            final ShakeSettings settings = ShakeSettings.builder()
                    .durationTicks(duration)
                    .fadeOutTicks(duration)
                    .frequency(20)
                    .amplitude(amplitude * 0.1F, amplitude * 0.08F, amplitude * 0.1F)
                    .seed(level.random.nextLong())
                    .build();
            KnightLib.NET.sendTo(player, CameraShakeS2C.TYPE.base(), new CameraShakeS2C(settings, false));
        }

    }

}
