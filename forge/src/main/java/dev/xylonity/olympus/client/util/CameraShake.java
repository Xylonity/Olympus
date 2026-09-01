package dev.xylonity.olympus.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/// Based off my own implementation of a camera shake manager (very simplistic)
/// https://github.com/Xylonity/Knight-Lib/blob/1.20.1/common/src/main/java/dev/xylonity/knightlib/api/camera/shake/CameraShakeManager.java
public class CameraShake {

    private static int age;
    private static int duration;
    private static float strength;
    private static float phase;

    public static void trigger(final Vec3 origin, final float radius, final float baseStrength, final int shakeDuration) {
        if (radius <= 0.0F || baseStrength <= 0.0F || shakeDuration <= 0) {
            return;
        }

        final Entity camera = Minecraft.getInstance().getCameraEntity();
        if (camera == null) {
            return;
        }

        final float distance = Mth.clamp(1.0F - (float) camera.position().distanceTo(origin) / radius, 0.0F, 1.0F);
        if (distance <= 0.0F) {
            return;
        }

        final float smoothness = distance * distance * (3.0F - 2.0F * distance);
        final float in = baseStrength * smoothness;
        final float rest = currentStrength();

        strength = Math.min(1.8f, Math.max(rest, in) + in * 0.25F);
        duration = Math.max(Math.max(1, duration - age), shakeDuration);
        age = 0;
        phase = (float) ((origin.x * 0.71D + origin.y * 0.37D + origin.z * 0.53D) % (Math.PI * 2.0D));
    }

    public static void tick() {
        if (age < duration) {
            age++;
        }
        else {
            strength = 0.0F;
            duration = 0;
        }

    }

    public static Vec3 getOffsets(final double partialTick) {
        if (duration == 0 || strength <= 0.0F) {
            return Vec3.ZERO;
        }

        final float time = age + (float) partialTick;
        final float envelope = currentEnvelope(time);
        final float shake = strength * envelope;
        return new Vec3(
                Mth.sin(time * 2.35F + phase) * shake * 0.75F,
                Mth.sin(time * 2.9F + phase * 0.7F) * shake * 0.6F,
                Mth.sin(time * 1.8F + phase * 1.3F) * shake * 0.45F
        );

    }

    private static float currentStrength() {
        return strength * currentEnvelope(age);
    }

    private static float currentEnvelope(final float currentAge) {
        if (duration == 0) {
            return 0;
        }

        final float remaining = Mth.clamp(1.0F - currentAge / duration, 0.0F, 1.0F);
        return remaining * remaining;
    }

}
