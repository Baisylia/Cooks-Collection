package com.ncpbails.cookscollection.screen;

import com.ncpbails.cookscollection.block.ModBlocks;
import com.ncpbails.cookscollection.block.entity.custom.OvenBlockEntity;
import com.ncpbails.cookscollection.recipe.ModRecipeBookTypes;
import com.ncpbails.cookscollection.recipe.OvenRecipe;
import com.ncpbails.cookscollection.screen.slot.ModResultSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public class OvenMenu extends RecipeBookMenu<SimpleContainer> {
    private final OvenBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public OvenMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(3));
    }

    public OvenMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.OVEN_MENU.get(), pContainerId);
        checkContainerSize(inv, 10);
        blockEntity = ((OvenBlockEntity) entity);
        this.level = inv.player.level();
        this.data = data;

        // Add oven slots first (0-8 for input, 9 for output)
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            this.addSlot(new SlotItemHandler(handler, 0, 30, 17));  // Slot 0
            this.addSlot(new SlotItemHandler(handler, 1, 48, 17));  // Slot 1
            this.addSlot(new SlotItemHandler(handler, 2, 66, 17));  // Slot 2
            this.addSlot(new SlotItemHandler(handler, 3, 30, 35));  // Slot 3
            this.addSlot(new SlotItemHandler(handler, 4, 48, 35));  // Slot 4
            this.addSlot(new SlotItemHandler(handler, 5, 66, 35));  // Slot 5
            this.addSlot(new SlotItemHandler(handler, 6, 30, 53));  // Slot 6
            this.addSlot(new SlotItemHandler(handler, 7, 48, 53));  // Slot 7
            this.addSlot(new SlotItemHandler(handler, 8, 66, 53));  // Slot 8
            this.addSlot(new ModResultSlot(handler, 9, 124, 35));   // Slot 9
        });

        // Add player inventory slots (10-45)
        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        addDataSlots(data);

        // Log slot setup for debugging
//        System.out.println("OvenMenu slots: ");
//        for (int i = 0; i < slots.size(); i++) {
//            System.out.println("Slot " + i + ": x=" + slots.get(i).x + ", y=" + slots.get(i).y);
//        }
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public boolean isFueled() {
        return blockEntity.getBlockState().getValue(BlockStateProperties.LIT);
    }

    public int getCookProgressionScaled() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);  // Max Progress
        int progressArrowSize = 26; // This is the height in pixels of your arrow

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 10;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_SLOT_COUNT = 10;  // 9 input slots + 1 result slot

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // From player inventory (10-45) to oven slots (0-8, exclude 9)
        if (index >= VANILLA_FIRST_SLOT_INDEX && index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT - 1, false)) {
                return ItemStack.EMPTY;
            }
        }
        // From oven slots (0-8) or result slot (9) to player inventory (10-45)
        else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            //System.out.println("Invalid slotIndex: " + index);
            return ItemStack.EMPTY;
        }

        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                pPlayer, ModBlocks.OVEN.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    public BlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    @Override
    public void fillCraftSlotsStackedContents(StackedContents itemHelper) {
        // Only consider oven input slots (0-8) for recipe matching
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = this.slots.get(i).getItem();
            if (!stack.isEmpty()) {
                itemHelper.accountStack(stack, 1);
                //System.out.println("Filling slot " + i + " with " + stack.getItem());
            }
        }
    }

    @Override
    public void clearCraftingContent() {
        // Clear only oven input slots (0-8)
        for (int i = 0; i < 9; ++i) {
            this.slots.get(i).set(ItemStack.EMPTY);
            //System.out.println("Server: Clearing slot " + i);
        }
    }

    @Override
    public boolean recipeMatches(Recipe<? super SimpleContainer> recipe) {
        SimpleContainer tempContainer = new SimpleContainer(9);
        for (int i = 0; i < 9; ++i) {
            tempContainer.setItem(i, this.slots.get(i).getItem());
        }
        boolean matches = recipe.matches(tempContainer, this.level);
        //System.out.println("Server: Recipe " + recipe.getId() + " matches: " + matches);
        return matches;
    }

    @Override
    public void slotsChanged(Container container) {
        if (!this.level.isClientSide) {
            SimpleContainer tempContainer = new SimpleContainer(9);
            for (int i = 0; i < 9; ++i) {
                tempContainer.setItem(i, this.slots.get(i).getItem());
                //System.out.println("Server: Slot " + i + " contains " + this.slots.get(i).getItem());
            }
            // Reset cooking progress if recipe changes
            Recipe<? super SimpleContainer> newRecipe = this.level.getRecipeManager().getRecipeFor(OvenRecipe.Type.INSTANCE, tempContainer, this.level).orElse(null);
            //System.out.println("Server: Slots changed, new recipe: " + (newRecipe != null ? newRecipe.getId() : "none"));
            if (newRecipe != null) {
                // Assuming ContainerData index 0 is cooking progress, reset it
                this.data.set(0, 0);
                //System.out.println("Server: Reset cooking progress to 0");
            }
        }
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        //System.out.println("Server: Clicked slot " + slotId + ", button " + button + ", clickType " + clickType);
        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public int getResultSlotIndex() {
        return 9; // The result slot index
    }

    @Override
    public int getGridWidth() {
        return 3; // 3x3 grid
    }

    @Override
    public int getGridHeight() {
        return 3; // 3x3 grid
    }

    @Override
    public int getSize() {
        return 10; // 9 input slots + 1 result slot
    }

    @Override
    public boolean shouldMoveToInventory(int slotIndex) {
        // Only move items from oven slots (0-8) to inventory
        return slotIndex >= 0 && slotIndex < 9;
    }

    @Override
    public RecipeBookType getRecipeBookType() {
        return ModRecipeBookTypes.OVEN;
    }
}