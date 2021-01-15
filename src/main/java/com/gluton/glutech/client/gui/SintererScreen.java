package com.gluton.glutech.client.gui;

import com.gluton.glutech.GluTech;
import com.gluton.glutech.container.SintererContainer;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

/**
 * @author Gluton
 */
public class SintererScreen extends MachineScreen<SintererContainer> {
	
	private static final ResourceLocation TEXTURE = new ResourceLocation(GluTech.MOD_ID, "textures/gui/sinterer.png");

	public SintererScreen(SintererContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
		// draws progress bar
		this.blit(matrixStack, this.guiLeft + 79, this.guiTop + 35, 176, 0, this.container.getProgessBarScaled(), 16);
	}

	@Override
	protected ResourceLocation getBackgroundTexture() {
		return TEXTURE;
	}
}
