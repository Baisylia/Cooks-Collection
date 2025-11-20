package com.baisylia.cookscollection.block.custom;


import com.mojang.datafixers.util.Pair;
import com.baisylia.cookscollection.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import vectorwing.farmersdelight.common.block.FeastBlock;
import vectorwing.farmersdelight.common.block.PieBlock;
import vectorwing.farmersdelight.common.tag.ModTags;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class RusticLoafBlock extends Block {
    public static final DirectionProperty FACING;
    public static final IntegerProperty BITES;
    protected static final VoxelShape SHAPE;
    public final Supplier<Item> pieSlice;

    public RusticLoafBlock(BlockBehaviour.Properties properties, Supplier<Item> pieSlice) {
        super(properties);
        this.pieSlice = pieSlice;
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(BITES, 0));
    }

    public ItemStack getPieSliceItem() {
        return new ItemStack((ItemLike)this.pieSlice.get());
    }

    public int getMaxBites() {
        return 4;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        VoxelShape shape = SHAPE;

        switch (facing) {
            case EAST:
                shape = Block.box(4F, 0F, 2F, 12F, 6F, 14F);
                break;
            case SOUTH:
                shape = Block.box(2F, 0F, 4F, 14F, 6F, 12F);
                break;
            case WEST:
                shape = Block.box(4F, 0F, 2F, 12F, 6F, 14F);
                break;
            default:
                break;
        }

        return shape;
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldStack = player.getItemInHand(hand);
        if (level.isClientSide) {
            if (heldStack.is(ModTags.KNIVES)) {
                return this.cutSlice(level, pos, state);
            }

            if (this.consumeBite(level, pos, state, player) == InteractionResult.SUCCESS) {
                return InteractionResult.SUCCESS;
            }

            if (heldStack.isEmpty()) {
                return InteractionResult.CONSUME;
            }
        }

        return heldStack.is(ModTags.KNIVES) ? this.cutSlice(level, pos, state) : this.consumeBite(level, pos, state, player);
    }

    protected InteractionResult consumeBite(Level level, BlockPos pos, BlockState state, Player playerIn) {
        if (!playerIn.canEat(false)) {
            return InteractionResult.PASS;
        } else {
            ItemStack sliceStack = this.getPieSliceItem();
            FoodProperties sliceFood = sliceStack.getItem().getFoodProperties();
            playerIn.getFoodData().eat(sliceStack.getItem(), sliceStack);
            if (this.getPieSliceItem().getItem().isEdible() && sliceFood != null) {
                for(Pair<MobEffectInstance, Float> pair : sliceFood.getEffects()) {
                    if (!level.isClientSide && pair.getFirst() != null && level.random.nextFloat() < (Float)pair.getSecond()) {
                        playerIn.addEffect(new MobEffectInstance((MobEffectInstance)pair.getFirst()));
                    }
                }
            }

            int bites = (Integer)state.getValue(BITES);
            if (bites < this.getMaxBites() - 1) {
                level.setBlock(pos, (BlockState)state.setValue(BITES, bites + 1), 3);
            } else {
                level.removeBlock(pos, false);
            }

            level.playSound((Player)null, pos, SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 0.8F, 0.8F);
            return InteractionResult.SUCCESS;
        }
    }

    protected InteractionResult cutSlice(Level level, BlockPos pos, BlockState state) {
        int bites = (Integer)state.getValue(BITES);
        if (bites < this.getMaxBites() - 1) {
            level.setBlock(pos, (BlockState)state.setValue(BITES, bites + 1), 3);
        } else {
            level.removeBlock(pos, false);
        }

        Containers.dropItemStack(level, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), this.getPieSliceItem());
        level.playSound((Player)null, pos, SoundEvents.WOOL_BREAK, SoundSource.PLAYERS, 0.8F, 0.8F);
        return InteractionResult.SUCCESS;
    }

    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        return facing == Direction.DOWN && !stateIn.canSurvive(level, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, level, currentPos, facingPos);
    }

    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).getMaterial().isSolid();
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, BITES});
    }

    public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos) {
        return this.getMaxBites() - (Integer)blockState.getValue(BITES);
    }

    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
        return false;
    }

    static {
        FACING = BlockStateProperties.HORIZONTAL_FACING;
        BITES = IntegerProperty.create("bites", 0, 3);
        SHAPE = Block.box(2F, 0F, 4F, 14F, 6F, 12F);
    }
}
