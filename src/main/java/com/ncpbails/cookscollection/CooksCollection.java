package com.ncpbails.cookscollection;

import com.mojang.logging.LogUtils;
import com.ncpbails.cookscollection.block.ModBlocks;
import com.ncpbails.cookscollection.block.entity.ModBlockEntities;
import com.ncpbails.cookscollection.client.ModSounds;
import com.ncpbails.cookscollection.client.recipebook.OvenRecipeBookComponent;
import com.ncpbails.cookscollection.client.recipebook.RecipeCategories;
import com.ncpbails.cookscollection.data.ModAdvancementProvider;
import com.ncpbails.cookscollection.data.ModRecipeProvider;
import com.ncpbails.cookscollection.data.RecipeAdvancementProvider;
import com.ncpbails.cookscollection.item.ModItems;
import com.ncpbails.cookscollection.recipe.ModRecipeTypes;
import com.ncpbails.cookscollection.screen.ModMenuTypes;
import com.ncpbails.cookscollection.screen.OvenScreen;
import com.ncpbails.cookscollection.screen.FueledStoveScreen;
import com.ncpbails.cookscollection.item.ModCreativeTabs;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(CooksCollection.MOD_ID)
public class CooksCollection {
    public static final String MOD_ID = "cookscollection";
    public static final RecipeBookType RECIPE_TYPE_OVEN = RecipeBookType.create("BAKING");
    private static final Logger LOGGER = LogUtils.getLogger();

    public CooksCollection() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModRecipeTypes.register(modEventBus);
        ModSounds.register(modEventBus);
        ModCreativeTabs.register(modEventBus);

        modEventBus.addListener(this::onDataGeneration);
        modEventBus.addListener(this::clientSetup);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            LOGGER.info("Registering client components for {}", MOD_ID);
            MenuScreens.register(ModMenuTypes.OVEN_MENU.get(), OvenScreen::new);
            MenuScreens.register(ModMenuTypes.FUELED_STOVE_MENU.get(), FueledStoveScreen::new);
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.SALTED_POINTED_DRIPSTONE.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.LEMON_SAPLING.get(), RenderType.cutoutMipped());

            MinecraftForge.EVENT_BUS.register(OvenRecipeBookComponent.class);
        });
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Moved to clientSetup method above
        }

        @SubscribeEvent
        public static void registerRecipeBookCategories(RegisterRecipeBookCategoriesEvent event) {
            LOGGER.info("Registering recipe book categories for {}", MOD_ID);
            RecipeCategories.init(event);
        }
    }

    @SubscribeEvent
    public void onDataGeneration(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        generator.addProvider(event.includeServer(), new ModAdvancementProvider(
                generator.getPackOutput(),
                event.getLookupProvider(),
                existingFileHelper
        ));
        generator.addProvider(event.includeServer(), new ModRecipeProvider(
                generator.getPackOutput(),
                existingFileHelper
        ));
        generator.addProvider(event.includeServer(), new RecipeAdvancementProvider(
                generator.getPackOutput(),
                event.getLookupProvider(),
                existingFileHelper
        ));
    }
}