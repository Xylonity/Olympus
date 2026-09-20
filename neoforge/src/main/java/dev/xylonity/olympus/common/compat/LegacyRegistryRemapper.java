package dev.xylonity.olympus.common.compat;

import dev.xylonity.olympus.Olympus;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = Olympus.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class LegacyRegistryRemapper {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRegister(final RegisterEvent event) {
        final Registry<?> registry = event.getRegistry();
        int aliased = 0;
        for (ResourceLocation key : registry.keySet()) {
            if (Olympus.MOD_ID.equals(key.getNamespace())) {
                registry.addAlias(ResourceLocation.fromNamespaceAndPath(LegacyNamespace.ID, key.getPath()), key);
                aliased++;
            }

        }

        if (aliased > 0) {
            Olympus.LOGGER.debug("Aliased {} legacy entries in {}", aliased, registry.key().location());
        }

    }

}
