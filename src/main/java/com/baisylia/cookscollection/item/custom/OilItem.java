package com.baisylia.cookscollection.item.custom;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import vectorwing.farmersdelight.common.item.DrinkableItem;

public class OilItem extends DrinkableItem {
    public OilItem(Properties properties) {
        super(properties);
    }

    public SoundEvent getDrinkingSound() {
        return SoundEvents.HONEY_DRINK;
    }
}
