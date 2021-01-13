package com.gluton.glutech.client.gui;

import com.gluton.glutech.GluTech;
import com.gluton.glutech.container.CrusherContainer;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

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
}
