package com.gluton.glutech.blocks;

import com.gluton.glutech.GluTech;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

/**
 * @author Gluton
 */
public class BlockItemBase extends BlockItem {

	public BlockItemBase(Block block) {
		super(block, new Item.Properties().group(GluTech.TAB));
	}

}
