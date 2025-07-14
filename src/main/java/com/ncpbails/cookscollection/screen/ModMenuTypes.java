package com.ncpbails.cookscollection.screen;

import com.ncpbails.cookscollection.CooksCollection;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, CooksCollection.MOD_ID);

    public static final RegistryObject<MenuType<OvenMenu>> OVEN_MENU =
            registerMenuType(OvenMenu::new, "oven_menu");
    public static final RegistryObject<MenuType<FueledStoveMenu>> FUELED_STOVE_MENU =
            registerMenuType(FueledStoveMenu::new, "fueled_stove_menu");

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(IContainerFactory<T> factory, String name) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}