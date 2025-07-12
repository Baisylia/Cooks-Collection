package com.ncpbails.cookscollection.world.feature;

import com.ncpbails.cookscollection.CooksCollection;
import com.ncpbails.cookscollection.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class ModPlacedFeatures {
    public static final DeferredRegister<PlacedFeature> PLACED_FEATURES =
            DeferredRegister.create(Registries.PLACED_FEATURE, CooksCollection.MOD_ID);

    public static final RegistryObject<PlacedFeature> LEMON_CHECKED = PLACED_FEATURES.register("lemon_checked",
            () -> new PlacedFeature(ModConfiguredFeatures.LEMON_TREE.getHolder().orElseThrow(),
                    List.of(PlacementUtils.filteredByBlockSurvival(ModBlocks.LEMON_SAPLING.get()))));

    public static final RegistryObject<PlacedFeature> LEMON_PLACED = PLACED_FEATURES.register("lemon_placed",
            () -> new PlacedFeature(ModConfiguredFeatures.LEMON_SPAWN.getHolder().orElseThrow(),
                    VegetationPlacements.treePlacement(PlacementUtils.countExtra(0, 0.05f, 1))));

    public static final RegistryObject<PlacedFeature> SALT_PLACED = PLACED_FEATURES.register("salt_placed",
            () -> new PlacedFeature(ModConfiguredFeatures.SALT.getHolder().orElseThrow(),
                    List.of(CountPlacement.of(16),
                            InSquarePlacement.spread(),
                            HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.top()),
                            BiomeFilter.biome())));

    public static void register(IEventBus eventBus) {
        PLACED_FEATURES.register(eventBus);
    }
}