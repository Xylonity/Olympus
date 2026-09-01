package dev.xylonity.olympus.client.sound;

import dev.xylonity.olympus.common.item.AphroditeLyreItem;
import dev.xylonity.olympus.registry.OlympusSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/// Derived from my own implementation
/// https://github.com/Xylonity/Knight-Lib/blob/1.20.1/common/src/main/java/dev/xylonity/knightlib/client/sound/music/AbstractLoopSound.java
/// https://github.com/Xylonity/Knight-Lib/blob/1.20.1/common/src/main/java/dev/xylonity/knightlib/client/sound/persistent/PersistentSoundInstance.java
public final class LyreMusicSound extends AbstractTickableSoundInstance {

    private static final Map<Integer, LyreMusicSound> ACTIVE_SOUNDS = new ConcurrentHashMap<>();

    private static final int FADE_TICKS = 10;

    private final int playerId;

    private int age;
    private int fadeOutAge = -1;
    private float fadeOutStartVolume;

    private LyreMusicSound(final Entity player) {
        super(OlympusSounds.LYRE_MUSIC.get(), SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        playerId = player.getId();
        looping = false;
        delay = 0;
        attenuation = Attenuation.LINEAR;
        relative = false;
        volume = 0.0F;
        pitch = 1.0F;
        updatePosition(player);
    }

    // Computed once the packet is received on the client
    public static void start(final int entityId) {
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }

        // Musician player
        final Entity entity = minecraft.level.getEntity(entityId);
        if (entity == null) {
            return;
        }

        // Tracks each active sound instance
        final LyreMusicSound current = ACTIVE_SOUNDS.get(entityId);
        if (current != null && !current.isStopped()) {
            return;
        }

        // Adds a new sounds instance
        final LyreMusicSound sound = new LyreMusicSound(entity);
        ACTIVE_SOUNDS.put(entityId, sound);
        minecraft.getSoundManager().play(sound);
    }

    public static void fadeOut(final int playerId) {
        final LyreMusicSound sound = ACTIVE_SOUNDS.get(playerId);
        if (sound != null) {
            sound.beginFadeOut();
        }

    }

    @Override
    public void tick() {
        if (isStopped()) {
            return;
        }

        final Minecraft minecraft = Minecraft.getInstance();
        final Entity player = minecraft.level == null ? null : minecraft.level.getEntity(playerId);
        if (player == null || player.isRemoved()) {
            beginFadeOut();
        }
        else {
            updatePosition(player);
        }

        age++;
        if (fadeOutAge < 0 && age >= AphroditeLyreItem.DURATION_TICKS - FADE_TICKS) {
            beginFadeOut();
        }

        if (fadeOutAge >= 0) {
            fadeOutAge++;
            volume = fadeOutStartVolume * Math.max(0, 1 - fadeOutAge / (float) FADE_TICKS);
            if (fadeOutAge >= FADE_TICKS) {
                finish();
            }

        }
        else {
            volume = Math.min(1, age / (float) FADE_TICKS);
        }

    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    private void beginFadeOut() {
        if (fadeOutAge >= 0) {
            return;
        }

        fadeOutAge = 0;
        fadeOutStartVolume = volume;
    }

    private void updatePosition(final Entity player) {
        x = player.getX();
        y = player.getY();
        z = player.getZ();
    }

    private void finish() {
        ACTIVE_SOUNDS.remove(playerId, this);
        stop();
    }

}