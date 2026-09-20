package dev.xylonity.olympus.common.compat;

import dev.xylonity.olympus.Olympus;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;

/**
 * The mod used to be called "olympus", but due to a private unpublished and shadowed library called "olympus", I have to rename this mod
 */
public final class LegacyNamespace {

    public static final String ID = "olympus";
    private static final String PREFIX = ID + ":";

    public static boolean isLegacy(final ResourceLocation location) {
        return ID.equals(location.getNamespace());
    }

    public static boolean isLegacy(final String id) {
        return id.startsWith(PREFIX);
    }

    public static ResourceLocation migrate(final ResourceLocation location) {
        return isLegacy(location) ? Olympus.of(location.getPath()) : location;
    }

    public static String migrate(final String id) {
        return isLegacy(id) ? Olympus.MOD_ID + id.substring(ID.length()) : id;
    }

    public static void migrate(final CompoundTag tag) {
        for (String key : new ArrayList<>(tag.getAllKeys())) {
            final Tag value = tag.get(key);
            migrate(value);

            if (value instanceof StringTag string && isLegacy(string.getAsString())) {
                tag.putString(key, migrate(string.getAsString()));
            }
            if (isLegacy(key)) {
                tag.put(migrate(key), tag.get(key));
                tag.remove(key);
            }

        }

    }

    private static void migrate(final Tag tag) {
        if (tag instanceof CompoundTag compound) {
            migrate(compound);
        }
        else if (tag instanceof ListTag list) {
            for (int i = 0; i < list.size(); i++) {
                final Tag element = list.get(i);
                if (element instanceof StringTag string && isLegacy(string.getAsString())) {
                    list.set(i, StringTag.valueOf(migrate(string.getAsString())));
                }
                else {
                    migrate(element);
                }

            }

        }

    }

}
