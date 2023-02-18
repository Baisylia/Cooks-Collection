package com.ncpbails.cookscollection.block.custom;


import com.ncpbails.cookscollection.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import vectorwing.farmersdelight.common.block.FeastBlock;
import vectorwing.farmersdelight.common.block.PieBlock;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class RusticLoafBlock extends FeastBlock {
    protected static final VoxelShape PLATE_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0);
    protected static final VoxelShape ROAST_SHAPE;

    public RusticLoafBlock(Properties properties, Supplier<Item> servingItem, boolean hasLeftovers) {
        super(properties, servingItem, hasLeftovers);
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return (Integer)state.getValue(SERVINGS) == 0 ? PLATE_SHAPE : ROAST_SHAPE;
    }

    static {
        ROAST_SHAPE = Shapes.joinUnoptimized(PLATE_SHAPE, Block.box(4.0, 2.0, 4.0, 12.0, 9.0, 12.0), BooleanOp.OR);
    }
}