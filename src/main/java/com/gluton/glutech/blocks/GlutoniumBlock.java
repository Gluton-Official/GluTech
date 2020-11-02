package com.gluton.glutech.blocks;


import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

/**
 * @author Gluton
 */
public class GlutoniumBlock extends Block {

	public GlutoniumBlock() {
		super(Block.Properties.create(Material.IRON)
				.sound(SoundType.METAL)
				.hardnessAndResistance(5.0f, 6.0f)
				.setRequiresTool()
				.harvestTool(ToolType.PICKAXE)
				.harvestLevel(2)
		);
	}
}
