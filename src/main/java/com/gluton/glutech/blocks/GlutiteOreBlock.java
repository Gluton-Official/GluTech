package com.gluton.glutech.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.OreBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.ToolType;

/**
 * @author Gluton
 */
public class GlutiteOreBlock extends OreBlock {

	public GlutiteOreBlock() {
		super(Block.Properties.create(Material.ROCK)
				.sound(SoundType.STONE)
				.hardnessAndResistance(3.0f, 4.0f)
				.setRequiresTool()
				.harvestTool(ToolType.PICKAXE)
				.harvestLevel(2));
	}
	
	@Override
	public int getExpDrop(BlockState state, IWorldReader reader, BlockPos pos, int fortune, int silktouch) {
		return silktouch == 0 ? MathHelper.nextInt(RANDOM, 3, 7) : 0;
	}
}
