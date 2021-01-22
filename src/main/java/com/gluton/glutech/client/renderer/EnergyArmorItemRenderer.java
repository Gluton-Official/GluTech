package com.gluton.glutech.client.renderer;

import com.gluton.glutech.items.EnergyArmorItem;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;

/**
 * @author Gluton
 */
public class EnergyArmorItemRenderer extends ItemRenderer {

	public EnergyArmorItemRenderer(TextureManager textureManagerIn, ModelManager modelManagerIn,
			ItemColors itemColorsIn) {
		super(textureManagerIn, modelManagerIn, itemColorsIn);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void renderItemOverlayIntoGUI(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, String text) {
		super.renderItemOverlayIntoGUI(fr, stack, xPosition, yPosition, text);
		
		if (stack.getItem() instanceof EnergyArmorItem) {
			EnergyArmorItem item = (EnergyArmorItem) stack.getItem();
			RenderSystem.disableDepthTest();
	        RenderSystem.disableTexture();
	        RenderSystem.disableAlphaTest();
	        RenderSystem.disableBlend();
	        Tessellator tessellator = Tessellator.getInstance();
	        BufferBuilder bufferbuilder = tessellator.getBuffer();
	        double health = item.getDurabilityForDisplay(stack);
	        int i = Math.round(13.0F - (float)health * 13.0F);
	        int j = item.getRGBDurabilityForDisplay(stack);
	        this.draw(bufferbuilder, xPosition + 2, yPosition + 13, 13, 2, 0, 0, 0, 255);
	        this.draw(bufferbuilder, xPosition + 2, yPosition + 13, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);
	        RenderSystem.enableBlend();
	        RenderSystem.enableAlphaTest();
	        RenderSystem.enableTexture();
	        RenderSystem.enableDepthTest();
		}
	}
	
	private void draw(BufferBuilder renderer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
		renderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		renderer.pos((double)(x + 0), (double)(y + 0), 0.0D).color(red, green, blue, alpha).endVertex();
		renderer.pos((double)(x + 0), (double)(y + height), 0.0D).color(red, green, blue, alpha).endVertex();
		renderer.pos((double)(x + width), (double)(y + height), 0.0D).color(red, green, blue, alpha).endVertex();
		renderer.pos((double)(x + width), (double)(y + 0), 0.0D).color(red, green, blue, alpha).endVertex();
		Tessellator.getInstance().draw();
	}
}
