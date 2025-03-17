package com.ncpbails.cookscollection.item;

import com.ncpbails.cookscollection.CooksCollection;
import com.ncpbails.cookscollection.block.ModBlocks;
import com.ncpbails.cookscollection.item.custom.BottleReturnerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.common.item.DrinkableItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CooksCollection.MOD_ID);


    public static final RegistryObject<Item> LEMON = ITEMS.register("lemon",
            () -> new Item(new Item.Properties().tab(FarmersDelight.CREATIVE_TAB).food(ModFoods.LEMON)));

    public static final RegistryObject<Item> SALT = ITEMS.register("salt",
            () -> new Item(new Item.Properties().tab(FarmersDelight.CREATIVE_TAB)));

    public static final RegistryObject<Item> SUNFLOWER_SEEDS = ITEMS.register("sunflower_seeds",
            () -> new Item(new Item.Properties().tab(FarmersDelight.CREATIVE_TAB)));

    public static final RegistryObject<Item> COOKING_OIL = ITEMS.register("cooking_oil",
            () -> new BottleReturnerItem(new Item.Properties().tab(FarmersDelight.CREATIVE_TAB)));

    public static final RegistryObject<Item> CHOCOLATE_MUFFIN = ITEMS.register("chocolate_muffin",
            () -> new Item(new Item.Properties().tab(FarmersDelight.CREATIVE_TAB).food(ModFoods.CHOCOLATE_MUFFIN)));

    public static final RegistryObject<Item> LEMON_MUFFIN = ITEMS.register("lemon_muffin",
            () -> new Item(new Item.Properties().tab(FarmersDelight.CREATIVE_TAB).food(ModFoods.LEMON_MUFFIN)));

    public static final RegistryObject<Item> FRIED_POTATO = ITEMS.register("fried_potato",
            () -> new Item(new Item.Properties().tab(FarmersDelight.CREATIVE_TAB).food(ModFoods.FRIED_POTATO)));

    public static final RegistryObject<Item> LEMONADE = ITEMS.register("lemonade",
            () -> new DrinkableItem(new Item.Properties().tab(FarmersDelight.CREATIVE_TAB).food(ModFoods.LEMONADE)));

    public static final RegistryObject<Item> RUSTIC_LOAF_SLICE = ITEMS.register("rustic_loaf_slice",
            () -> new Item(new Item.Properties().tab(FarmersDelight.CREATIVE_TAB).food(ModFoods.RUSTIC_LOAF_SLICE)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
