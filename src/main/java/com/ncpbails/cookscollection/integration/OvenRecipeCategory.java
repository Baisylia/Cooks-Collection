package com.ncpbails.cookscollection.integration;

import com.ncpbails.cookscollection.CooksCollection;
import com.ncpbails.cookscollection.block.ModBlocks;
import com.ncpbails.cookscollection.item.ModItems;
import com.ncpbails.cookscollection.recipe.OvenRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class OvenRecipeCategory implements IRecipeCategory<OvenRecipe> {
    public final static ResourceLocation UID = new ResourceLocation(CooksCollection.MOD_ID, "baking");
    public final static ResourceLocation TEXTURE =
            new ResourceLocation(CooksCollection.MOD_ID, "textures/gui/oven_gui_jei.png");

    private final IDrawable background;
    private final IDrawable icon;

    public OvenRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.OVEN.get()));
    }

    @Override
    public RecipeType<OvenRecipe> getRecipeType() {
        return JEICooksCollectionPlugin.BAKING_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal("Shapeless Baking");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, OvenRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 30, 19).addIngredients(recipe.getIngredients().get(0));
        if (recipe.getIngredients().size() > 1) {
            builder.addSlot(RecipeIngredientRole.INPUT, 48, 19).addIngredients(recipe.getIngredients().get(1));
            if (recipe.getIngredients().size() > 2) {
                builder.addSlot(RecipeIngredientRole.INPUT, 66, 19).addIngredients(recipe.getIngredients().get(2));
                if (recipe.getIngredients().size() > 3) {
                    builder.addSlot(RecipeIngredientRole.INPUT, 30, 37).addIngredients(recipe.getIngredients().get(3));
                    if (recipe.getIngredients().size() > 4) {
                        builder.addSlot(RecipeIngredientRole.INPUT, 48, 37).addIngredients(recipe.getIngredients().get(4));
                        if (recipe.getIngredients().size() > 5) {
                            builder.addSlot(RecipeIngredientRole.INPUT, 66, 37).addIngredients(recipe.getIngredients().get(5));
                            if (recipe.getIngredients().size() > 6) {
                                builder.addSlot(RecipeIngredientRole.INPUT, 30, 55).addIngredients(recipe.getIngredients().get(6));
                                if (recipe.getIngredients().size() > 7) {
                                    builder.addSlot(RecipeIngredientRole.INPUT, 48, 55).addIngredients(recipe.getIngredients().get(7));
                                    if (recipe.getIngredients().size() > 8) {
                                        builder.addSlot(RecipeIngredientRole.INPUT, 66, 55).addIngredients(recipe.getIngredients().get(8));
        }}}}}}}}
        builder.addSlot(RecipeIngredientRole.OUTPUT, 124, 37).addItemStack(recipe.getResultItem());
    }
}