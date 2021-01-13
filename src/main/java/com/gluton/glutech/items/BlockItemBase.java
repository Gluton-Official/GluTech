package com.gluton.glutech.items;

import com.gluton.glutech.GluTech;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

/**
 * @author Gluton
 */
public class BlockItemBase extends BlockItem {

	public BlockItemBase(Block block, Properties properties) {
		super(block, properties);
	}
	
	public BlockItemBase(Block block) {
		this(block, new Item.Properties().group(GluTech.TAB));
	}
}
