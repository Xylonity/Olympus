package dev.xylonity.olympus.registry;

import com.mojang.serialization.Codec;
import dev.xylonity.olympus.Olympus;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class OlympusAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Olympus.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> ARTEMIS_ARROW = ATTACHMENTS.register(
            "artemis_arrow",
            () -> AttachmentType.builder(() -> false)
                    .serialize(Codec.BOOL.fieldOf("value"), Boolean::booleanValue)
                    .sync(ByteBufCodecs.BOOL)
                    .build()
    );

}
