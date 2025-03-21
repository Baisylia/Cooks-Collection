package com.ncpbails.cookscollection.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ncpbails.cookscollection.CooksCollection;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import vectorwing.farmersdelight.common.utility.TextUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class OvenScreen extends AbstractContainerScreen<OvenMenu> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(CooksCollection.MOD_ID, "textures/gui/oven_gui.png");

    public OvenScreen(OvenMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.blit(pPoseStack, x, y, 0, 0, imageWidth, imageHeight);

        if(menu.isCrafting()) {
            blit(pPoseStack, x + 90, y + 35, 176, 14,  menu.getScaledProgress(), 17);
        }
        if(menu.isFueled()) {
            blit(pPoseStack, x + 93, y + 55, 176, 32, 17, 15);
        }

    }

    @Override
    public void render(PoseStack pPoseStack, int mouseX, int mouseY, float delta) {
        renderBackground(pPoseStack);
        super.render(pPoseStack, mouseX, mouseY, delta);
        renderTooltip(pPoseStack, mouseX, mouseY);
        if (this.isHovering(93, 55, 17, 15, mouseX, mouseY)) {
            List<Component> tooltip = new ArrayList<>();
            String key = "container.cookscollection.oven." + (this.menu.isFueled() ? "heated" : "not_heated");
            tooltip.add(Component.translatable(key));
            this.renderComponentTooltip(pPoseStack, tooltip, mouseX, mouseY);
        }

    }


}