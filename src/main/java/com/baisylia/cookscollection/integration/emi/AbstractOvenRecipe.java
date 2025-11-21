package com.baisylia.cookscollection.integration.emi;

import com.baisylia.cookscollection.CooksCollection;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public abstract class AbstractOvenRecipe implements EmiRecipe {
    public final static ResourceLocation TEXTURE = new ResourceLocation(CooksCollection.MOD_ID, "textures/gui/oven_gui_jei.png");
    final List<EmiIngredient> ingredients;
    final int cookTime;
    final EmiStack result;
    final ResourceLocation id;


    public AbstractOvenRecipe(ResourceLocation id, List<EmiIngredient> ingredients, ItemStack resultItem, int cookTime) {
        this.id = id;
        this.ingredients =ingredients;
        this.result = EmiStack.of(resultItem);
        this.cookTime = cookTime;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return this.id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return ingredients;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(result);
    }

    @Override
    public int getDisplayWidth() {
        return 124;
    }

    @Override
    public int getDisplayHeight() {
        return 58;
    }

    protected static void drawCookTime(int cookTime, WidgetHolder builder, int y, int width) {
        if (cookTime > 0) {
            int cookTimeSeconds = cookTime / 20;
            Component timeString = Component.translatable("emi.cooking.time", cookTimeSeconds);
            builder.addFillingArrow(63, 20, cookTime*100).tooltipText(Collections.singletonList(timeString));
        }
    }
}
