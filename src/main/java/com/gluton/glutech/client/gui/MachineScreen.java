package com.gluton.glutech.client.gui;

import com.gluton.glutech.container.MachineContainer;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

/**
 * @author Gluton
 */
public abstract class MachineScreen<C extends MachineContainer> extends ContainerScreen<C> {

	public MachineScreen(C screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
		
		this.guiLeft = 0;
		this.guiTop = 0;
		this.xSize = 176;
		this.ySize = 166;
	}

	@Override
	protected abstract void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY);

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
		// draws container label
		this.font.drawString(matrixStack, this.title.getString(), (this.xSize - this.font.getStringPropertyWidth(this.title)) / 2, 6.0f, 0x404040);
		// draws inventory label
		this.font.drawString(matrixStack, this.playerInventory.getDisplayName().getString(), 8.0f, 72.0f, 0x404040);
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
	}
}
