package com.ncpbails.cookscollection.world.feature;

import com.ncpbails.cookscollection.CooksCollection;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> LEMON_CHECKED =
            ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(CooksCollection.MOD_ID, "lemon_checked"));

    public static final ResourceKey<PlacedFeature> LEMON_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(CooksCollection.MOD_ID, "lemon_placed"));

    public static final ResourceKey<PlacedFeature> SALT_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(CooksCollection.MOD_ID, "salt_placed"));

    public static void register() {
        // No registration needed, as features are defined in JSON
    }
}