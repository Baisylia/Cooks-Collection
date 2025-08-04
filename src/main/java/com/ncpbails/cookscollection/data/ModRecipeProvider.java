package com.ncpbails.cookscollection.data;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    private final ExistingFileHelper existingFileHelper;

    public ModRecipeProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output);
        this.existingFileHelper = existingFileHelper;
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        ModRecipes.register(consumer);
    }
}