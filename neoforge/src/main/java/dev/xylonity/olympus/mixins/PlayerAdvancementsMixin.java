package dev.xylonity.olympus.mixins;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import dev.xylonity.olympus.common.compat.LegacyNamespace;
import net.minecraft.server.PlayerAdvancements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.Map;

@Mixin(PlayerAdvancements.class)
public abstract class PlayerAdvancementsMixin {

    @Redirect(method = "load", at = @At(value = "INVOKE", target = "Lcom/google/gson/internal/Streams;parse(Lcom/google/gson/stream/JsonReader;)Lcom/google/gson/JsonElement;"))
    private JsonElement olympus$migrateLegacyAdvancements(final JsonReader reader) {
        final JsonElement parsed = Streams.parse(reader);
        if (!(parsed instanceof JsonObject progress)) {
            return parsed;
        }

        for (Map.Entry<String, JsonElement> entry : new ArrayList<>(progress.entrySet())) {
            if (LegacyNamespace.isLegacy(entry.getKey())) {
                progress.remove(entry.getKey());
                if (!progress.has(LegacyNamespace.migrate(entry.getKey()))) {
                    progress.add(LegacyNamespace.migrate(entry.getKey()), entry.getValue());
                }

            }

        }

        return progress;
    }

}