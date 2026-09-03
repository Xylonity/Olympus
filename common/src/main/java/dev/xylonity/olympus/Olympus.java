package dev.xylonity.olympus;

import dev.xylonity.olympus.platform.OlympusPlatform;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;

public class Olympus {

    public static final String MOD_ID = "olympus";
    public static final Logger LOGGER = LoggerFactory.getLogger("Olympus");

    public static final OlympusPlatform PLATFORM = ServiceLoader.load(OlympusPlatform.class).findFirst().orElseThrow();

    public static void init() {
        ;;
    }

    public static ResourceLocation of(final String path) {
        return new ResourceLocation(MOD_ID, path);
    }

}
