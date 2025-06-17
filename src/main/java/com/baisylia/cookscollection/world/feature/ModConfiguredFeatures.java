package com.baisylia.cookscollection.world.feature;

import com.google.common.base.Suppliers;
import com.baisylia.cookscollection.CooksCollection;
import com.baisylia.cookscollection.block.ModBlocks;
import com.baisylia.cookscollection.block.custom.FruitingLeaves;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Supplier;

public class ModConfiguredFeatures {
    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES =
            DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, CooksCollection.MOD_ID);

    public static final RegistryObject<ConfiguredFeature<?, ?>> LEMON_TREE =
            CONFIGURED_FEATURES.register("lemon", () ->
                    new ConfiguredFeature<>(Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                            BlockStateProvider.simple(ModBlocks.LEMON_LOG.get()),
                            //new LemonTrunkPlacer(5, 2, 2),
                            new StraightTrunkPlacer(5, 2, 2),
                            new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder().add(ModBlocks.LEMON_LEAVES.get().defaultBlockState(),
                                    8).add(ModBlocks.FRUITING_LEMON_LEAVES.get().defaultBlockState().setValue(FruitingLeaves.AGE, 4), 1)),
                            //new MegaJungleFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0), 2),
                            new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
                            //new DarkOakFoliagePlacer(ConstantInt.of(0), ConstantInt.of(2)),
                            //new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 4),
                            //new AcaciaFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0)),
                            new TwoLayersFeatureSize(1, 0, 1)).ignoreVines().build()));
    public static final RegistryObject<ConfiguredFeature<?, ?>> LEMON_SPAWN =
            CONFIGURED_FEATURES.register("lemon_spawn", () -> new ConfiguredFeature<>(Feature.RANDOM_SELECTOR,
                    new RandomFeatureConfiguration(List.of(new WeightedPlacedFeature(
                            ModPlacedFeatures.LEMON_CHECKED.getHolder().get(),
                            0.5F)), ModPlacedFeatures.LEMON_CHECKED.getHolder().get())));


    public static final Supplier<List<OreConfiguration.TargetBlockState>> OVERWORLD_SALT = Suppliers.memoize(() -> List.of(
            OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, ModBlocks.SALTED_DRIPSTONE_BLOCK.get().defaultBlockState()),
            OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, ModBlocks.SALTED_DRIPSTONE_BLOCK.get().defaultBlockState())));

    public static final RegistryObject<ConfiguredFeature<?, ?>> SALT = CONFIGURED_FEATURES.register("salt",
            () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(OVERWORLD_SALT.get(), 30)));


    public static void register(IEventBus eventBus) {
        CONFIGURED_FEATURES.register(eventBus);
    }
}
