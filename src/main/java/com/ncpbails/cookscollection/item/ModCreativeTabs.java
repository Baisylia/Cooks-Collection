package com.ncpbails.cookscollection.item;

import com.ncpbails.cookscollection.CooksCollection;
import com.ncpbails.cookscollection.block.ModBlocks;
import com.ncpbails.cookscollection.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CooksCollection.MOD_ID);

    public static final RegistryObject<CreativeModeTab> COOKS_COLLECTION_TAB = CREATIVE_MODE_TABS.register("cooks_collection_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.LEMON.get()))
                    .title(Component.translatable("itemGroup.cookscollection"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.LEMON.get());
                        output.accept(ModItems.SALT.get());
                        //output.accept(ModItems.SUNFLOWER_SEEDS.get());
                        output.accept(ModItems.COOKING_OIL.get());
                        output.accept(ModItems.CHOCOLATE_MUFFIN.get());
                        output.accept(ModItems.LEMON_MUFFIN.get());
                        output.accept(ModItems.FRIED_POTATO.get());
                        output.accept(ModItems.LEMONADE.get());
                        output.accept(ModItems.RUSTIC_LOAF_SLICE.get());
                        output.accept(ModItems.FISH_AND_CHIPS.get());
                        output.accept(ModBlocks.OVEN.get());
                        output.accept(ModBlocks.LEMON_SAPLING.get());
                        output.accept(ModBlocks.LEMON_LOG.get());
                        output.accept(ModBlocks.LEMON_LEAVES.get());
                        output.accept(ModBlocks.FRUITING_LEMON_LEAVES.get());
                        output.accept(ModBlocks.SALTED_DRIPSTONE_BLOCK.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}