package com.ncpbails.cookscollection;

import com.mojang.logging.LogUtils;
import com.ncpbails.cookscollection.block.ModBlocks;
import com.ncpbails.cookscollection.item.ModItems;
import com.ncpbails.cookscollection.world.feature.ModConfiguredFeatures;
import com.ncpbails.cookscollection.world.feature.ModPlacedFeatures;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CooksCollection.MOD_ID)
public class CooksCollection
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "cookscollection";
    private static final Logger LOGGER = LogUtils.getLogger();

    public CooksCollection()
    {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(eventBus);
        ModBlocks.register(eventBus);
        ModConfiguredFeatures.register(eventBus);
        ModPlacedFeatures.register(eventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.SALTED_POINTED_DRIPSTONE.get(), RenderType.cutoutMipped());
        }
    }
}
