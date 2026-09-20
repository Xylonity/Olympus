package dev.xylonity.olympus.mixins;

import dev.xylonity.olympus.common.compat.LegacyNamespace;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ChunkSerializer.class)
public abstract class ChunkSerializerMixin {

    @ModifyVariable(method = "read", at = @At("HEAD"), argsOnly = true)
    private static CompoundTag olympus$migrateLegacyStructures(final CompoundTag tag) {
        if (tag.contains("structures", Tag.TAG_COMPOUND)) {
            LegacyNamespace.migrate(tag.getCompound("structures"));
        }

        return tag;
    }

}
