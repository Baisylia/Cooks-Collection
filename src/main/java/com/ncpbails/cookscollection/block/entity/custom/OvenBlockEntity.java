package com.ncpbails.cookscollection.block.entity.custom;

import com.ncpbails.cookscollection.block.custom.OvenBlock;
import com.ncpbails.cookscollection.block.entity.ModBlockEntities;
import com.ncpbails.cookscollection.client.ModSounds;
import com.ncpbails.cookscollection.recipe.OvenRecipe;
import com.ncpbails.cookscollection.recipe.OvenShapedRecipe;
import com.ncpbails.cookscollection.screen.OvenMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.tag.ModTags;

import java.util.Optional;

import static com.ncpbails.cookscollection.block.custom.OvenBlock.LIT;

public class OvenBlockEntity extends BlockEntity implements MenuProvider {

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 72;
    private int litTime = 0;
    static int countOutput = 1;
    private ContainerOpenersCounter openersCounter;

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
                return switch (index) {
                    case 0 -> OvenBlockEntity.this.progress;
                    case 1 -> OvenBlockEntity.this.maxProgress;
                    case 2 -> OvenBlockEntity.this.litTime;
                    default -> 0;
                };
            }

            public void set(int index, int value) {
                switch (index) {
                    case 0 -> OvenBlockEntity.this.progress = value;
                    case 1 -> OvenBlockEntity.this.maxProgress = value;
                    case 2 -> OvenBlockEntity.this.litTime = value;
                }
            }

            public int getCount() {
                return 3;
            }
        };
        this.openersCounter = new ContainerOpenersCounter() {
            protected void onOpen(Level level, BlockPos pos, BlockState state) {
                OvenBlockEntity.this.playSound(state, ModSounds.OVEN_OPEN.get());
                OvenBlockEntity.this.updateBlockState(state, true);
            }

            protected void onClose(Level level, BlockPos pos, BlockState state) {
                OvenBlockEntity.this.playSound(state, ModSounds.OVEN_CLOSE.get());
                OvenBlockEntity.this.updateBlockState(state, false);
            }

            protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int p_155069_, int p_155070_) {
            }

            protected boolean isOwnContainer(Player player) {
                if (player.containerMenu instanceof OvenMenu) {
                    BlockEntity be = ((OvenMenu) player.containerMenu).getBlockEntity();
                    return be == OvenBlockEntity.this;
                }
                return false;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.cookscollection.oven");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new OvenMenu(pContainerId, pInventory, this, this.data);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
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
    public void invalidateCaps() {
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
        pBlockEntity.recheckOpen();

        if (isFueled(pBlockEntity, pPos, pLevel)) {
            pBlockEntity.litTime = 1;
            setChanged(pLevel, pPos, pState);
        } else {
            pBlockEntity.litTime = 0;
            setChanged(pLevel, pPos, pState);
        }

        if (hasRecipe(pBlockEntity)) {
            pBlockEntity.progress++;
            setChanged(pLevel, pPos, pState);
            if (pBlockEntity.progress > pBlockEntity.maxProgress) {
                craftItem(pBlockEntity);
            }
        } else {
            pBlockEntity.resetProgress();
            setChanged(pLevel, pPos, pState);
        }
    }

    private static boolean hasRecipe(OvenBlockEntity entity) {
        Level level = entity.level;
        BlockPos pos = entity.getBlockPos();

        // Check if the oven is fueled (lit)
        if (!isFueled(entity, pos, level)) {
            return false;
        }

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
                level.setBlock(pos, entity.getBlockState().setValue(LIT, Boolean.TRUE), 3);
                return true;
            } else {
                level.setBlock(pos, entity.getBlockState().setValue(LIT, Boolean.FALSE), 3);
                return false;
            }
        } else {
            level.setBlock(pos, entity.getBlockState().setValue(LIT, Boolean.FALSE), 3);
            return false;
        }
    }

    private static void craftItem(OvenBlockEntity entity) {
        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        // Check for OvenShapedRecipe or OvenRecipe
        Optional<OvenShapedRecipe> shapedMatch = level.getRecipeManager()
                .getRecipeFor(OvenShapedRecipe.Type.INSTANCE, inventory, level);
        Optional<OvenRecipe> recipeMatch = level.getRecipeManager()
                .getRecipeFor(OvenRecipe.Type.INSTANCE, inventory, level);

        ItemStack result = ItemStack.EMPTY;
        if (shapedMatch.isPresent()) {
            result = shapedMatch.get().getResultItem(level.registryAccess());
        } else if (recipeMatch.isPresent()) {
            result = recipeMatch.get().getResultItem(level.registryAccess());
        }

        if (!result.isEmpty()) {
            // Handle crafting remainders (e.g., buckets)
            for (int i = 0; i < 9; ++i) {
                ItemStack slotStack = entity.itemHandler.getStackInSlot(i);
                if (slotStack.hasCraftingRemainingItem()) {
                    Direction direction = entity.getBlockState().getValue(OvenBlock.FACING).getCounterClockWise();
                    double x = entity.worldPosition.getX() + 0.5 + direction.getStepX() * 0.25;
                    double y = entity.worldPosition.getY() + 0.7;
                    double z = entity.worldPosition.getZ() + 0.5 + direction.getStepZ() * 0.25;
                    spawnItemEntity(entity.level, slotStack.getCraftingRemainingItem(), x, y, z,
                            direction.getStepX() * 0.08F, 0.25, direction.getStepZ() * 0.08F);
                }
                entity.itemHandler.extractItem(i, 1, false);
            }

            // Update output slot (slot 9)
            ItemStack outputSlot = entity.itemHandler.getStackInSlot(9);
            if (outputSlot.isEmpty() || outputSlot.is(result.getItem())) {
                int newCount = outputSlot.getCount() + result.getCount();
                entity.itemHandler.setStackInSlot(9, new ItemStack(result.getItem(), newCount));
            }

            entity.resetProgress();
        }
    }

    public static void spawnItemEntity(Level level, ItemStack stack, double x, double y, double z, double xMotion, double yMotion, double zMotion) {
        ItemEntity entity = new ItemEntity(level, x, y, z, stack);
        entity.setDeltaMovement(xMotion, yMotion, zMotion);
        level.addFreshEntity(entity);
    }

    private int getTheCount(ItemStack itemIn) {
        return itemIn.getCount();
    }

    private void resetProgress() {
        this.progress = 0;
        this.maxProgress = 72;
    }

    public void startOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.openersCounter.incrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    public void stopOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.openersCounter.decrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    public void recheckOpen() {
        if (!this.remove) {
            this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    void updateBlockState(BlockState state, boolean open) {
        this.level.setBlock(this.getBlockPos(), state.setValue(OvenBlock.OPEN, open), 3);
    }

    void playSound(BlockState state, SoundEvent sound) {
        Vec3i normal = state.getValue(OvenBlock.FACING).getNormal();
        double x = this.worldPosition.getX() + 0.5 + normal.getX() / 2.0;
        double y = this.worldPosition.getY() + 0.5 + normal.getY() / 2.0;
        double z = this.worldPosition.getZ() + 0.5 + normal.getZ() / 2.0;
        this.level.playSound(null, x, y, z, sound, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
    }

    public void setRecipeUsed(OvenRecipe ovenRecipe) {
    }
}