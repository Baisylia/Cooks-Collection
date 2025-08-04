package com.ncpbails.cookscollection.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.ncpbails.cookscollection.CooksCollection;
import com.ncpbails.cookscollection.client.recipebook.OvenRecipeBookComponent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class OvenScreen extends AbstractContainerScreen<OvenMenu> implements RecipeUpdateListener {
    private static final ResourceLocation RECIPE_BUTTON_LOCATION = new ResourceLocation("textures/gui/recipe_button.png");
    private static final ResourceLocation TEXTURE = new ResourceLocation(CooksCollection.MOD_ID, "textures/gui/oven_gui.png");
    private static final Rectangle HEAT_ICON = new Rectangle(93, 55, 17, 15);
    private static final Rectangle PROGRESS_ARROW = new Rectangle(90, 35, 26, 17);

    private final OvenRecipeBookComponent recipeBookComponent = new OvenRecipeBookComponent();
    private boolean widthTooNarrow;

    public OvenScreen(OvenMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    public void init() {
        super.init();
        this.widthTooNarrow = this.width < 379;
        this.titleLabelX = 28;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = this.imageHeight - 94;
        this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.menu);
        // Set initial GUI position (centered by default, shift right only if recipe book is visible)
        this.leftPos = (this.width - this.imageWidth) / 2 + (this.recipeBookComponent.isVisible() && !this.widthTooNarrow ? 77 : 0);
        // Place recipe book button just left of the 3x3 grid, centered vertically (y=35)
        this.addRenderableWidget(new ImageButton(this.leftPos + 5, this.topPos + 35, 20, 18, 0, 0, 19, RECIPE_BUTTON_LOCATION, (button) -> {
            this.recipeBookComponent.toggleVisibility();
            this.leftPos = (this.width - this.imageWidth) / 2 + (this.recipeBookComponent.isVisible() && !this.widthTooNarrow ? 77 : 0);
            ((ImageButton) button).setPosition(this.leftPos + 5, this.topPos + 35);
        }));
        this.addWidget(this.recipeBookComponent);
        this.setInitialFocus(this.recipeBookComponent);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        this.recipeBookComponent.tick();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(guiGraphics);
        if (this.recipeBookComponent.isVisible() && this.widthTooNarrow) {
            this.renderBg(guiGraphics, delta, mouseX, mouseY);
            this.recipeBookComponent.render(guiGraphics, mouseX, mouseY, delta);
        } else {
            this.recipeBookComponent.render(guiGraphics, mouseX, mouseY, delta);
            super.render(guiGraphics, mouseX, mouseY, delta);
            this.recipeBookComponent.renderGhostRecipe(guiGraphics, this.leftPos, this.topPos, true, delta);
        }
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        this.recipeBookComponent.renderTooltip(guiGraphics, this.leftPos, this.topPos, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = this.leftPos;
        int y = this.topPos;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        if (menu.isCrafting()) {
            guiGraphics.blit(TEXTURE, x + PROGRESS_ARROW.x, y + PROGRESS_ARROW.y, 176, 14, menu.getCookProgressionScaled(), PROGRESS_ARROW.height);
        }
        if (menu.isFueled()) {
            guiGraphics.blit(TEXTURE, x + HEAT_ICON.x, y + HEAT_ICON.y, 176, 32, HEAT_ICON.width, HEAT_ICON.height);
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);
        if (this.isHovering(HEAT_ICON.x, HEAT_ICON.y, HEAT_ICON.width, HEAT_ICON.height, mouseX, mouseY)) {
            List<Component> tooltip = new ArrayList<>();
            String key = "container.cookscollection.oven." + (this.menu.isFueled() ? "heated" : "not_heated");
            tooltip.add(Component.translatable(key));
            guiGraphics.renderTooltip(font, tooltip, Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    protected boolean isHovering(int x, int y, int width, int height, double mouseX, double mouseY) {
        return (!this.widthTooNarrow || !this.recipeBookComponent.isVisible()) && super.isHovering(x, y, width, height, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int buttonId) {
        if (this.recipeBookComponent.mouseClicked(mouseX, mouseY, buttonId)) {
            this.setFocused(this.recipeBookComponent);
            return true;
        }
        return this.widthTooNarrow && this.recipeBookComponent.isVisible() || super.mouseClicked(mouseX, mouseY, buttonId);
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int x, int y, int buttonIdx) {
        boolean flag = mouseX < (double) x || mouseY < (double) y || mouseX >= (double) (x + this.imageWidth) || mouseY >= (double) (y + this.imageHeight);
        return flag && this.recipeBookComponent.hasClickedOutside(mouseX, mouseY, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, buttonIdx);
    }

    @Override
    protected void slotClicked(Slot slot, int mouseX, int mouseY, ClickType clickType) {
        super.slotClicked(slot, mouseX, mouseY, clickType);
        this.recipeBookComponent.slotClicked(slot);
    }

    @Override
    public void recipesUpdated() {
        this.recipeBookComponent.recipesUpdated();
    }

    @Override
    public OvenRecipeBookComponent getRecipeBookComponent() {
        return this.recipeBookComponent;
    }
}