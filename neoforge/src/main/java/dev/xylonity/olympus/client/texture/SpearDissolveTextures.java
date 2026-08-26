package dev.xylonity.olympus.client.texture;

import com.mojang.blaze3d.platform.NativeImage;
import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.client.item.model.SpearOfAresModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jspecify.annotations.NonNull;

/// Instead of using the old shader (doesn't work with shaderpacks, ty iris) for the dithering dissolve, textures are computed automatically on resource load
public final class SpearDissolveTextures extends SimplePreparableReloadListener<SpearDissolveTextures.PreparedTextures> {

    public static final SpearDissolveTextures INSTANCE = new SpearDissolveTextures();

    // Mask that determines which pixels should disappear
    private static final Identifier DISSOLVE_MASK = Olympus.of("textures/misc/ares_spear_dissolve_mask.png");
    // TODO: Both spear textures need their own (even if the charged one is not visible at all), may change in the future
    private static final List<Identifier> SOURCE_TEXTURES = List.of(SpearOfAresModel.BASE_TEXTURE, SpearOfAresModel.CHARGED_TEXTURE);

    // 32 textures are made
    private static final int FRAMES = 32;

    // Computed textures
    private static volatile Map<Identifier, List<Identifier>> generatedTextures = Map.of();
    private static List<Identifier> registeredTextureIds = List.of();

    public static Identifier textureFor(final Identifier sourceTexture, final float visibility) {
        // Full visibility uses the original texture
        if (visibility >= 0.9999) {
            return sourceTexture;
        }

        // Also fallbacks to the original while the frames are not loaded
        final List<Identifier> frames = generatedTextures.get(sourceTexture);
        if (frames == null || frames.isEmpty()) {
            return sourceTexture;
        }

        final float clampedVisibility = Mth.clamp(visibility, 0, 1);
        final int frame = Mth.clamp((int) ((1 - clampedVisibility) * FRAMES), 0, FRAMES - 1);
        return frames.get(frame);
    }

    @Override
    protected PreparedTextures prepare(final @NonNull ResourceManager resourceManager, final @NonNull ProfilerFiller profiler) {
        final Map<Identifier, List<PreparedFrame>> prepared = new HashMap<>();

        // Reads the mask once but generates a separate frame set for each source texture
        try (final NativeImage mask = readImage(resourceManager, DISSOLVE_MASK)) {
            for (final Identifier sourceTexture : SOURCE_TEXTURES) {
                try (final NativeImage source = readImage(resourceManager, sourceTexture)) {
                    final List<PreparedFrame> frames = new ArrayList<>(FRAMES);
                    for (int frame = 0; frame < FRAMES; frame++) {
                        final Identifier idx = frameId(sourceTexture, frame);
                        final float visibility = 1f - (frame + 1) / (float) FRAMES;
                        frames.add(new PreparedFrame(idx, createFrame(source, mask, visibility)));
                    }

                    prepared.put(sourceTexture, frames);
                }

            }

        }
        catch (final Exception exception) {
            Olympus.LOGGER.error("Could not prepare the Spear of Ares dissolve textures", exception);
            prepared.values().stream().flatMap(List::stream).forEach(frame -> frame.image.close());
            prepared.clear();
        }

        return new PreparedTextures(prepared);
    }

    @Override
    protected void apply(final PreparedTextures prepared, final ResourceManager resourceManager, final ProfilerFiller profiler) {
        final TextureManager textureManager = Minecraft.getInstance().getTextureManager();

        registeredTextureIds.forEach(textureManager::release);

        final Map<Identifier, List<Identifier>> applied = new HashMap<>();
        final List<Identifier> registered = new ArrayList<>();
        prepared.frames.forEach((source, frames) -> {
            final List<Identifier> ids = new ArrayList<>(frames.size());
            for (final PreparedFrame frame : frames) {
                textureManager.register(frame.id, new DynamicTexture(frame.id::toString, frame.image));
                ids.add(frame.id);
                registered.add(frame.id);
            }

            applied.put(source, List.copyOf(ids));
        });

        // Replaces both collections only after the whole thing has been registered
        registeredTextureIds = List.copyOf(registered);
        generatedTextures = Map.copyOf(applied);
    }

    private static NativeImage readImage(final ResourceManager resourceManager, final Identifier texture) throws IOException {
        final Resource resource = resourceManager.getResource(texture).orElseThrow(() -> new IOException("[Olympus] Missing texture " + texture));
        return NativeImage.read(resource.open());
    }

    private static NativeImage createFrame(final NativeImage source, final NativeImage mask, final float visibility) {
        // Every generated image keeps the exact size and color format of the original texture
        final NativeImage frame = new NativeImage(source.getWidth(), source.getHeight(), true);
        final int cap = Math.round(visibility * 255.0F);

        for (int y = 0; y < source.getHeight(); y++) {
            // Integer scaling
            final int maskY = y * mask.getHeight() / source.getHeight();
            for (int x = 0; x < source.getWidth(); x++) {
                final int sourcePixel = source.getPixel(x, y);
                final int maskX = x * mask.getWidth() / source.getWidth();
                final int maskAlpha = ARGB.alpha(mask.getPixel(maskX, maskY));
                final boolean visible = visibility > 0.0F && ARGB.alpha(sourcePixel) > 0 && maskAlpha <= cap;
                frame.setPixel(x, y, visible ? sourcePixel : 0);
            }

        }

        return frame;
    }

    private static Identifier frameId(final Identifier sourceTexture, final int frame) {
        // Converts the original texture into various separated frames
        final String name = sourceTexture.getPath().replace("textures/item/", "").replace(".png", "");
        return Olympus.of("dynamic/spear_dissolve/" + name + "_" + frame);
    }

    public record PreparedTextures(
            Map<Identifier, List<PreparedFrame>> frames
    ) {
        ;;
    }

    public record PreparedFrame(
            Identifier id,
            NativeImage image
    ) {
        ;;
    }

}