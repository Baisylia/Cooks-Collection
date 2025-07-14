package com.ncpbails.cookscollection.block.custom;

import com.ncpbails.cookscollection.block.entity.ModBlockEntities;
import com.ncpbails.cookscollection.block.entity.custom.FueledStoveBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;
import vectorwing.farmersdelight.common.registry.ModDamageTypes;
import vectorwing.farmersdelight.common.registry.ModSounds;
import vectorwing.farmersdelight.common.utility.MathUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Optional;

@SuppressWarnings("deprecation")
public class FueledStoveBlock extends BaseEntityBlock {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	public FueledStoveBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, false));
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		ItemStack heldStack = player.getItemInHand(hand);

		if (!state.getValue(LIT)) {
			// Ignite with flint and steel
			if (heldStack.is(Items.FLINT_AND_STEEL)) {
				if (!level.isClientSide) {
					BlockEntity tileEntity = level.getBlockEntity(pos);
					if (tileEntity instanceof FueledStoveBlockEntity stoveEntity && stoveEntity.ignite()) {
						level.setBlock(pos, state.setValue(LIT, true), 3);
						level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, MathUtils.RAND.nextFloat() * 0.4F + 0.8F);
						if (!player.isCreative()) {
							heldStack.hurtAndBreak(1, player, action -> action.broadcastBreakEvent(hand));
						}
						//LOGGER.debug("Ignited stove at {} with fuel from input slot", pos);
					} else {
						//LOGGER.debug("Failed to ignite stove at {}: no valid fuel", pos);
					}
				}
				return InteractionResult.sidedSuccess(level.isClientSide);
			}
		} else {
			// Extinguish with water bucket
			if (heldStack.is(Items.WATER_BUCKET)) {
				if (!level.isClientSide) {
					level.playSound(null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
					level.setBlock(pos, state.setValue(LIT, false), 3);
					if (!player.isCreative()) {
						player.setItemInHand(hand, new ItemStack(Items.BUCKET));
					}
					//LOGGER.debug("Extinguished stove at {} with water bucket", pos);
				}
				return InteractionResult.sidedSuccess(level.isClientSide);
			}
			// Add campfire recipe item
			BlockEntity tileEntity = level.getBlockEntity(pos);
			if (tileEntity instanceof FueledStoveBlockEntity stoveEntity) {
				if (!stoveEntity.isStoveBlockedAbove()) {
					Optional<CampfireCookingRecipe> recipe = stoveEntity.getMatchingRecipe(new ItemStack(heldStack.getItem(), 1));
					if (recipe.isPresent()) {
						if (!level.isClientSide && stoveEntity.addItem(player.getAbilities().instabuild ? heldStack.copy() : heldStack, recipe.get())) {
							return InteractionResult.SUCCESS;
						}
						return InteractionResult.CONSUME;
					}
				}
			}
		}

		// Open GUI
		if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
			BlockEntity entity = level.getBlockEntity(pos);
			if (entity instanceof FueledStoveBlockEntity stoveEntity) {
				//LOGGER.info("Attempting to open FueledStoveMenu for block at {}", pos);
				NetworkHooks.openScreen(serverPlayer, stoveEntity, buf -> buf.writeBlockPos(pos));
				return InteractionResult.SUCCESS;
			} else {
				//LOGGER.error("Failed to open FueledStoveMenu: BlockEntity at {} is not FueledStoveBlockEntity", pos);
			}
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(LIT, false);
	}

	@Override
	public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
		if (state.getValue(LIT) && !entity.fireImmune() && entity instanceof LivingEntity) {
			entity.hurt(ModDamageTypes.getSimpleDamageSource(level, ModDamageTypes.STOVE_BURN), 1.0F);
		}
		super.stepOn(level, pos, state, entity);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity tileEntity = level.getBlockEntity(pos);
			if (tileEntity instanceof FueledStoveBlockEntity stoveEntity) {
				tileEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
					for (int i = 0; i < handler.getSlots(); i++) {
						Block.popResource(level, pos, handler.getStackInSlot(i));
					}
				});
			}
			super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(LIT, FACING);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
		if (state.getValue(LIT)) {
			double x = pos.getX() + 0.5;
			double y = pos.getY();
			double z = pos.getZ() + 0.5;
			if (rand.nextInt(10) == 0) {
				level.playLocalSound(x, y, z, ModSounds.BLOCK_STOVE_CRACKLE.get(), SoundSource.BLOCKS, 1.0F, 1.0F, false);
			}
			Direction direction = state.getValue(FACING);
			Direction.Axis axis = direction.getAxis();
			double offset = rand.nextDouble() * 0.6 - 0.3;
			double xOffset = axis == Direction.Axis.X ? direction.getStepX() * 0.52 : offset;
			double yOffset = rand.nextDouble() * 6.0 / 16.0;
			double zOffset = axis == Direction.Axis.Z ? direction.getStepZ() * 0.52 : offset;
			level.addParticle(ParticleTypes.SMOKE, x + xOffset, y + yOffset, z + zOffset, 0.0, 0.0, 0.0);
			level.addParticle(ParticleTypes.FLAME, x + xOffset, y + yOffset, z + zOffset, 0.0, 0.0, 0.0);
			//LOGGER.debug("Added block animation particles at {}", pos);
		}
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return ModBlockEntities.FUELED_STOVE.get().create(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		if (state.getValue(LIT)) {
			return createTickerHelper(blockEntityType, ModBlockEntities.FUELED_STOVE.get(), level.isClientSide
					? FueledStoveBlockEntity::animationTick
					: FueledStoveBlockEntity::cookingTick);
		}
		return null;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}
}