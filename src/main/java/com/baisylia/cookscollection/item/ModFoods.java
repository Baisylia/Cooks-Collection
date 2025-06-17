package com.baisylia.cookscollection.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import vectorwing.farmersdelight.common.FoodValues;
import vectorwing.farmersdelight.common.registry.ModEffects;

public class ModFoods {
    public static final FoodProperties LEMON = (new FoodProperties.Builder()).nutrition(1).saturationMod(0.3F).build();
    public static final FoodProperties LEMONADE = (new FoodProperties.Builder()).nutrition(0).saturationMod(0.8F)
            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200, 0), 1.0F).build();
    public static final FoodProperties COOKING_OIL = (new FoodProperties.Builder()).nutrition(0).saturationMod(0.0F).build();
    public static final FoodProperties LEMON_MUFFIN = (new FoodProperties.Builder()).nutrition(5).saturationMod(0.6F).build();
    public static final FoodProperties CHOCOLATE_MUFFIN = (new FoodProperties.Builder()).nutrition(5).saturationMod(0.6F).build();
    public static final FoodProperties FRIED_POTATO = (new FoodProperties.Builder()).nutrition(1).saturationMod(0.3F).build();
    public static final FoodProperties RUSTIC_LOAF_SLICE = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.3F).build();
    public static final FoodProperties FISH_AND_CHIPS = (new FoodProperties.Builder()).nutrition(16).saturationMod(1.2F)
            .effect(() -> new MobEffectInstance(ModEffects.NOURISHMENT.get(), FoodValues.LONG_DURATION, 0), 1.0F).build();

}
