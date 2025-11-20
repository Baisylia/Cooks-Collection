package com.baisylia.cookscollection.block.custom;



import com.baisylia.cookscollection.client.ModSounds;
import com.baisylia.cookscollection.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.Supplier;

public class FruitingLeaves extends LeavesBlock implements BonemealableBlock {
    public static final int MAX_AGE = 4;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_4;
    public Supplier<Item> FRUIT;

    public FruitingLeaves(BlockBehaviour.Properties properties, Supplier<Item> itemSupplier) {
        super(properties);
        this.FRUIT = itemSupplier;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AGE, 0)
                .setValue(DISTANCE, 7)
                .setValue(PERSISTENT, Boolean.FALSE)
                .setValue(WATERLOGGED, Boolean.FALSE));

    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter p_57256_, BlockPos p_57257_, BlockState p_57258_) {
        return new ItemStack(FRUIT.get());
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(AGE) < MAX_AGE || state.getValue(DISTANCE) == 7 && !state.getValue(PERSISTENT);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
        if (this.decaying(state)) {
            dropResources(state, world, pos);
            world.removeBlock(pos, false);
        }
        else {
            int age = state.getValue(AGE);
            if (age < MAX_AGE) {
                BlockState blockstate = state.setValue(AGE, age + 1);
                world.setBlock(pos, blockstate, 2);
                world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(blockstate));
                net.minecraftforge.common.ForgeHooks.onCropsGrowPost(world, pos, state);
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        int i = state.getValue(AGE);
        boolean flag = i == MAX_AGE;
        if (!flag && player.getItemInHand(hand).is(Items.BONE_MEAL)) {
            return InteractionResult.PASS;
        } else if (i > 1 && flag) {
            int j = 1 + world.random.nextInt(2);
            popResourceFromFace(world, pos, result.getDirection(), new ItemStack(ModItems.LEMON.get(), j + 1));
            world.playSound(player, pos, ModSounds.LEAVES_PICKED.get(), SoundSource.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
            BlockState blockstate = state.setValue(AGE, 0);
            world.setBlock(pos, blockstate, 2);
            world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, blockstate));
            return InteractionResult.sidedSuccess(world.isClientSide);
        } else {
            return super.use(state, world, pos, player, hand, result);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> state) {
        state.add(AGE);
        state.add(DISTANCE, PERSISTENT, WATERLOGGED);
    }

    @Override
    public boolean isValidBonemealTarget(BlockGetter blockGetter, BlockPos pos, BlockState state, boolean b) {
        return state.getValue(AGE) < MAX_AGE;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource randomSource, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource randomSource, BlockPos pos, BlockState state) {
        int i = Math.min(MAX_AGE, state.getValue(AGE) + 1);
        serverLevel.setBlock(pos, state.setValue(AGE, i), 2);
    }
}
