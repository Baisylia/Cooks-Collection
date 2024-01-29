package com.ncpbails.cookscollection.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.ncpbails.cookscollection.CooksCollection;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.util.RecipeMatcher;

import java.util.EnumSet;
import java.util.List;

public class OvenRecipe implements Recipe<SimpleContainer> {

    private final ResourceLocation id;
    private final ItemStack output;
    private final NonNullList<Ingredient> recipeItems;
    //private final boolean isSimple;

    public OvenRecipe(ResourceLocation id, ItemStack output, NonNullList<Ingredient> recipeItems) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
        //this.isSimple = recipeItems.stream().allMatch(Ingredient::isSimple);
    }

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        StackedContents stackedcontents = new StackedContents();
        List<ItemStack> inputs = new java.util.ArrayList<>();
        int i = 0;

        for(int j = 0; j < 9; ++j) {
            ItemStack itemstack = pContainer.getItem(j);
            if (!itemstack.isEmpty()) {
                ++i;
                inputs.add(itemstack);
            }
            stackedcontents.accountStack(itemstack, 1);
        }
            //return i >= this.recipeItems.size() && (isSimple ? stackedcontents.canCraft(this, null) :
                //RecipeMatcher.findMatches(inputs, this.recipeItems) != null);

            return i >= this.recipeItems.size() && RecipeMatcher.findMatches(inputs, this.recipeItems) != null;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return recipeItems;
    }

    @Override
    public ItemStack assemble(SimpleContainer p_44001_) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<OvenRecipe> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "baking";
    }

    public static class Serializer implements RecipeSerializer<OvenRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID =
                new ResourceLocation(CooksCollection.MOD_ID,"baking");

        @Override
        public OvenRecipe fromJson(ResourceLocation id, JsonObject json) {
            NonNullList<Ingredient> inputs = readIngredients(GsonHelper.getAsJsonArray(json, "ingredients"));
            if (inputs.isEmpty()) {
                throw new JsonParseException("No ingredients for cooking recipe");
            } else if (inputs.size() > 9) {
                throw new JsonParseException("Too many ingredients for cooking recipe! The max is 9");
            } else {
                ItemStack output = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "output"), true);

                //int cookTimeIn = GsonHelper.getAsInt(json, "cookingtime", 200);
                return new OvenRecipe(id, output, inputs);
            }
        }

        private static NonNullList<Ingredient> readIngredients(JsonArray ingredientArray) {
            NonNullList<Ingredient> nonnulllist = NonNullList.create();

            for(int i = 0; i < ingredientArray.size(); ++i) {
                Ingredient ingredient = Ingredient.fromJson(ingredientArray.get(i));
                if (!ingredient.isEmpty()) {
                    nonnulllist.add(ingredient);
                }
            }

            return nonnulllist;
        }

        @Override
        public OvenRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(buf.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(buf));
            }

            ItemStack output = buf.readItem();
            return new OvenRecipe(id, output, inputs);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, OvenRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients()) {
                ing.toNetwork(buf);
            }
            buf.writeItemStack(recipe.getResultItem(), false);
        }
    }
}