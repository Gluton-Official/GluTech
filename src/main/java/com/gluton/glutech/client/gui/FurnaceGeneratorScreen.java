package com.gluton.glutech.client.gui;

import com.gluton.glutech.GluTech;
import com.gluton.glutech.container.FurnaceGeneratorContainer;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

/**
 * @author Gluton
 */
public class FurnaceGeneratorScreen extends MachineScreen<FurnaceGeneratorContainer> {

	private static final ResourceLocation TEXTURE = new ResourceLocation(GluTech.MOD_ID, "textures/gui/furnace_generator.png");
	
	public FurnaceGeneratorScreen(FurnaceGeneratorContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
		// draws progress bar
		int progressBarWidth = this.container.getProgessBarScaled();
		this.blit(matrixStack, this.guiLeft + 80, this.guiTop + 28 + 13 - progressBarWidth,
				176, 13 - progressBarWidth, 14, progressBarWidth);
	}
	
	@Override
	protected ResourceLocation getBackgroundTexture() {
		return TEXTURE;
	}
}
