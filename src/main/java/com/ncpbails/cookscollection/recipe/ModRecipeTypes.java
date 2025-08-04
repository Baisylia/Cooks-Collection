package com.ncpbails.cookscollection.recipe;

import com.ncpbails.cookscollection.CooksCollection;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeTypes {
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, CooksCollection.MOD_ID);
    private static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, CooksCollection.MOD_ID);

    public static final RegistryObject<RecipeType<OvenRecipe>> BAKING_TYPE =
            TYPES.register("baking", () -> OvenRecipe.Type.INSTANCE);
    public static final RegistryObject<RecipeSerializer<OvenRecipe>> BAKING_SERIALIZER =
            SERIALIZERS.register("baking", () -> OvenRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}