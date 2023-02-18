package com.ncpbails.cookscollection.world.feature;

import com.ncpbails.cookscollection.CooksCollection;
import com.ncpbails.cookscollection.block.ModBlocks;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

    public class ModPlacedFeatures {
    public static final DeferredRegister<PlacedFeature> PLACED_FEATURES =
            DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, CooksCollection.MOD_ID);

    public static final RegistryObject<PlacedFeature> LEMON_CHECKED = PLACED_FEATURES.register("lemon_checked",
            () -> new PlacedFeature(ModConfiguredFeatures.LEMON_TREE.getHolder().get(),
                    List.of(PlacementUtils.filteredByBlockSurvival(ModBlocks.LEMON_SAPLING.get()))));

    public static final RegistryObject<PlacedFeature> LEMON_PLACED = PLACED_FEATURES.register("lemon_placed",
            () -> new PlacedFeature(ModConfiguredFeatures.LEMON_SPAWN.getHolder().get(), VegetationPlacements.treePlacement(
                    PlacementUtils.countExtra(1, 0.1f, 1))));


    public static void register(IEventBus eventBus) {
        PLACED_FEATURES.register(eventBus);
    }
}