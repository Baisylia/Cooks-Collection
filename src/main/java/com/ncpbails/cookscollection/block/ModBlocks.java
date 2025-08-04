package com.ncpbails.cookscollection.block;

import com.ncpbails.cookscollection.CooksCollection;
import com.ncpbails.cookscollection.block.custom.*;
import com.ncpbails.cookscollection.item.ModItems;
import com.ncpbails.cookscollection.world.feature.tree.LemonTreeGrower;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.registry.ModCreativeTabs;

import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CooksCollection.MOD_ID);

    private static ToIntFunction<BlockState> litBlockEmission(int lightValue) {
        return (state) -> state.getValue(BlockStateProperties.LIT) ? lightValue : 0;
    }

    public static final RegistryObject<Block> LEMON_CRATE = registerBlock("lemon_crate",
            () -> new Block(BlockBehaviour.Properties.copy(vectorwing.farmersdelight.common.registry.ModBlocks.CARROT_CRATE.get())),
            false, 0);

    public static final RegistryObject<Block> LEMON_SAPLING = registerBlock("lemon_sapling",
            () -> new SaplingBlock(new LemonTreeGrower(), BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING)),
            true, 100);

    public static final RegistryObject<Block> LEMON_LOG = registerBlock("lemon_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG)) {
                @Override public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face) { return true; }
                @Override public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) { return 60; }
                @Override public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) { return 30; }
                @Override public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
                    if (context.getItemInHand().getItem() instanceof AxeItem) {
                        if (state.is(ModBlocks.LEMON_LOG.get())) {
                            return Blocks.STRIPPED_OAK_LOG.defaultBlockState().setValue(AXIS, state.getValue(AXIS));
                        }
                    }
                    return super.getToolModifiedState(state, context, toolAction, simulate);
                }
            }, true, 300);

    public static final RegistryObject<Block> LEMON_WOOD = registerBlock("lemon_wood",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_WOOD)) {
                @Override public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face) { return true; }
                @Override public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) { return 60; }
                @Override public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) { return 30; }
                public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
                    if (context.getItemInHand().getItem() instanceof AxeItem) {
                        if (state.is(ModBlocks.LEMON_WOOD.get())) {
                            return Blocks.STRIPPED_OAK_WOOD.defaultBlockState().setValue(AXIS, state.getValue(AXIS));
                        }
                    }
                    return super.getToolModifiedState(state, context, toolAction, simulate);
                }
            }, true, 300);

    public static final RegistryObject<Block> LEMON_LEAVES = registerBlock("lemon_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.JUNGLE_LEAVES)) {
                @Override public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face) { return true; }
                @Override public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) { return 60; }
                @Override public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) { return 30; }
            }, false, 0);

    public static final RegistryObject<Block> FRUITING_LEMON_LEAVES = registerBlock("fruiting_lemon_leaves",
            () -> new LemonFruitingLeaves(BlockBehaviour.Properties.copy(Blocks.JUNGLE_LEAVES)),
            false, 0);

    public static final RegistryObject<Block> RUSTIC_LOAF = registerBlock("rustic_loaf",
            () -> new RusticLoafBlock(BlockBehaviour.Properties.copy(vectorwing.farmersdelight.common.registry.ModBlocks.APPLE_PIE.get()).noOcclusion(),
                    () -> ModItems.RUSTIC_LOAF_SLICE.get()), false, 0);

    public static final RegistryObject<Block> SALTED_POINTED_DRIPSTONE = registerBlockWithoutBlockItem("salted_pointed_dripstone",
            () -> new SaltedPointedDripstone(BlockBehaviour.Properties.copy(Blocks.POINTED_DRIPSTONE).noOcclusion()
                    .sound(SoundType.POINTED_DRIPSTONE).randomTicks().strength(1.5F, 3.0F).dynamicShape().offsetType(BlockBehaviour.OffsetType.XZ)));

    public static final RegistryObject<Block> SALTED_DRIPSTONE_BLOCK = registerBlock("salted_dripstone_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.DRIPSTONE_BLOCK).noOcclusion()), false, 0);

    public static final RegistryObject<Block> OVEN = registerBlock("oven",
            () -> new OvenBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(5.0F, 6.0F)
                    .requiresCorrectToolForDrops().lightLevel(litBlockEmission(13))),
            false, 0);

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, boolean isFuel, Integer fuelAmount) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn, isFuel, fuelAmount);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<T> registerBlockWithoutBlockItem(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block, boolean isFuel, Integer fuelAmount) {
        if (!isFuel) {
            return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        } else {
            return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()) {
                @Override
                public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
                    return fuelAmount;
                }
            });
        }
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        eventBus.register(ModBlocks.class);
    }

    @SubscribeEvent
    public static void buildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == ModCreativeTabs.TAB_FARMERS_DELIGHT.get()) {
            event.accept(LEMON_CRATE);
            event.accept(LEMON_SAPLING);
            event.accept(LEMON_LOG);
            event.accept(LEMON_WOOD);
            event.accept(LEMON_LEAVES);
            event.accept(FRUITING_LEMON_LEAVES);
            event.accept(RUSTIC_LOAF);
            event.accept(SALTED_DRIPSTONE_BLOCK);
            event.accept(OVEN);
        }
    }
}