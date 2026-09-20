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
    private static CompoundTag olympus$migrateLegacyChunk(final CompoundTag tag) {
        if (tag.contains("structures", Tag.TAG_COMPOUND)) {
            LegacyNamespace.migrate(tag.getCompound("structures"));
        }

        for (Tag entry : tag.getList("block_entities", Tag.TAG_COMPOUND)) {
            final CompoundTag blockEntity = (CompoundTag) entry;
            if (blockEntity.contains("LootTable", Tag.TAG_STRING)) {
                blockEntity.putString("LootTable", LegacyNamespace.migrate(blockEntity.getString("LootTable")));
            }
        }

        return tag;
    }

}
