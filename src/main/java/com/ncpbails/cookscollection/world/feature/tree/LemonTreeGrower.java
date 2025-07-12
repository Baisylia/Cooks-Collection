package com.ncpbails.cookscollection.world.feature.tree;

import com.ncpbails.cookscollection.world.feature.ModConfiguredFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import javax.annotation.Nullable;

public class LemonTreeGrower extends AbstractTreeGrower {
    @Nullable
    @Override
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource p_222910_, boolean pLargeHive) {
        return (ResourceKey<ConfiguredFeature<?, ?>>) ModConfiguredFeatures.LEMON_TREE.getHolder().orElse(null);
    }
}