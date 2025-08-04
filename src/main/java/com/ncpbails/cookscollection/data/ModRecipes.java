package com.ncpbails.cookscollection.data;

import com.ncpbails.cookscollection.block.ModBlocks;
import com.ncpbails.cookscollection.client.recipebook.OvenRecipeBookTab;
import com.ncpbails.cookscollection.item.ModItems;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
//import vectorwing.farmersdelight.common.registry.ModItems;
import com.ncpbails.cookscollection.recipe.ModRecipeTypes;

import java.util.function.Consumer;

public class ModRecipes {
    public static final int FAST_COOKING = 100;  // 5 seconds
    public static final int NORMAL_COOKING = 200; // 10 seconds
    public static final int SLOW_COOKING = 400;  // 20 seconds

    public static void register(Consumer<FinishedRecipe> consumer) {
        System.out.println("Generating oven recipes...");
        // Rustic Loaf
        OvenRecipeBuilder.ovenRecipe(ModBlocks.RUSTIC_LOAF.get(), 1, NORMAL_COOKING)
                .addIngredient(vectorwing.farmersdelight.common.registry.ModItems.WHEAT_DOUGH.get(), 2)
                .addIngredient(Items.SUGAR)
                .addIngredient(ModItems.SALT.get())
                .unlockedByAnyIngredient(vectorwing.farmersdelight.common.registry.ModItems.WHEAT_DOUGH.get(), Items.SUGAR, ModItems.SALT.get())
                .setRecipeBookTab(OvenRecipeBookTab.PASTRIES)
                .build(consumer);
        System.out.println("Generated rustic_loaf recipe");

        // Lemon Muffin
        OvenRecipeBuilder.ovenRecipe(ModItems.LEMON_MUFFIN.get(), 1, NORMAL_COOKING)
                .addIngredient(vectorwing.farmersdelight.common.registry.ModItems.WHEAT_DOUGH.get())
                .addIngredient(ModItems.LEMON.get())
                .addIngredient(Items.SUGAR)
                .addIngredient(ModItems.SALT.get())
                .addIngredient(Items.POPPY)
                .unlockedByAnyIngredient(vectorwing.farmersdelight.common.registry.ModItems.WHEAT_DOUGH.get(), ModItems.LEMON.get(), Items.SUGAR, ModItems.SALT.get(), Items.POPPY)
                .setRecipeBookTab(OvenRecipeBookTab.DESSERTS)
                .build(consumer);
        System.out.println("Generated lemon_muffin recipe");
    }
}