package dev.xylonity.olympus.mixins;

import dev.xylonity.olympus.common.compat.LegacyNamespace;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(ServerRecipeBook.class)
public abstract class ServerRecipeBookMixin {

    @Redirect(method = "loadRecipes", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;byKey(Lnet/minecraft/resources/ResourceLocation;)Ljava/util/Optional;"))
    private Optional<RecipeHolder<?>> olympus$migrateLegacyRecipe(final RecipeManager manager, final ResourceLocation id) {
        final Optional<RecipeHolder<?>> recipe = manager.byKey(id);
        return recipe.isEmpty() && LegacyNamespace.isLegacy(id) ? manager.byKey(LegacyNamespace.migrate(id)) : recipe;
    }

}
