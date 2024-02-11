package com.ncpbails.cookscollection.integration;

import com.ncpbails.cookscollection.CooksCollection;
import com.ncpbails.cookscollection.block.ModBlocks;
import com.ncpbails.cookscollection.recipe.OvenRecipe;
import com.ncpbails.cookscollection.recipe.OvenShapedRecipe;
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

public class OvenShapedRecipeCategory implements IRecipeCategory<OvenShapedRecipe> {
    public final static ResourceLocation UID = new ResourceLocation(CooksCollection.MOD_ID, "baking_shaped");
    public final static ResourceLocation TEXTURE =
            new ResourceLocation(CooksCollection.MOD_ID, "textures/gui/oven_gui_jei.png");

    private final IDrawable background;
    private final IDrawable icon;

    public OvenShapedRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.OVEN.get()));
    }

    @Override
    public RecipeType<OvenShapedRecipe> getRecipeType() {
        return JEICooksCollectionPlugin.BAKING_SHAPED_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal("Shaped Baking");
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
    public void setRecipe(IRecipeLayoutBuilder builder, OvenShapedRecipe recipe, IFocusGroup focuses) {
        int startX = 30;
        int startY = 19;
        int index = 0;

        for (int y = 0; y < recipe.getHeight(); y++) {
            for (int x = 0; x < recipe.getWidth(); x++) {
                builder.addSlot(RecipeIngredientRole.INPUT, startX + x * 18, startY + y * 18)
                        .addIngredients(recipe.getIngredients().get(index));
                index++;
            }
        }

        // Add output slot
        builder.addSlot(RecipeIngredientRole.OUTPUT, 124, 37).addItemStack(recipe.getResultItem());
    }
}