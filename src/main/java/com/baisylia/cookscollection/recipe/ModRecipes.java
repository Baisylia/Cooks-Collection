package com.baisylia.cookscollection.recipe;

import com.baisylia.cookscollection.CooksCollection;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, CooksCollection.MOD_ID);


    public static final RegistryObject<RecipeSerializer<OvenRecipe>> BAKING_SERIALIZER =
            SERIALIZERS.register("baking", () -> OvenRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<OvenShapedRecipe>> BAKING_SHAPED_SERIALIZER =
            SERIALIZERS.register("baking_shaped", () -> OvenShapedRecipe.Serializer.INSTANCE);


    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
