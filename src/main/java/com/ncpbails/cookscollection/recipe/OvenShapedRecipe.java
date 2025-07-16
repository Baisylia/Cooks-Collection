package com.ncpbails.cookscollection.recipe;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.*;
import com.ncpbails.cookscollection.CooksCollection;
import com.ncpbails.cookscollection.client.recipebook.OvenRecipeBookTab;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.util.RecipeMatcher;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class OvenShapedRecipe implements Recipe<SimpleContainer> {
    static int MAX_WIDTH = 3;
    static int MAX_HEIGHT = 3;

    public static void setCraftingSize(int width, int height) {
        if (MAX_WIDTH < width) MAX_WIDTH = width;
        if (MAX_HEIGHT < height) MAX_HEIGHT = height;
    }

    final int width;
    final int height;
    private final ResourceLocation id;
    private final ItemStack output;
    private final NonNullList<Ingredient> recipeItems;
    private final int cookTime;
    private final boolean isSimple;
    private final OvenRecipeBookTab recipeBookTab;

    public OvenShapedRecipe(int width, int height, ResourceLocation id, ItemStack output, NonNullList<Ingredient> recipeItems, int cookTime, @Nullable OvenRecipeBookTab recipeBookTab) {
        this.width = width;
        this.height = height;
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
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        ItemStack outputSlot = pContainer.getItem(9);
        if (!outputSlot.isEmpty() && !ItemStack.isSameItemSameTags(this.getResultItem(pLevel.registryAccess()), outputSlot)) {
            return false;
        }

        if (!outputSlot.isEmpty() && outputSlot.getCount() >= outputSlot.getMaxStackSize()) {
            return false;
        }

        boolean[][] slotUsed = new boolean[3][3]; // Track which slots are used

        // Iterate over the crafting grid
        for (int offsetX = 0; offsetX <= 3 - this.getWidth(); ++offsetX) {
            for (int offsetY = 0; offsetY <= 3 - this.getHeight(); ++offsetY) {
                if (checkIngredients(pContainer, offsetX, offsetY, slotUsed)) {
                    if (areOtherSlotsEmpty(pContainer, offsetX, offsetY)) {
                        return true; // Match found, return true
                    }
                }
            }
        }

        return false; // No match found
    }

    private boolean areOtherSlotsEmpty(SimpleContainer pContainer, int offsetX, int offsetY) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                if (i < offsetX || i >= offsetX + this.getWidth() || j < offsetY || j >= offsetY + this.getHeight()) {
                    ItemStack itemStack = pContainer.getItem(i + j * 3); // Use a fixed grid size of 3x3
                    if (!itemStack.isEmpty()) {
                        return false; // Slot is not empty
                    }
                }
            }
        }
        return true; // All other slots are empty
    }

    private boolean checkIngredients(SimpleContainer pContainer, int offsetX, int offsetY, boolean[][] slotUsed) {
        // Iterate over the recipe's dimensions
        for (int i = 0; i < this.getWidth(); ++i) {
            for (int j = 0; j < this.getHeight(); ++j) {
                int gridX = i + offsetX;
                int gridY = j + offsetY;

                // Check if the current position is within the crafting grid
                if (gridX >= 3 || gridY >= 3) {
                    continue;
                }

                // Check if the slot is already used by another recipe
                if (slotUsed[gridX][gridY]) {
                    return false;
                }

                Ingredient recipeIngredient = this.recipeItems.get(i + j * this.getWidth());
                ItemStack gridStack = pContainer.getItem(gridX + gridY * 3); // Use a fixed grid size of 3x3

                // Check if the ingredient matches the item in the crafting grid
                if (!recipeIngredient.test(gridStack)) {
                    return false;
                }

                // Mark the slot as used
                slotUsed[gridX][gridY] = true;
            }
        }

        return true; // All ingredients matched
    }

    @Override
    public ItemStack assemble(SimpleContainer container, RegistryAccess registryAccess) {
        return output.copy();
    }

    public int getWidth() {
        return this.width;
    }

    public int getRecipeWidth() {
        return getWidth();
    }

    public int getHeight() {
        return this.height;
    }

    public int getRecipeHeight() {
        return getHeight();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= this.width && height >= this.height;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<OvenShapedRecipe> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "baking_shaped";
    }

    static NonNullList<Ingredient> dissolvePattern(String[] pattern, Map<String, Ingredient> key, int width, int height) {
        NonNullList<Ingredient> nonNullList = NonNullList.withSize(width * height, Ingredient.EMPTY);
        Set<String> set = Sets.newHashSet(key.keySet());
        set.remove(" ");

        for (int i = 0; i < pattern.length; ++i) {
            for (int j = 0; j < pattern[i].length(); ++j) {
                String s = pattern[i].substring(j, j + 1);
                Ingredient ingredient = key.get(s);
                if (ingredient == null) {
                    throw new JsonSyntaxException("Pattern references symbol '" + s + "' but it's not defined in the key");
                }
                set.remove(s);
                nonNullList.set(j + width * i, ingredient);
            }
        }

        if (!set.isEmpty()) {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
        }
        return nonNullList;
    }

    @VisibleForTesting
    static String[] shrink(String... pattern) {
        int i = Integer.MAX_VALUE;
        int j = 0;
        int k = 0;
        int l = 0;

        for (int i1 = 0; i1 < pattern.length; ++i1) {
            String s = pattern[i1];
            i = Math.min(i, firstNonSpace(s));
            int j1 = lastNonSpace(s);
            j = Math.max(j, j1);
            if (j1 < 0) {
                if (k == i1) {
                    ++k;
                }
                ++l;
            } else {
                l = 0;
            }
        }

        if (pattern.length == l) {
            return new String[0];
        }

        String[] astring = new String[pattern.length - l - k];
        for (int k1 = 0; k1 < astring.length; ++k1) {
            astring[k1] = pattern[k1 + k].substring(i, j + 1);
        }
        return astring;
    }

    public boolean isIncomplete() {
        NonNullList<Ingredient> nonNullList = this.getIngredients();
        return nonNullList.isEmpty() || nonNullList.stream()
                .filter(ingredient -> !ingredient.isEmpty())
                .anyMatch(ingredient -> net.minecraftforge.common.ForgeHooks.hasNoElements(ingredient));
    }

    private static int firstNonSpace(String s) {
        int i = 0;
        while (i < s.length() && s.charAt(i) == ' ') {
            ++i;
        }
        return i;
    }

    private static int lastNonSpace(String s) {
        int i = s.length() - 1;
        while (i >= 0 && s.charAt(i) == ' ') {
            --i;
        }
        return i;
    }

    static String[] patternFromJson(JsonArray patternArray) {
        String[] astring = new String[patternArray.size()];
        if (astring.length > MAX_HEIGHT) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, " + MAX_HEIGHT + " is maximum");
        }
        if (astring.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        }

        for (int i = 0; i < astring.length; ++i) {
            String s = GsonHelper.convertToString(patternArray.get(i), "pattern[" + i + "]");
            if (s.length() > MAX_WIDTH) {
                throw new JsonSyntaxException("Invalid pattern: too many columns, " + MAX_WIDTH + " is maximum");
            }
            if (i > 0 && astring[0].length() != s.length()) {
                throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
            }
            astring[i] = s;
        }
        return astring;
    }

    static Map<String, Ingredient> keyFromJson(JsonObject keyObj) {
        Map<String, Ingredient> map = Maps.newHashMap();
        for (Map.Entry<String, JsonElement> entry : keyObj.entrySet()) {
            if (entry.getKey().length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
            }
            if (" ".equals(entry.getKey())) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }
            map.put(entry.getKey(), Ingredient.fromJson(entry.getValue()));
        }
        map.put(" ", Ingredient.EMPTY);
        return map;
    }

    public static ItemStack itemStackFromJson(JsonObject json) {
        return CraftingHelper.getItemStack(json, true, true);
    }

    public static Item itemFromJson(JsonObject json) {
        String s = GsonHelper.getAsString(json, "item");
        Item item = CraftingHelper.getItem(String.valueOf(new ResourceLocation(s)), true);
        if (item == Items.AIR) {
            throw new JsonSyntaxException("Invalid item: " + s);
        }
        return item;
    }

    public static class Serializer implements RecipeSerializer<OvenShapedRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        private static final ResourceLocation NAME = new ResourceLocation(CooksCollection.MOD_ID, "baking_shaped");

        @Override
        public OvenShapedRecipe fromJson(ResourceLocation id, JsonObject json) {
            Map<String, Ingredient> map = keyFromJson(GsonHelper.getAsJsonObject(json, "key"));
            String[] astring = shrink(patternFromJson(GsonHelper.getAsJsonArray(json, "pattern")));
            int width = astring[0].length();
            int height = astring.length;
            NonNullList<Ingredient> nonNullList = dissolvePattern(astring, map, width, height);
            ItemStack itemStack = itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            int cookTimeIn = GsonHelper.getAsInt(json, "cooktime", 200);
            String tabName = GsonHelper.getAsString(json, "recipe_book_tab", null);
            OvenRecipeBookTab tab = tabName != null ? OvenRecipeBookTab.findByName(tabName) : null;
            return new OvenShapedRecipe(width, height, id, itemStack, nonNullList, cookTimeIn, tab);
        }

        @Override
        public OvenShapedRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            int width = buf.readVarInt();
            int height = buf.readVarInt();
            NonNullList<Ingredient> nonNullList = NonNullList.withSize(width * height, Ingredient.EMPTY);
            for (int k = 0; k < nonNullList.size(); ++k) {
                nonNullList.set(k, Ingredient.fromNetwork(buf));
            }
            ItemStack itemStack = buf.readItem();
            int cookTimeIn = buf.readVarInt();
            String tabName = buf.readUtf();
            OvenRecipeBookTab tab = tabName.isEmpty() ? null : OvenRecipeBookTab.findByName(tabName);
            return new OvenShapedRecipe(width, height, id, itemStack, nonNullList, cookTimeIn, tab);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, OvenShapedRecipe recipe) {
            buf.writeVarInt(recipe.width);
            buf.writeVarInt(recipe.height);
            for (Ingredient ingredient : recipe.recipeItems) {
                ingredient.toNetwork(buf);
            }
            buf.writeItem(recipe.getResultItem(RegistryAccess.EMPTY));
            buf.writeVarInt(recipe.cookTime);
            buf.writeUtf(recipe.recipeBookTab != null ? recipe.recipeBookTab.name : "");
        }
    }
}