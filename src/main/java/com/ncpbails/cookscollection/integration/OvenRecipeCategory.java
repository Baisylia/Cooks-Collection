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
        this.background = helper.createDrawable(TEXTURE, 0, 0, 124, 58);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.OVEN.get()));
    }

    @Override
    public RecipeType<OvenRecipe> getRecipeType() {
        return JEICooksCollectionPlugin.BAKING_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("recipe.cookscollection.shapeless_baking");
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
        int start = 3;
        int offset = 18;
        int offset2 = offset+offset;
        builder.addSlot(RecipeIngredientRole.INPUT, start, start).addIngredients(recipe.getIngredients().get(0));
        if (recipe.getIngredients().size() > 1) {
            builder.addSlot(RecipeIngredientRole.INPUT, start+offset, start).addIngredients(recipe.getIngredients().get(1));
            if (recipe.getIngredients().size() > 2) {
                builder.addSlot(RecipeIngredientRole.INPUT, start+offset2, start).addIngredients(recipe.getIngredients().get(2));
                if (recipe.getIngredients().size() > 3) {
                    builder.addSlot(RecipeIngredientRole.INPUT, start, start+offset).addIngredients(recipe.getIngredients().get(3));
                    if (recipe.getIngredients().size() > 4) {
                        builder.addSlot(RecipeIngredientRole.INPUT, start+offset, start+offset).addIngredients(recipe.getIngredients().get(4));
                        if (recipe.getIngredients().size() > 5) {
                            builder.addSlot(RecipeIngredientRole.INPUT, start+offset2, start+offset).addIngredients(recipe.getIngredients().get(5));
                            if (recipe.getIngredients().size() > 6) {
                                builder.addSlot(RecipeIngredientRole.INPUT, start, start+offset2).addIngredients(recipe.getIngredients().get(6));
                                if (recipe.getIngredients().size() > 7) {
                                    builder.addSlot(RecipeIngredientRole.INPUT, start+offset, start+offset2).addIngredients(recipe.getIngredients().get(7));
                                    if (recipe.getIngredients().size() > 8) {
                                        builder.addSlot(RecipeIngredientRole.INPUT, start+offset2, start+offset2).addIngredients(recipe.getIngredients().get(8));
        }}}}}}}}
        builder.addSlot(RecipeIngredientRole.OUTPUT, 97, 21).addItemStack(recipe.getResultItem());
    }
}