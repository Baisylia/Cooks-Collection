package com.ncpbails.cookscollection.client.recipebook;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.ncpbails.cookscollection.recipe.ModRecipeBookTypes;
import com.ncpbails.cookscollection.recipe.OvenRecipe;
import com.ncpbails.cookscollection.recipe.OvenShapedRecipe;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.event.RegisterRecipeBookCategoriesEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class RecipeCategories {
    private static final Logger LOGGER = LogManager.getLogger();

    public static final Supplier<RecipeBookCategories> BAKING_SEARCH = Suppliers.memoize(() ->
            RecipeBookCategories.create("BAKING_SEARCH", new ItemStack(Items.COMPASS)));
    public static final Supplier<RecipeBookCategories> BAKING_PASTRIES = Suppliers.memoize(() ->
            RecipeBookCategories.create("BAKING_PASTRIES", new ItemStack(Items.BREAD)));
    public static final Supplier<RecipeBookCategories> BAKING_DESSERTS = Suppliers.memoize(() ->
            RecipeBookCategories.create("BAKING_DESSERTS", new ItemStack(Items.CAKE)));
    public static final Supplier<RecipeBookCategories> BAKING_MISC = Suppliers.memoize(() ->
            RecipeBookCategories.create("BAKING_MISC", new ItemStack(Items.COOKIE)));

    public static void init(RegisterRecipeBookCategoriesEvent event) {
        event.registerBookCategories(ModRecipeBookTypes.OVEN,
                ImmutableList.of(BAKING_SEARCH.get(), BAKING_PASTRIES.get(), BAKING_DESSERTS.get(), BAKING_MISC.get()));
        event.registerAggregateCategory(BAKING_SEARCH.get(),
                ImmutableList.of(BAKING_PASTRIES.get(), BAKING_DESSERTS.get(), BAKING_MISC.get()));
        event.registerRecipeCategoryFinder(OvenRecipe.Type.INSTANCE, recipe -> {
            if (recipe instanceof OvenRecipe ovenRecipe) {
                OvenRecipeBookTab tab = ovenRecipe.getRecipeBookTab();
                LOGGER.debug("Assigning recipe {} to tab {}", recipe.getId(), tab != null ? tab.name : "null");
                if (tab != null) {
                    return switch (tab) {
                        case PASTRIES -> BAKING_PASTRIES.get();
                        case DESSERTS -> BAKING_DESSERTS.get();
                        case MISC -> BAKING_MISC.get();
                    };
                } else {
                    LOGGER.warn("Recipe {} has no recipe book tab, defaulting to MISC", recipe.getId());
                }
            }
            return BAKING_MISC.get();
        });
        event.registerRecipeCategoryFinder(OvenShapedRecipe.Type.INSTANCE, recipe -> {
            if (recipe instanceof OvenShapedRecipe ovenShapedRecipe) {
                OvenRecipeBookTab tab = ovenShapedRecipe.getRecipeBookTab();
                LOGGER.debug("Assigning shaped recipe {} to tab {}", recipe.getId(), tab != null ? tab.name : "null");
                if (tab != null) {
                    return switch (tab) {
                        case PASTRIES -> BAKING_PASTRIES.get();
                        case DESSERTS -> BAKING_DESSERTS.get();
                        case MISC -> BAKING_MISC.get();
                    };
                } else {
                    LOGGER.warn("Shaped recipe {} has no recipe book tab, defaulting to MISC", recipe.getId());
                }
            }
            return BAKING_MISC.get();
        });
    }
}