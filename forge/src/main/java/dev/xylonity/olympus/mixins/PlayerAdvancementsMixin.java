package dev.xylonity.olympus.mixins;

import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import dev.xylonity.olympus.common.compat.LegacyNamespace;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.LinkedHashMap;
import java.util.Map;

@Mixin(PlayerAdvancements.class)
public abstract class PlayerAdvancementsMixin {

    @Redirect(method = "load", at = @At(value = "INVOKE", target = "Lcom/google/gson/TypeAdapter;fromJsonTree(Lcom/google/gson/JsonElement;)Ljava/lang/Object;"))
    private Object olympus$migrateLegacyAdvancements(final TypeAdapter<Object> adapter, final JsonElement json) {
        final Object parsed = adapter.fromJsonTree(json);
        if (!(parsed instanceof Map<?, ?> progress)) {
            return parsed;
        }

        final Map<Object, Object> migrated = new LinkedHashMap<>();
        progress.forEach((key, value) -> {
            if (key instanceof ResourceLocation location && LegacyNamespace.isLegacy(location)) {
                migrated.put(LegacyNamespace.migrate(location), value);
            }

        });

        progress.forEach((key, value) -> {
            if (!(key instanceof ResourceLocation location && LegacyNamespace.isLegacy(location))) {
                migrated.put(key, value);
            }

        });

        return migrated;
    }

}
