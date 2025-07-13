package com.ncpbails.cookscollection;

import com.mojang.logging.LogUtils;
import com.ncpbails.cookscollection.block.ModBlocks;
import com.ncpbails.cookscollection.block.entity.ModBlockEntities;
import com.ncpbails.cookscollection.client.ModSounds;
import com.ncpbails.cookscollection.client.recipebook.RecipeCategories;
import com.ncpbails.cookscollection.item.ModItems;
import com.ncpbails.cookscollection.recipe.ModRecipeTypes;
import com.ncpbails.cookscollection.screen.ModMenuTypes;
import com.ncpbails.cookscollection.screen.OvenScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import com.ncpbails.cookscollection.item.ModCreativeTabs;
import org.slf4j.Logger;

@Mod(CooksCollection.MOD_ID)
public class CooksCollection {
    public static final String MOD_ID = "cookscollection";
    private static final Logger LOGGER = LogUtils.getLogger();

    public CooksCollection() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(eventBus);
        ModBlocks.register(eventBus);
        ModBlockEntities.register(eventBus);
        ModMenuTypes.register(eventBus);
        ModRecipeTypes.register(eventBus);
        ModSounds.register(eventBus);
        ModCreativeTabs.register(eventBus);

        eventBus.addListener(this::clientSetup);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            LOGGER.info("Registering client components for {}", MOD_ID);
            MenuScreens.register(ModMenuTypes.OVEN_MENU.get(), OvenScreen::new);
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.SALTED_POINTED_DRIPSTONE.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.LEMON_SAPLING.get(), RenderType.cutoutMipped());
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
}