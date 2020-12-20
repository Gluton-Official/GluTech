package com.gluton.glutech.client.gui;

import com.gluton.glutech.GluTech;
import com.gluton.glutech.container.CrusherContainer;
import com.gluton.glutech.tileentity.CrusherTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

/**
 * @author Gluton
 */
public class CrusherScreen extends MachineScreen<CrusherContainer> {

	private static final ResourceLocation TEXTURE = new ResourceLocation(GluTech.MOD_ID, "textures/gui/crusher.png");
	
	public CrusherScreen(CrusherContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		// draws inventory gui
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		this.blit(matrixStack, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
		// draws progress bar
		this.blit(matrixStack, this.guiLeft + 79, this.guiTop + 35, 176, 0, this.container.getSmeltProgressionScaled(), 16);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
		// draws container label
		this.font.drawString(matrixStack, this.title.getString(), (this.xSize - this.font.getStringPropertyWidth(this.title)) / 2, 6.0f, 0x404040);
		// draws energy amount
		CrusherTileEntity tileEntity = this.container.getTileEntity();
		StringTextComponent energyAmount = new StringTextComponent(tileEntity.getEnergyStored() + "/" + tileEntity.getMaxEnergyStored());
		this.font.drawString(matrixStack, energyAmount.getString(), (this.xSize - this.font.getStringPropertyWidth(energyAmount)) / 2, 18.0f, 0x404040);
		// draws inventory label
		this.font.drawString(matrixStack, this.playerInventory.getDisplayName().getString(), 8.0f, 72.0f, 0x404040);
	}
}
