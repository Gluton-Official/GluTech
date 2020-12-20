package com.gluton.glutech.client.renderer;

import java.util.EnumMap;
import java.util.function.Function;

import com.gluton.glutech.GluTech;
import com.gluton.glutech.blocks.properties.EnergyIOMode;
import com.gluton.glutech.tileentity.EnergyCellTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.world.LightType;
import net.minecraft.world.World;;

/**
 * @author Gluton
 */
public class EnergyCellRenderer extends TileEntityRenderer<EnergyCellTileEntity> {

	public static final EnumMap<EnergyIOMode, ResourceLocation> IO_TEXTURES;
//	public static final EnumMap<EnergyIOMode, ResourceLocation> IO_TEXTURES_ON;
	
	static {
		IO_TEXTURES = new EnumMap<>(EnergyIOMode.class);
		IO_TEXTURES.put(EnergyIOMode.NONE, new ResourceLocation(GluTech.MOD_ID, "block/io_none"));
		IO_TEXTURES.put(EnergyIOMode.INPUT, new ResourceLocation(GluTech.MOD_ID, "block/io_input"));
		IO_TEXTURES.put(EnergyIOMode.OUTPUT, new ResourceLocation(GluTech.MOD_ID, "block/io_output"));
		
//		IO_TEXTURES_ON = new EnumMap<>(EnergyIOMode.class);
//		IO_TEXTURES_ON.put(EnergyIOMode.NONE, new ResourceLocation(GluTech.MOD_ID, "block/energy_cell/energy_cell_on_none"));
//		IO_TEXTURES_ON.put(EnergyIOMode.INPUT, new ResourceLocation(GluTech.MOD_ID, "block/energy_cell/energy_cell_on_input"));
//		IO_TEXTURES_ON.put(EnergyIOMode.OUTPUT, new ResourceLocation(GluTech.MOD_ID, "block/energy_cell/energy_cell_on_output"));
	}
	
	private static final float OFFSET = 0.004f;
	
	public EnergyCellRenderer(TileEntityRendererDispatcher renderDispatcherIn) {
		super(renderDispatcherIn);
	}

	/**
	 * Referrenced {@link https://github.com/mekanism/Mekanism/blob/87babbbe75a02f16e5a992e3abfb6d22d0a0bbc1/src/main/java/mekanism/client/render/RenderResizableCuboid.java}
	 * Banners might be a good reference for model baking
	 * Some semi-helpful lighting documentation {@link http://greyminecraftcoder.blogspot.com/2013/08/lighting.html}
	 */
	@Override
	public void render(EnergyCellTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn,
			IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		Function<ResourceLocation, TextureAtlasSprite> atlasSpriteGetter = Minecraft.getInstance().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
		IVertexBuilder builder = bufferIn.getBuffer(RenderType.getTranslucent());
		World worldIn = tileEntityIn.getWorld();
		for (Direction side : Direction.values()) {
			BlockPos blockSidePos = tileEntityIn.getPos().offset(side);
			// checks if face should be culled
			if (!worldIn.getBlockState(blockSidePos).isSolidSide(worldIn.getBlockReader(blockSidePos.getX() % 16, blockSidePos.getZ() % 16), blockSidePos, side.getOpposite())) {
//				// Gets the texture that cooresponds to the energy and io mode
//				TextureAtlasSprite sprite = atlasSpriteGetter.apply(tileEntityIn.energy == 0
//						? IO_TEXTURES.get(tileEntityIn.energyIOConfig.get(side))
//						: IO_TEXTURES_ON.get(tileEntityIn.energyIOConfig.get(side)));
				float lightLevel = tileEntityIn.getWorld().getLightFor(LightType.SKY, blockSidePos);
				int light = (int) (0xF00000 * (lightLevel / 15));
				TextureAtlasSprite sprite = atlasSpriteGetter.apply(IO_TEXTURES.get(tileEntityIn.energyIOConfig.get(side)));
				matrixStackIn.push();
				MatrixStack.Entry lastMatrix = matrixStackIn.getLast();
				switch (side) {
					case DOWN:
						addVertex(builder, lastMatrix.getMatrix(), lastMatrix.getNormal(), Direction.DOWN, 0, 0 - OFFSET, 0, sprite.getMinU(), sprite.getMinV(), light, combinedOverlayIn);
						addVertex(builder, lastMatrix.getMatrix(), lastMatrix.getNormal(), Direction.DOWN, 1, 0 - OFFSET, 0, sprite.getMaxU(), sprite.getMinV(), light, combinedOverlayIn);
						addVertex(builder, lastMatrix.getMatrix(), lastMatrix.getNormal(), Direction.DOWN, 1, 0 - OFFSET, 1, sprite.getMaxU(), sprite.getMaxV(), light, combinedOverlayIn);
						addVertex(builder, lastMatrix.getMatrix(), lastMatrix.getNormal(), Direction.DOWN, 0, 0 - OFFSET, 1, sprite.getMinU(), sprite.getMaxV(), light, combinedOverlayIn);
						break;
					case UP:
						addVertex(builder, lastMatrix.getMatrix(), lastMatrix.getNormal(), Direction.UP, 0, 1 + OFFSET, 1, sprite.getMinU(), sprite.getMinV(), light, combinedOverlayIn);
						addVertex(builder, lastMatrix.getMatrix(), lastMatrix.getNormal(), Direction.UP, 1, 1 + OFFSET, 1, sprite.getMaxU(), sprite.getMinV(), light, combinedOverlayIn);
						addVertex(builder, lastMatrix.getMatrix(), lastMatrix.getNormal(), Direction.UP, 1, 1 + OFFSET, 0, sprite.getMaxU(), sprite.getMaxV(), light, combinedOverlayIn);
						addVertex(builder, lastMatrix.getMatrix(), lastMatrix.getNormal(), Direction.UP, 0, 1 + OFFSET, 0, sprite.getMinU(), sprite.getMaxV(), light, combinedOverlayIn);
						break;
					case NORTH:
						addVertex(builder, lastMatrix.getMatrix(), lastMatrix.getNormal(), Direction.NORTH, 0, 1 + OFFSET, 0 - OFFSET, sprite.getMinU(), sprite.getMinV(), light, combinedOverlayIn);
						addVertex(builder, lastMatrix.getMatrix(), lastMatrix.getNormal(), Direction.NORTH, 1, 1 + OFFSET, 0 - OFFSET, sprite.getMaxU(), sprite.getMinV(), light, combinedOverlayIn);
						addVertex(builder, lastMatrix.getMatrix(), lastMatrix.getNormal(), Direction.NORTH, 1, 0 + OFFSET, 0 - OFFSET, sprite.getMaxU(), sprite.getMaxV(), light, combinedOverlayIn);
						addVertex(builder, lastMatrix.getMatrix(), lastMatrix.getNormal(), Direction.NORTH, 0, 0 + OFFSET, 0 - OFFSET, sprite.getMinU(), sprite.getMaxV(), light, combinedOverlayIn);
						break;
					case EAST:
						addVertex(builder, lastMatrix.getMatrix(), lastMatrix.getNormal(), Direction.EAST, 1 + OFFSET, 1, 0, sprite.getMinU(), sprite.getMinV(), light, combinedOverlayIn);
						addVertex(builder, lastMatrix.getMatrix(), lastMatrix.getNormal(), Direction.EAST, 1 + OFFSET, 1, 1, sprite.getMaxU(), sprite.getMinV(), light, combinedOverlayIn);
						addVertex(builder, lastMatrix.getMatrix(), lastMatrix.getNormal(), Direction.EAST, 1 + OFFSET, 0, 1, sprite.getMaxU(), sprite.getMaxV(), light, combinedOverlayIn);
						addVertex(builder, lastMatrix.getMatrix(), lastMatrix.getNormal(), Direction.EAST, 1 + OFFSET, 0, 0, sprite.getMinU(), sprite.getMaxV(), light, combinedOverlayIn);
						break;
					case SOUTH:
						addVertex(builder, lastMatrix.getMatrix(), lastMatrix.getNormal(), Direction.SOUTH, 1, 1, 1 + OFFSET, sprite.getMinU(), sprite.getMinV(), light, combinedOverlayIn);
						addVertex(builder, lastMatrix.getMatrix(), lastMatrix.getNormal(), Direction.SOUTH, 0, 1, 1 + OFFSET, sprite.getMaxU(), sprite.getMinV(), light, combinedOverlayIn);
						addVertex(builder, lastMatrix.getMatrix(), lastMatrix.getNormal(), Direction.SOUTH, 0, 0, 1 + OFFSET, sprite.getMaxU(), sprite.getMaxV(), light, combinedOverlayIn);
						addVertex(builder, lastMatrix.getMatrix(), lastMatrix.getNormal(), Direction.SOUTH, 1, 0, 1 + OFFSET, sprite.getMinU(), sprite.getMaxV(), light, combinedOverlayIn);
						break;
					case WEST:
						addVertex(builder, lastMatrix.getMatrix(), lastMatrix.getNormal(), Direction.WEST, 0 - OFFSET, 1, 1, sprite.getMinU(), sprite.getMinV(), light, combinedOverlayIn);
						addVertex(builder, lastMatrix.getMatrix(), lastMatrix.getNormal(), Direction.WEST, 0 - OFFSET, 1, 0, sprite.getMaxU(), sprite.getMinV(), light, combinedOverlayIn);
						addVertex(builder, lastMatrix.getMatrix(), lastMatrix.getNormal(), Direction.WEST, 0 - OFFSET, 0, 0, sprite.getMaxU(), sprite.getMaxV(), light, combinedOverlayIn);
						addVertex(builder, lastMatrix.getMatrix(), lastMatrix.getNormal(), Direction.WEST, 0 - OFFSET, 0, 1, sprite.getMinU(), sprite.getMaxV(), light, combinedOverlayIn);
						break;
				}
				matrixStackIn.pop();
			}
		}
	}
	
//	private void addQuad(IVertexBuilder builder, Matrix4f matrix, Matrix3f normal, Direction face, TextureAtlasSprite sprite, int light, int overlay) {
//		Vector3i norm = face.getDirectionVec();
//		norm.
//		addVertex(builder, matrix, normal, face, 0, 0, 0, sprite.getMinU(), sprite.getMinV(), light, overlay);
//		addVertex(builder, matrix, normal, face, 1, 0, 0, sprite.getMaxU(), sprite.getMinV(), light, overlay);
//		addVertex(builder, matrix, normal, face, 1, 0, 1, sprite.getMaxU(), sprite.getMaxV(), light, overlay);
//		addVertex(builder, matrix, normal, face, 0, 0, 1, sprite.getMinU(), sprite.getMaxV(), light, overlay);
//	}
	
	private void addVertex(IVertexBuilder builder, Matrix4f matrix, Matrix3f normal, Direction face,
			float x, float y, float z, float u, float v, int light, int overlay) {
		switch (face) {
			case UP: break;
			case DOWN: light *= 0.1; break;
			case EAST: case WEST: light *= 0.35; break;
			case NORTH: case SOUTH: light *= 0.55; break;
		}
		builder.pos(matrix, x, y, z)
				.color(1.0f, 1.0f, 1.0f, 1.0f)
				.tex(u, v)
				.lightmap(light)
				.overlay(overlay)
				.normal(normal, face.getDirectionVec().getX(), face.getDirectionVec().getY(), face.getDirectionVec().getZ())
				.endVertex();
	}
}
