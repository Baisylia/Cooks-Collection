package com.ncpbails.cookscollection.item.custom;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BottleReturnerItem extends Item {
    public BottleReturnerItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        return new ItemStack(Items.GLASS_BOTTLE); // Returns an empty bottle
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true; // Ensures that the bottle is returned after crafting
    }
}
