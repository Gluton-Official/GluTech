package com.gluton.glutech.client.gui;

import com.gluton.glutech.container.MachineContainer;
import com.gluton.glutech.tileentity.MachineTileEntity;
import com.gluton.glutech.util.EnergyFormat;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

/**
 * @author Gluton
 */
public abstract class MachineScreen<C extends MachineContainer> extends ContainerScreen<C> {

	public MachineScreen(C screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
		
		this.guiLeft = 0;
		this.guiTop = 0;
		this.xSize = 175;
		this.ySize = 165;
	}

	/**
	 * Must be called via {@code super} when overriden if you want a background texture and energy bar
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		// draws inventory gui
		this.minecraft.getTextureManager().bindTexture(getBackgroundTexture());
		this.blit(matrixStack, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
		// draws energy bar
		int energyBarHeight = this.container.getEnergyBarScaled();
		this.blit(matrixStack, this.guiLeft + 9, this.guiTop + 66 - energyBarHeight, 0, this.ySize + 49 - energyBarHeight, 14, energyBarHeight);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
		// draws container label
		this.font.drawString(matrixStack, this.title.getString(), (this.xSize - this.font.getStringPropertyWidth(this.title)) / 2, 6.0f, 0x404040);
		// draws inventory label
		this.font.drawString(matrixStack, this.playerInventory.getDisplayName().getString(), 8.0f, 72.0f, 0x404040);
		// draws energy amount
		int relativeMouseX = mouseX - this.guiLeft;
		int relativeMouseY = mouseY - this.guiTop;
		if (relativeMouseX >= 9 && relativeMouseX < 9 + 14
				&& relativeMouseY >= 18 && relativeMouseY < 18 + 48) {
			MachineTileEntity tileEntity = this.container.getTileEntity();
			StringTextComponent energyAmount = new StringTextComponent(EnergyFormat.getEnergyLabel(
					TextFormatting.GREEN + "Energy", tileEntity.getEnergyStored(), tileEntity.getMaxEnergyStored(), EnergyFormat.COMPACT));
			this.renderTooltip(matrixStack, energyAmount, mouseX - this.guiLeft, mouseY - this.guiTop);
		}
		
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
	}
	
	protected abstract ResourceLocation getBackgroundTexture();
}
