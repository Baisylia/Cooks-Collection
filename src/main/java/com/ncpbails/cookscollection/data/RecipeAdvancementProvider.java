package com.ncpbails.cookscollection.data;

import com.ncpbails.cookscollection.CooksCollection;
import com.ncpbails.cookscollection.recipe.ModRecipeTypes;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class RecipeAdvancementProvider extends ForgeAdvancementProvider {
    public RecipeAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, existingFileHelper, List.of(new OvenRecipeAdvancements()));
    }

    private static class OvenRecipeAdvancements implements AdvancementGenerator {
        @Override
        public void generate(HolderLookup.Provider registries, Consumer<Advancement> consumer, ExistingFileHelper existingFileHelper) {
            // Collect recipes from ModRecipeProvider or datapacks
            Map<ResourceLocation, Recipe<?>> recipes = collectRecipes();
            System.out.println("Total recipes found: " + recipes.size());
            recipes.forEach((recipeId, recipe) -> {
                if (recipe.getType() == ModRecipeTypes.BAKING_TYPE.get()) {
                    System.out.println("Generating advancement for recipe: " + recipeId);
                    createRecipeAdvancement(recipe, recipeId, consumer);
                }
            });
        }

        private Map<ResourceLocation, Recipe<?>> collectRecipes() {
            // Placeholder: In a real scenario, integrate with ModRecipeProvider
            Map<ResourceLocation, Recipe<?>> recipes = new HashMap<>();
            // Manually add known recipes for testing (replace with actual recipe collection)
            // Example: Add lemon_muffin and rustic_loaf
            // This should be replaced with actual recipe provider output
            System.out.println("Collecting recipes (placeholder implementation)");
            return recipes;
        }

        private void createRecipeAdvancement(Recipe<?> recipe, ResourceLocation recipeId, Consumer<Advancement> consumer) {
            String advancementPath = recipeId.getPath().startsWith("recipes/") ?
                    recipeId.getPath().replace("recipes/", "recipes/oven/") :
                    "recipes/oven/" + recipeId.getPath();
            ResourceLocation advancementId = new ResourceLocation(CooksCollection.MOD_ID, advancementPath);

            Advancement.Builder builder = Advancement.Builder.advancement()
                    .parent(new ResourceLocation("minecraft:recipes/root"))
                    .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                    .rewards(AdvancementRewards.Builder.recipe(recipeId))
                    .requirements(RequirementsStrategy.OR);

            List<Ingredient> ingredients = recipe.getIngredients();
            int index = 0;
            for (Ingredient ingredient : ingredients) {
                if (!ingredient.isEmpty()) {
                    ItemPredicate.Builder predicateBuilder = ItemPredicate.Builder.item();
                    for (ItemStack stack : ingredient.getItems()) {
                        predicateBuilder.of(stack.getItem());
                    }
                    ItemPredicate predicate = predicateBuilder.build();
                    builder.addCriterion("has_item_" + index, InventoryChangeTrigger.TriggerInstance.hasItems(predicate));
                    System.out.println("Added criterion for ingredient: " + index + " in recipe: " + recipeId);
                    index++;
                }
            }

            consumer.accept(builder.build(advancementId));
        }
    }
}