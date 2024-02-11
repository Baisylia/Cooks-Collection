package com.ncpbails.cookscollection.block.entity.custom;

import com.ncpbails.cookscollection.block.custom.OvenBlock;
import com.ncpbails.cookscollection.block.entity.ModBlockEntities;
import com.ncpbails.cookscollection.recipe.OvenRecipe;
import com.ncpbails.cookscollection.recipe.OvenShapedRecipe;
import com.ncpbails.cookscollection.screen.OvenMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.block.CookingPotBlock;
import vectorwing.farmersdelight.common.tag.ModTags;
import vectorwing.farmersdelight.common.utility.ItemUtils;

import javax.annotation.Nonnull;
import java.util.Optional;

import static com.ncpbails.cookscollection.block.custom.OvenBlock.LIT;

public class OvenBlockEntity extends BlockEntity implements MenuProvider {

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 72;
    private int litTime = 0;
    static int countOutput = 1;
    private final ItemStackHandler itemHandler = new ItemStackHandler(11) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    public OvenBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ModBlockEntities.OVEN_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
        this.data = new ContainerData() {
            public int get(int index) {
                switch (index) {
                    case 0: return OvenBlockEntity.this.progress;
                    case 1: return OvenBlockEntity.this.maxProgress;
                    case 2: return OvenBlockEntity.this.litTime;
                    default: return 0;
                }
            }

            public void set(int index, int value) {
                switch(index) {
                    case 0: OvenBlockEntity.this.progress = value; break;
                    case 1: OvenBlockEntity.this.maxProgress = value; break;
                    case 2: OvenBlockEntity.this.litTime = value; break;
                }
            }

            public int getCount() {
                return 3;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Oven");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new OvenMenu(pContainerId, pInventory, this, this.data);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @javax.annotation.Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps()  {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("oven.progress", progress);
        tag.putInt("oven.lit_time", litTime);
        tag.putInt("oven.max_progress", maxProgress);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        progress = nbt.getInt("oven.progress");
        litTime = nbt.getInt("oven.lit_time");
        maxProgress = nbt.getInt("oven.max_progress");
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }


    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, OvenBlockEntity pBlockEntity) {
        if(isFueled(pBlockEntity, pPos, pLevel))
        {
            pBlockEntity.litTime = 1;
            setChanged(pLevel, pPos, pState);
        }
        else
        {
            pBlockEntity.litTime = 0;
            setChanged(pLevel, pPos, pState);
        }

        if(hasRecipe(pBlockEntity)) {
            pBlockEntity.progress++;
            setChanged(pLevel, pPos, pState);
            if(pBlockEntity.progress > pBlockEntity.maxProgress) {
            craftItem(pBlockEntity);
            }
        } else {
            pBlockEntity.resetProgress();
            setChanged(pLevel, pPos, pState);
        }
    }

    private static boolean hasRecipe(OvenBlockEntity entity) {
        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        // Check for OvenShapedRecipe
        Optional<OvenShapedRecipe> shapedMatch = level.getRecipeManager()
                .getRecipeFor(OvenShapedRecipe.Type.INSTANCE, inventory, level);

        // Check for OvenRecipe
        Optional<OvenRecipe> recipeMatch = level.getRecipeManager()
                .getRecipeFor(OvenRecipe.Type.INSTANCE, inventory, level);

        if (shapedMatch.isPresent()) {
            entity.maxProgress = shapedMatch.get().getCookTime();
            return true;
        } else if (recipeMatch.isPresent()) {
            entity.maxProgress = recipeMatch.get().getCookTime();
            return true;
        }

        return false;
    }

    static boolean isFueled(OvenBlockEntity entity, BlockPos pos, Level level) {
        BlockState stateBelow = level.getBlockState(pos.below());
        if (stateBelow.hasProperty(BlockStateProperties.LIT) ? stateBelow.getValue(BlockStateProperties.LIT) : true) {
            if (stateBelow.is(ModTags.HEAT_SOURCES) || stateBelow.is(ModTags.HEAT_CONDUCTORS)) {
                level.setBlock(pos, entity.getBlockState().setValue(LIT, Boolean.valueOf(true)), 3);
                return true;
            }
            else {
                System.out.println("NOT FUELED");
                level.setBlock(pos, entity.getBlockState().setValue(LIT, Boolean.valueOf(false)), 3);
                return false;
            }
        }
        else {
            System.out.println("NOT FUELED");
            level.setBlock(pos, entity.getBlockState().setValue(LIT, Boolean.valueOf(false)), 3);
            return false;
        }
    }

    private static void craftItem(OvenBlockEntity entity) {
        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        // Check for OvenShapedRecipe
        Optional<OvenShapedRecipe> shapedMatch = level.getRecipeManager()
                .getRecipeFor(OvenShapedRecipe.Type.INSTANCE, inventory, level);

        // Check for OvenRecipe
        Optional<OvenRecipe> recipeMatch = level.getRecipeManager()
                .getRecipeFor(OvenRecipe.Type.INSTANCE, inventory, level);

        if (shapedMatch.isPresent()) {
            for(int i = 0; i < 9; ++i) {
                ItemStack slotStack = entity.itemHandler.getStackInSlot(i);
                if (slotStack.hasCraftingRemainingItem()) {
                    Direction direction = ((Direction)entity.getBlockState().getValue(OvenBlock.FACING)).getCounterClockWise();
                    double x = (double)entity.worldPosition.getX() + 0.5 + (double)direction.getStepX() * 0.25;
                    double y = (double)entity.worldPosition.getY() + 0.7;
                    double z = (double)entity.worldPosition.getZ() + 0.5 + (double)direction.getStepZ() * 0.25;
                    spawnItemEntity(entity.level, entity.itemHandler.getStackInSlot(i).getCraftingRemainingItem(), x, y, z, (double)((float)direction.getStepX() * 0.08F), 0.25, (double)((float)direction.getStepZ() * 0.08F));
                }
            }

            for (int i = 0; i < 9; ++i) {
                entity.itemHandler.extractItem(i, 1, false);
            }
            inventory.getItem(9).is(shapedMatch.get().getResultItem().getItem());

            entity.itemHandler.setStackInSlot(9, new ItemStack(shapedMatch.get().getResultItem().getItem(),
                    entity.itemHandler.getStackInSlot(9).getCount() + entity.getTheCount(shapedMatch.get().getResultItem())));

            entity.resetProgress();

        } else if (recipeMatch.isPresent()) {
            for(int i = 0; i < 9; ++i) {
                ItemStack slotStack = entity.itemHandler.getStackInSlot(i);
                if (slotStack.hasCraftingRemainingItem()) {
                    Direction direction = ((Direction)entity.getBlockState().getValue(OvenBlock.FACING)).getCounterClockWise();
                    double x = (double)entity.worldPosition.getX() + 0.5 + (double)direction.getStepX() * 0.25;
                    double y = (double)entity.worldPosition.getY() + 0.7;
                    double z = (double)entity.worldPosition.getZ() + 0.5 + (double)direction.getStepZ() * 0.25;
                    spawnItemEntity(entity.level, entity.itemHandler.getStackInSlot(i).getCraftingRemainingItem(), x, y, z, (double)((float)direction.getStepX() * 0.08F), 0.25, (double)((float)direction.getStepZ() * 0.08F));
                }
            }

            for (int i = 0; i < 9; ++i) {
                entity.itemHandler.extractItem(i, 1, false);
            }
            inventory.getItem(9).is(recipeMatch.get().getResultItem().getItem());

            entity.itemHandler.setStackInSlot(9, new ItemStack(recipeMatch.get().getResultItem().getItem(),
                    entity.itemHandler.getStackInSlot(9).getCount() + entity.getTheCount(recipeMatch.get().getResultItem())));

            entity.resetProgress();
        }
    }
    public static void spawnItemEntity(Level level, ItemStack stack, double x, double y, double z, double xMotion, double yMotion, double zMotion) {
        ItemEntity entity = new ItemEntity(level, x, y, z, stack);
        entity.setDeltaMovement(xMotion, yMotion, zMotion);
        level.addFreshEntity(entity);
    }
    private int getTheCount (ItemStack itemIn)
    {
        return itemIn.getCount();
    }
    private void resetProgress() {
        this.progress = 0;
        this.maxProgress = 72;
    }
}


//entity.itemHandler.extractItem(0, 1, false);
//        entity.itemHandler.extractItem(1, 1, false);
//        entity.itemHandler.extractItem(2, 1, false);
//        entity.itemHandler.extractItem(3, 1, false);
//        entity.itemHandler.extractItem(4, 1, false);
//        entity.itemHandler.setStackInSlot(3, new ItemStack(ModItems.AVOCADO_TOAST.get(),
//                entity.itemHandler.getStackInSlot(5).getCount() + 1));