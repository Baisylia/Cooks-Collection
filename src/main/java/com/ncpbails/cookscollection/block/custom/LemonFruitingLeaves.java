package com.ncpbails.cookscollection.block.custom;

import com.ncpbails.cookscollection.item.ModItems;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class LemonFruitingLeaves extends FruitingLeaves {
    public LemonFruitingLeaves(BlockBehaviour.Properties properties) {
        super(properties, () -> ModItems.LEMON.get());
    }
}