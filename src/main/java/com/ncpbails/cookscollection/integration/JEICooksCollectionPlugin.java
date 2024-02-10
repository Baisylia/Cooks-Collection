package com.ncpbails.cookscollection.integration;

import com.ncpbails.cookscollection.CooksCollection;
import com.ncpbails.cookscollection.recipe.OvenRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;
import java.util.Objects;

@JeiPlugin
public class JEICooksCollectionPlugin implements IModPlugin {
    public static RecipeType<OvenRecipe> BAKING_TYPE =
            new RecipeType<>(OvenRecipeCategory.UID, OvenRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(CooksCollection.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new
                OvenRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();

        List<OvenRecipe> recipes = rm.getAllRecipesFor(OvenRecipe.Type.INSTANCE);
        registration.addRecipes(BAKING_TYPE, recipes);
    }
}
