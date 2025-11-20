package com.ncpbails.cookscollection.integration.emi;

import com.google.common.collect.Lists;
import com.ncpbails.cookscollection.screen.OvenMenu;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import net.minecraft.world.inventory.Slot;

import java.util.List;

public class OvenRecipeHandler implements StandardRecipeHandler<OvenMenu> {

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe instanceof OvenEmiRecipe || recipe instanceof OvenShapedEmiRecipe;
    }

    @Override
    public List<Slot> getInputSources(OvenMenu handler) {
        List<Slot> list = Lists.newArrayList();
        list.add(handler.getSlot(0));
        int invStart = 3;
        for (int i = invStart; i < invStart + 36; i++) {
            list.add(handler.getSlot(i));
        }
        return list;
    }

    @Override
    public List<Slot> getCraftingSlots(OvenMenu handler) {
        List<Slot> list = Lists.newArrayList();
        for (int i = 1; i < 10; i++) {
            list.add(handler.getSlot(35+i));
        }
        return list;
    }
}
