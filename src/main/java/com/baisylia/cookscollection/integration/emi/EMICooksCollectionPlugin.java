package com.baisylia.cookscollection.integration.emi;

import com.mojang.blaze3d.systems.RenderSystem;
import com.baisylia.cookscollection.CooksCollection;
import com.baisylia.cookscollection.block.ModBlocks;
import com.baisylia.cookscollection.recipe.OvenRecipe;
import com.baisylia.cookscollection.recipe.OvenShapedRecipe;
import com.baisylia.cookscollection.screen.ModMenuTypes;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;

@EmiEntrypoint
public class EMICooksCollectionPlugin implements EmiPlugin {

    static final ResourceLocation TEXTURE = new ResourceLocation(CooksCollection.MOD_ID, "textures/gui/oven_gui_jei.png");

    public static final EmiRecipeCategory SHAPELESS_BAKING = new EmiRecipeCategory(new ResourceLocation(CooksCollection.MOD_ID, "shapeless_baking"), EmiStack.of(ModBlocks.OVEN.get()), simplifiedRenderer(0, 0));
    public static final EmiRecipeCategory SHAPED_BAKING = new EmiRecipeCategory(new ResourceLocation(CooksCollection.MOD_ID, "shaped_baking"), EmiStack.of(ModBlocks.OVEN.get()), simplifiedRenderer(0, 0));

    private static EmiRenderable simplifiedRenderer(int u, int v) {
        return (draw, x, y, delta) -> {
            RenderSystem.setShaderTexture(0, TEXTURE);
            GuiComponent.blit(draw, x, y, u, v, 124, 58, 124, 58);
        };
    }

    @Override
    public void register(EmiRegistry registry) {
        var forge = EmiStack.of(ModBlocks.OVEN.get());
        registry.addCategory(SHAPELESS_BAKING);
        registry.addWorkstation(SHAPELESS_BAKING, forge);
        registry.addCategory(SHAPED_BAKING);
        registry.addWorkstation(SHAPED_BAKING, forge);
        for (OvenRecipe recipe : registry.getRecipeManager().getAllRecipesFor(OvenRecipe.Type.INSTANCE)) {
            registry.addRecipe(new OvenEmiRecipe(recipe));
        }
        for (var recipe : registry.getRecipeManager().getAllRecipesFor(OvenShapedRecipe.Type.INSTANCE)) {
            registry.addRecipe(new OvenShapedEmiRecipe(recipe));
        }
        registry.addRecipeHandler(ModMenuTypes.OVEN_MENU.get(), new OvenRecipeHandler());
    }
}
