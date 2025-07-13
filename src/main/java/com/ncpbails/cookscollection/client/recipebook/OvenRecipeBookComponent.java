package com.ncpbails.cookscollection.client.recipebook;

import com.ncpbails.cookscollection.recipe.OvenRecipe;
import com.ncpbails.cookscollection.screen.OvenMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.network.protocol.game.ServerboundPlaceRecipePacket;

import java.util.List;

public class OvenRecipeBookComponent extends RecipeBookComponent {
    private static final Component SEARCH_HINT = Component.translatable("gui.cookscollection.recipebook.search_hint");

    // Slot positions matching OvenMenu (x, y coordinates relative to GUI)
    private static final int[][] INPUT_SLOT_POSITIONS = {
            {30, 17}, // Slot 0
            {48, 17}, // Slot 1
            {66, 17}, // Slot 2
            {30, 35}, // Slot 3
            {48, 35}, // Slot 4
            {66, 35}, // Slot 5
            {30, 53}, // Slot 6
            {48, 53}, // Slot 7
            {66, 53}  // Slot 8
    };
    private static final int[] RESULT_SLOT_POSITION = {124, 35}; // Slot 9

    @Override
    protected void initFilterButtonTextures() {
        this.filterButton.initTextureValues(152, 182, 28, 18, RECIPE_BOOK_LOCATION);
    }

    @Override
    public void setupGhostRecipe(Recipe<?> recipe, List<Slot> slots) {
        if (!(recipe instanceof OvenRecipe ovenRecipe)) {
            return;
        }

        this.ghostRecipe.clear();

        // Add result to slot 9
        ItemStack result = ovenRecipe.getResultItem(null);
        this.ghostRecipe.setRecipe(recipe);
        this.ghostRecipe.addIngredient(Ingredient.of(result), RESULT_SLOT_POSITION[0], RESULT_SLOT_POSITION[1]);

        // Add ingredients to 3x3 grid (slots 0-8)
        List<Ingredient> inputs = ovenRecipe.getIngredients();
        int inputCount = Math.min(inputs.size(), 9);
        for (int i = 0; i < inputCount; i++) {
            Ingredient ingredient = inputs.get(i);
            if (!ingredient.isEmpty()) {
                int[] pos = INPUT_SLOT_POSITIONS[i];
                this.ghostRecipe.addIngredient(ingredient, pos[0], pos[1]);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isVisible() && this.menu instanceof OvenMenu ovenMenu && button == 0) {
            // Recipe book bounds (left-aligned, adjusted for OvenScreen)
            int bookX = this.minecraft.screen.width / 2 - 176 + 5; // leftPos + 5
            int bookY = this.minecraft.screen.height / 2 - 166 / 2 + 35; // topPos + 35
            System.out.println("Mouse clicked at: x=" + mouseX + ", y=" + mouseY + ", bookX=" + bookX + ", bookY=" + bookY);
            System.out.println("Recipe book visible: " + this.isVisible() + ", Menu: " + this.menu.getClass().getSimpleName() + ", Ghost recipe: " + (this.ghostRecipe.getRecipe() != null ? this.ghostRecipe.getRecipe().getId() : "none"));
            if (mouseX >= bookX && mouseX < bookX + 147 && mouseY >= bookY && mouseY < bookY + 166) {
                Recipe<?> recipe = this.ghostRecipe.getRecipe();
                if (recipe instanceof OvenRecipe ovenRecipe) {
                    Minecraft minecraft = Minecraft.getInstance();
                    if (minecraft.player != null && minecraft.gameMode != null && minecraft.getConnection() != null) {
                        // Log recipe click
                        System.out.println("Recipe clicked: " + ovenRecipe.getId());
                        // Check if all ingredients are available
                        List<Ingredient> inputs = ovenRecipe.getIngredients();
                        int inputCount = Math.min(inputs.size(), 9);
                        boolean canPlace = true;
                        int[] slotsUsed = new int[inputCount];
                        for (int i = 0; i < inputCount; i++) {
                            Ingredient ingredient = inputs.get(i);
                            if (!ingredient.isEmpty()) {
                                boolean found = false;
                                for (int j = 10; j < ovenMenu.slots.size(); j++) {
                                    ItemStack stack = ovenMenu.slots.get(j).getItem();
                                    if (ingredient.test(stack)) {
                                        slotsUsed[i] = j;
                                        found = true;
                                        break;
                                    }
                                }
                                if (!found) {
                                    canPlace = false;
                                    System.out.println("Client: Missing ingredient for slot " + i);
                                    break;
                                }
                            }
                        }
                        if (canPlace) {
                            // Clear oven slots (0-8)
                            for (int i = 0; i < 9; i++) {
                                ovenMenu.slots.get(i).set(ItemStack.EMPTY);
                                System.out.println("Client: Clearing slot " + i);
                            }
                            // Place ingredients in oven slots (0-8)
                            for (int i = 0; i < inputCount; i++) {
                                if (!inputs.get(i).isEmpty()) {
                                    int sourceSlot = slotsUsed[i];
                                    ItemStack stack = ovenMenu.slots.get(sourceSlot).getItem();
                                    System.out.println("Client: Placing " + stack.getItem() + " in slot " + i);
                                    minecraft.gameMode.handleInventoryMouseClick(ovenMenu.containerId, sourceSlot, 0, ClickType.PICKUP, minecraft.player);
                                    minecraft.gameMode.handleInventoryMouseClick(ovenMenu.containerId, i, 0, ClickType.PICKUP, minecraft.player);
                                }
                            }
                            // Send recipe placement packet to server
                            minecraft.getConnection().send(new ServerboundPlaceRecipePacket(ovenMenu.containerId, ovenRecipe, false));
                        } else {
                            System.out.println("Client: Cannot place recipe " + ovenRecipe.getId() + " due to missing ingredients");
                        }
                        // Clear ghost recipe to prevent locking
                        this.ghostRecipe.clear();
                        return true;
                    }
                } else {
                    // Clear ghost recipe if no valid recipe is selected
                    this.ghostRecipe.clear();
                    System.out.println("Client: No valid recipe selected, clearing ghost recipe");
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void slotClicked(Slot slot) {
        // Only handle clicks on oven slots (0-8)
        if (slot != null && slot.index < 9) {
            super.slotClicked(slot);
            // Clear ghost recipe to allow new recipe clicks
            this.ghostRecipe.clear();
            System.out.println("Client: Slot " + slot.index + " clicked, clearing ghost recipe");
        }
    }

    @Override
    protected Component getRecipeFilterName() {
        return SEARCH_HINT;
    }
}