package com.ncpbails.cookscollection.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.ncpbails.cookscollection.CooksCollection;
import com.ncpbails.cookscollection.client.recipebook.OvenRecipeBookTab;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.RecipeMatcher;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class OvenRecipe implements Recipe<SimpleContainer> {

    private final ResourceLocation id;
    private final ItemStack output;
    private final NonNullList<Ingredient> recipeItems;
    private final int cookTime;
    private final boolean isSimple;
    private final OvenRecipeBookTab recipeBookTab;

    public OvenRecipe(ResourceLocation id, ItemStack output, NonNullList<Ingredient> recipeItems, int cookTime, @Nullable OvenRecipeBookTab recipeBookTab) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
        this.cookTime = cookTime;
        this.isSimple = recipeItems.stream().allMatch(Ingredient::isSimple);
        this.recipeBookTab = recipeBookTab;
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
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return recipeItems;
    }

    public int getCookTime() {
        return this.cookTime;
    }

    @Nullable
    public OvenRecipeBookTab getRecipeBookTab() {
        return this.recipeBookTab;
    }

    @Override
    public boolean matches(SimpleContainer container, Level level) {
        ItemStack outputSlot = container.getItem(9);
        if (!outputSlot.isEmpty() && !ItemStack.isSameItemSameTags(this.getResultItem(level.registryAccess()), outputSlot)) {
            return false;
        }
        if (!outputSlot.isEmpty() && outputSlot.getCount() >= outputSlot.getMaxStackSize()) {
            return false;
        }

        StackedContents stackedContents = new StackedContents();
        List<ItemStack> inputs = new ArrayList<>();
        int i = 0;

        for (int j = 0; j < 9; ++j) {
            ItemStack itemStack = container.getItem(j);
            if (!itemStack.isEmpty()) {
                ++i;
                if (isSimple) {
                    stackedContents.accountStack(itemStack, 1);
                } else {
                    inputs.add(itemStack);
                }
            }
        }

        return i == this.recipeItems.size() && (isSimple ? stackedContents.canCraft(this, null) : RecipeMatcher.findMatches(inputs, this.recipeItems) != null);
    }

    @Override
    public ItemStack assemble(SimpleContainer container, RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
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
        private static final ResourceLocation NAME = new ResourceLocation(CooksCollection.MOD_ID, "baking");

        @Override
        public OvenRecipe fromJson(ResourceLocation resourceLocation, JsonObject json) {
            NonNullList<Ingredient> inputs = itemsFromJson(GsonHelper.getAsJsonArray(json, "ingredients"));
            if (inputs.isEmpty()) {
                throw new JsonParseException("No ingredients for baking recipe");
            } else if (inputs.size() > 9) {
                throw new JsonParseException("Too many ingredients for baking recipe. The maximum is 9");
            } else {
                ItemStack itemStack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
                int cookTimeIn = GsonHelper.getAsInt(json, "cooktime", 200);
                String tabName = GsonHelper.getAsString(json, "recipe_book_tab", null);
                OvenRecipeBookTab tab = tabName != null ? OvenRecipeBookTab.findByName(tabName) : null;
                return new OvenRecipe(resourceLocation, itemStack, inputs, cookTimeIn, tab);
            }
        }

        private static NonNullList<Ingredient> itemsFromJson(JsonArray ingredientArray) {
            NonNullList<Ingredient> nonNullList = NonNullList.create();

            for (int i = 0; i < ingredientArray.size(); ++i) {
                Ingredient ingredient = Ingredient.fromJson(ingredientArray.get(i));
                if (!ingredient.isEmpty()) {
                    nonNullList.add(ingredient);
                }
            }
            return nonNullList;
        }

        @Override
        public OvenRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            int i = buf.readVarInt();
            NonNullList<Ingredient> inputs = NonNullList.withSize(i, Ingredient.EMPTY);

            for (int j = 0; j < inputs.size(); ++j) {
                inputs.set(j, Ingredient.fromNetwork(buf));
            }

            ItemStack itemStack = buf.readItem();
            int cookTimeIn = buf.readVarInt();
            String tabName = buf.readUtf(32767);
            OvenRecipeBookTab tab = tabName.isEmpty() ? null : OvenRecipeBookTab.findByName(tabName);
            return new OvenRecipe(id, itemStack, inputs, cookTimeIn, tab);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, OvenRecipe recipe) {
            buf.writeVarInt(recipe.recipeItems.size());

            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buf);
            }

            buf.writeItem(recipe.getResultItem(RegistryAccess.EMPTY));
            buf.writeVarInt(recipe.cookTime);
            buf.writeUtf(recipe.recipeBookTab != null ? recipe.recipeBookTab.name : "");
        }
    }
}