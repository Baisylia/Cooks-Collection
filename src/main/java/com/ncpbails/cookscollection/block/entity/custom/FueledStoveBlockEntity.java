package com.ncpbails.cookscollection.block.entity.custom;

import com.ncpbails.cookscollection.block.custom.FueledStoveBlock;
import com.ncpbails.cookscollection.block.entity.ModBlockEntities;
import com.ncpbails.cookscollection.screen.FueledStoveMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vectorwing.farmersdelight.common.mixin.accessor.RecipeManagerAccessor;
import vectorwing.farmersdelight.common.utility.ItemUtils;

import javax.annotation.Nullable;
import java.util.Optional;

public class FueledStoveBlockEntity extends BlockEntity implements MenuProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final VoxelShape GRILLING_AREA = Block.box(3.0F, 0.0F, 3.0F, 13.0F, 1.0F, 13.0F);
	private static final int INPUT_SLOT = 0;
	private static final int INVENTORY_SLOT_COUNT = 1;

	private final ItemStackHandler inventory = new ItemStackHandler(INVENTORY_SLOT_COUNT) {
		@Override
		protected void onContentsChanged(int slot) {
			setChanged();
			inventoryChanged();
		}
	};
	private final ContainerData data = new ContainerData() {
		@Override
		public int get(int index) {
			return switch (index) {
				case 0 -> cookingTime;
				case 1 -> cookingTimeTotal;
				case 2 -> burnTime;
				case 3 -> burnTimeTotal;
				default -> 0;
			};
		}

		@Override
		public void set(int index, int value) {
			switch (index) {
				case 0 -> cookingTime = value;
				case 1 -> cookingTimeTotal = value;
				case 2 -> burnTime = value;
				case 3 -> burnTimeTotal = value;
			}
		}

		@Override
		public int getCount() {
			return 4;
		}
	};

	private int cookingTime;
	private int cookingTimeTotal;
	private int burnTime;
	private int burnTimeTotal;
	private ResourceLocation lastRecipeID;

	public FueledStoveBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.FUELED_STOVE.get(), pos, state);
		this.cookingTime = 0;
		this.cookingTimeTotal = 0;
		this.burnTime = 0;
		this.burnTimeTotal = 0;
		this.lastRecipeID = null;
		LOGGER.info("Initialized FueledStoveBlockEntity at {} with MenuProvider from net.minecraft.world", pos);
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		inventory.deserializeNBT(compound.getCompound("Inventory"));
		cookingTime = compound.getInt("CookingTime");
		cookingTimeTotal = compound.getInt("CookingTimeTotal");
		burnTime = compound.getInt("BurnTime");
		burnTimeTotal = compound.getInt("BurnTimeTotal");
		if (compound.contains("LastRecipe")) {
			lastRecipeID = new ResourceLocation(compound.getString("LastRecipe"));
		}
		LOGGER.debug("Loaded NBT at {}: burnTime={}, burnTimeTotal={}", worldPosition, burnTime, burnTimeTotal);
	}

	@Override
	protected void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		compound.put("Inventory", inventory.serializeNBT());
		compound.putInt("CookingTime", cookingTime);
		compound.putInt("CookingTimeTotal", cookingTimeTotal);
		compound.putInt("BurnTime", burnTime);
		compound.putInt("BurnTimeTotal", burnTimeTotal);
		if (lastRecipeID != null) {
			compound.putString("LastRecipe", lastRecipeID.toString());
		}
	}

	private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
		if (cap == ForgeCapabilities.ITEM_HANDLER) {
			return lazyItemHandler.cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void onLoad() {
		super.onLoad();
		lazyItemHandler = LazyOptional.of(() -> inventory);
		LOGGER.info("Loaded FueledStoveBlockEntity capabilities at {}", worldPosition);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		lazyItemHandler.invalidate();
	}

	public static void cookingTick(Level level, BlockPos pos, BlockState state, FueledStoveBlockEntity stove) {
		if (stove.isStoveBlockedAbove()) {
			if (!ItemUtils.isInventoryEmpty(stove.inventory)) {
				ItemUtils.dropItems(level, pos, stove.inventory);
				stove.inventoryChanged();
			}
			return;
		}

		boolean isLit = state.getValue(BlockStateProperties.LIT);
		ItemStack inputStack = stove.inventory.getStackInSlot(INPUT_SLOT);
		LOGGER.debug("Cooking tick at {}: isLit={}, burnTime={}, inputStack={}", pos, isLit, stove.burnTime, inputStack);

		if (isLit && stove.burnTime <= 0 && !inputStack.isEmpty()) {
			int fuelBurnTime = ForgeHooks.getBurnTime(inputStack, RecipeType.SMELTING);
			if (fuelBurnTime > 0) {
				stove.burnTime = stove.burnTimeTotal = fuelBurnTime;
				ItemStack remainder = inputStack.getCraftingRemainingItem();
				stove.inventory.extractItem(INPUT_SLOT, 1, false);
				if (!remainder.isEmpty()) {
					stove.inventory.setStackInSlot(INPUT_SLOT, remainder);
				}
				stove.inventoryChanged();
				LOGGER.debug("Consumed fuel {} at {}, set burnTime={}", inputStack, pos, stove.burnTime);
			} else {
				level.setBlock(pos, state.setValue(BlockStateProperties.LIT, false), 3);
				isLit = false;
				LOGGER.debug("No valid fuel at {}, extinguished stove", pos);
			}
		}

		if (isLit && stove.burnTime > 0) {
			stove.burnTime--;
			LOGGER.debug("Decremented burnTime to {} at {}", stove.burnTime, pos);
			if (!inputStack.isEmpty()) {
				Optional<CampfireCookingRecipe> recipe = stove.getMatchingRecipe(inputStack);
				if (recipe.isPresent()) {
					stove.cookingTime++;
					if (stove.cookingTime >= stove.cookingTimeTotal) {
						ItemStack result = recipe.get().getResultItem(level.registryAccess());
						ItemUtils.spawnItemEntity(level, result.copy(),
								pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
								level.random.nextGaussian() * 0.01, 0.1, level.random.nextGaussian() * 0.01);
						stove.inventory.extractItem(INPUT_SLOT, 1, false);
						stove.cookingTime = 0;
						stove.lastRecipeID = recipe.get().getId();
						stove.inventoryChanged();
						LOGGER.debug("Cooked item at {}, output={}", pos, result);
					}
				} else {
					stove.cookingTime = 0;
				}
			} else {
				stove.cookingTime = 0;
			}
		} else if (stove.burnTime <= 0 && isLit) {
			level.setBlock(pos, state.setValue(BlockStateProperties.LIT, false), 3);
			stove.cookingTime = 0;
			LOGGER.debug("Extinguished stove at {}: burnTime={}", pos, stove.burnTime);
		}
	}

	public static void animationTick(Level level, BlockPos pos, BlockState state, FueledStoveBlockEntity stove) {
		if (state.getValue(BlockStateProperties.LIT) && level.random.nextFloat() < 0.2F) {
			Direction direction = state.getValue(FueledStoveBlock.FACING);
			double x = pos.getX() + 0.5;
			double y = pos.getY() + 1.0;
			double z = pos.getZ() + 0.5;
			for (int k = 0; k < 3; ++k) {
				level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 5.0E-4, 0.0);
				level.addParticle(ParticleTypes.FLAME, x, y, z, 0.0, 0.0, 0.0);
			}
			LOGGER.debug("Added animation particles at {}", pos);
		}
	}

	public boolean addItem(ItemStack itemStackIn, CampfireCookingRecipe recipe) {
		if (inventory.getStackInSlot(INPUT_SLOT).isEmpty()) {
			cookingTimeTotal = recipe.getCookingTime();
			cookingTime = 0;
			inventory.setStackInSlot(INPUT_SLOT, itemStackIn.split(1));
			lastRecipeID = recipe.getId();
			inventoryChanged();
			LOGGER.debug("Added item {} to input slot at {}", itemStackIn, worldPosition);
			return true;
		}
		return false;
	}

	public boolean ignite() {
		ItemStack inputStack = inventory.getStackInSlot(INPUT_SLOT);
		int fuelBurnTime = ForgeHooks.getBurnTime(inputStack, RecipeType.SMELTING);
		if (fuelBurnTime > 0) {
			this.burnTime = this.burnTimeTotal = fuelBurnTime;
			ItemStack remainder = inputStack.getCraftingRemainingItem();
			inventory.extractItem(INPUT_SLOT, 1, false);
			if (!remainder.isEmpty()) {
				inventory.setStackInSlot(INPUT_SLOT, remainder);
			}
			inventoryChanged();
			LOGGER.debug("Ignited stove with fuel {} at {}, set burnTime={}", inputStack, worldPosition, fuelBurnTime);
			return true;
		}
		LOGGER.debug("Failed to ignite stove at {}: no valid fuel in input slot", worldPosition);
		return false;
	}

	public Optional<CampfireCookingRecipe> getMatchingRecipe(ItemStack stack) {
		if (level == null || stack.isEmpty()) return Optional.empty();

		if (lastRecipeID != null) {
			Recipe<Container> recipe = ((RecipeManagerAccessor) level.getRecipeManager())
					.getRecipeMap(RecipeType.CAMPFIRE_COOKING)
					.get(lastRecipeID);
			if (recipe instanceof CampfireCookingRecipe && recipe.matches(new SimpleContainer(stack), level)) {
				return Optional.of((CampfireCookingRecipe) recipe);
			}
		}
		return level.getRecipeManager().getRecipeFor(RecipeType.CAMPFIRE_COOKING, new SimpleContainer(stack), level);
	}

	public boolean isStoveBlockedAbove() {
		if (level != null) {
			BlockState above = level.getBlockState(worldPosition.above());
			boolean blocked = Shapes.joinIsNotEmpty(GRILLING_AREA, above.getShape(level, worldPosition.above()), BooleanOp.AND);
			LOGGER.debug("Stove at {} blocked above: {}", worldPosition, blocked);
			return blocked;
		}
		return false;
	}

	public NonNullList<ItemStack> getInventory() {
		NonNullList<ItemStack> items = NonNullList.withSize(INVENTORY_SLOT_COUNT, ItemStack.EMPTY);
		for (int i = 0; i < INVENTORY_SLOT_COUNT; i++) {
			items.set(i, inventory.getStackInSlot(i));
		}
		return items;
	}

	private void inventoryChanged() {
		setChanged();
		if (level != null) {
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
			LOGGER.debug("Inventory changed, block updated at {}", worldPosition);
		}
	}

	@Override
	public net.minecraft.network.chat.Component getDisplayName() {
		LOGGER.info("FueledStoveBlockEntity getDisplayName called");
		return net.minecraft.network.chat.Component.translatable("block.cookscollection.fueled_stove");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
		LOGGER.info("Creating FueledStoveMenu for containerId: {}", containerId);
		return new FueledStoveMenu(containerId, inventory, this, this.data);
	}
}