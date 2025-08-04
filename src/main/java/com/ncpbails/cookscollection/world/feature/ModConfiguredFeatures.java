package com.ncpbails.cookscollection.world.feature;

import com.ncpbails.cookscollection.CooksCollection;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> LEMON_TREE =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(CooksCollection.MOD_ID, "lemon"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> SALT =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(CooksCollection.MOD_ID, "salt"));

    public static void register() {
        // No registration needed, as features are defined in JSON
    }
}