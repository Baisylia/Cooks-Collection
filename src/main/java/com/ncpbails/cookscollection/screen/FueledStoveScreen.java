package com.ncpbails.cookscollection.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.ncpbails.cookscollection.CooksCollection;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.awt.*;

public class FueledStoveScreen extends AbstractContainerScreen<FueledStoveMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(CooksCollection.MOD_ID, "textures/gui/fueled_stove_gui.png");
    private static final Rectangle HEAT_ICON = new Rectangle(73, 7, 31, 20);

    public FueledStoveScreen(FueledStoveMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    public void init() {
        super.init();
        this.titleLabelX = 56;
        this.titleLabelY = 34;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = this.leftPos;
        int y = this.topPos;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        if (menu.isFueled()) {
            int burnProgress = menu.getBurnProgressionScaled();

            int height = (burnProgress * HEAT_ICON.height) / 31;
            int yOffset = HEAT_ICON.y + (HEAT_ICON.height - height);
            int vOffset = HEAT_ICON.height - height;
            guiGraphics.blit(TEXTURE, x + HEAT_ICON.x, y + yOffset, 176, vOffset, HEAT_ICON.width, height);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);
        if (this.isHovering(HEAT_ICON.x, HEAT_ICON.y, HEAT_ICON.width, HEAT_ICON.height, mouseX, mouseY)) {
            guiGraphics.renderTooltip(font, Component.translatable("container.cookscollection.fueled_stove." + (menu.isFueled() ? "heated" : "not_heated")), mouseX, mouseY);
        }
    }
}