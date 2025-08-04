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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ExperienceOrb;
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

public class OvenBlockEntity extends BlockEntity implements MenuProvider {

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 72;
    private int litTime = 0;
    private ResourceLocation lastRecipeID;
    private float experienceToDrop = 0.0F;
    static int countOutput = 1;
    private ContainerOpenersCounter openersCounter;

    private final ItemStackHandler itemHandler = new ItemStackHandler(11) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (slot < 9) {
                if (!isSameRecipe()) {
                    lastRecipeID = null;
                }
            }
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot == 9 && !simulate && !level.isClientSide) {
                ItemStack extracted = super.extractItem(slot, amount, simulate);
                if (!extracted.isEmpty() && experienceToDrop > 0) {
                    float xpPerItem = experienceToDrop / getTheCount(extracted);
                    float totalXp = xpPerItem * amount;
                    spawnExperience(level, worldPosition, totalXp);
                    experienceToDrop = Math.max(0, experienceToDrop - totalXp);
                    setChanged();
                }
                return extracted;
            }
            return super.extractItem(slot, amount, simulate);
        }

        private boolean isSameRecipe() {
            SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                inventory.setItem(i, itemHandler.getStackInSlot(i));
            }
            Optional<OvenShapedRecipe> shapedMatch = level.getRecipeManager()
                    .getRecipeFor(OvenShapedRecipe.Type.INSTANCE, inventory, level);
            Optional<OvenRecipe> recipeMatch = level.getRecipeManager()
                    .getRecipeFor(OvenRecipe.Type.INSTANCE, inventory, level);
            ResourceLocation currentRecipeID = null;
            if (shapedMatch.isPresent()) {
                currentRecipeID = shapedMatch.get().getId();
            } else if (recipeMatch.isPresent()) {
                currentRecipeID = recipeMatch.get().getId();
            }
            return lastRecipeID != null && lastRecipeID.equals(currentRecipeID);
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
        tag.putFloat("oven.experience", experienceToDrop);
        if (lastRecipeID != null) {
            tag.putString("LastRecipe", lastRecipeID.toString());
        }
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        progress = nbt.getInt("oven.progress");
        litTime = nbt.getInt("oven.lit_time");
        maxProgress = nbt.getInt("oven.max_progress");
        experienceToDrop = nbt.getFloat("oven.experience");
        if (nbt.contains("LastRecipe")) {
            lastRecipeID = new ResourceLocation(nbt.getString("LastRecipe"));
        }
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
        if (experienceToDrop > 0 && !this.level.isClientSide) {
            spawnExperience(this.level, this.worldPosition, experienceToDrop);
            experienceToDrop = 0;
        }
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
            if (pBlockEntity.progress >= pBlockEntity.maxProgress) {
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

        if (!isFueled(entity, pos, level)) {
            return false;
        }

        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        Optional<OvenShapedRecipe> shapedMatch = level.getRecipeManager()
                .getRecipeFor(OvenShapedRecipe.Type.INSTANCE, inventory, level);
        Optional<OvenRecipe> recipeMatch = level.getRecipeManager()
                .getRecipeFor(OvenRecipe.Type.INSTANCE, inventory, level);

        if (shapedMatch.isPresent()) {
            ResourceLocation newRecipeID = shapedMatch.get().getId();
            if (entity.lastRecipeID == null || !entity.lastRecipeID.equals(newRecipeID)) {
                entity.progress = 0;
                entity.maxProgress = shapedMatch.get().getCookTime();
                entity.lastRecipeID = newRecipeID;
            }
            return true;
        } else if (recipeMatch.isPresent()) {
            ResourceLocation newRecipeID = recipeMatch.get().getId();
            if (entity.lastRecipeID == null || !entity.lastRecipeID.equals(newRecipeID)) {
                entity.progress = 0;
                entity.maxProgress = recipeMatch.get().getCookTime();
                entity.lastRecipeID = newRecipeID;
            }
            return true;
        }
        return false;
    }

    static boolean isFueled(OvenBlockEntity entity, BlockPos pos, Level level) {
        BlockState stateBelow = level.getBlockState(pos.below());
        if (stateBelow.hasProperty(BlockStateProperties.LIT) ? stateBelow.getValue(BlockStateProperties.LIT) : true) {
            if (stateBelow.is(ModTags.HEAT_SOURCES) || stateBelow.is(ModTags.HEAT_CONDUCTORS)) {
                level.setBlock(pos, entity.getBlockState().setValue(OvenBlock.LIT, Boolean.TRUE), 3);
                return true;
            } else {
                level.setBlock(pos, entity.getBlockState().setValue(OvenBlock.LIT, Boolean.FALSE), 3);
                return false;
            }
        } else {
            level.setBlock(pos, entity.getBlockState().setValue(OvenBlock.LIT, Boolean.FALSE), 3);
            return false;
        }
    }

    private static void craftItem(OvenBlockEntity entity) {
        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        assert level != null;
        Optional<OvenShapedRecipe> shapedMatch = level.getRecipeManager()
                .getRecipeFor(OvenShapedRecipe.Type.INSTANCE, inventory, level);
        Optional<OvenRecipe> recipeMatch = level.getRecipeManager()
                .getRecipeFor(OvenRecipe.Type.INSTANCE, inventory, level);

        ItemStack result = ItemStack.EMPTY;
        float experience = 0.0F;
        if (shapedMatch.isPresent()) {
            result = shapedMatch.get().getResultItem(level.registryAccess());
            experience = shapedMatch.get().getExperience();
            entity.maxProgress = shapedMatch.get().getCookTime();
            entity.lastRecipeID = shapedMatch.get().getId();
        } else if (recipeMatch.isPresent()) {
            result = recipeMatch.get().getResultItem(level.registryAccess());
            experience = recipeMatch.get().getExperience();
            entity.maxProgress = recipeMatch.get().getCookTime();
            entity.lastRecipeID = recipeMatch.get().getId();
        }

        if (!result.isEmpty()) {
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

            ItemStack outputSlot = entity.itemHandler.getStackInSlot(9);
            if (outputSlot.isEmpty() || outputSlot.is(result.getItem())) {
                int newCount = outputSlot.getCount() + result.getCount();
                entity.itemHandler.setStackInSlot(9, new ItemStack(result.getItem(), newCount));
                entity.experienceToDrop += experience * result.getCount();
            }

            entity.resetProgress();
        }
    }

    private static void spawnExperience(Level level, BlockPos pos, float experience) {
        if (!level.isClientSide) {
            int exp = (int) experience;
            float fractional = experience - exp;
            if (fractional > level.random.nextFloat()) {
                exp++;
            }
            ExperienceOrb.award((ServerLevel) level, pos.getCenter(), exp);
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
        this.maxProgress = ovenRecipe.getCookTime();
        this.progress = 0;
        this.lastRecipeID = ovenRecipe.getId();
        setChanged();
    }

    public void setRecipeUsed(OvenShapedRecipe ovenShapedRecipe) {
        this.maxProgress = ovenShapedRecipe.getCookTime();
        this.progress = 0;
        this.lastRecipeID = ovenShapedRecipe.getId();
        setChanged();
    }
}