package com.gluton.glutech.items;

import com.gluton.glutech.GluTech;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

/**
 * @author Gluton
 */
public class EnergyBlockItem extends BlockItemBase {
	
	private final int capacity;
	
	private static final int GREEN = 0x0000ff00;
	
	public EnergyBlockItem(Block block, Properties properties, int capacity) {
		super(block, properties);
		
		this.capacity = capacity;
	}
	
	public EnergyBlockItem(Block block, int capacity) {
		this(block, new Item.Properties().group(GluTech.TAB), capacity);
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return stack.getChildTag("BlockEntityTag") != null;
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		CompoundNBT tag = stack.getChildTag("BlockEntityTag");
		if (tag != null && tag.contains("Energy")) {
			return 1 - (tag.getInt("Energy") / (double) capacity);
		} else {
			return 1;
		}
	}
	
	@Override
	public int getItemStackLimit(ItemStack stack) {
		return stack.hasTag() ? 1 : 64;
	}
	
	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack) {
		return GREEN;
	}
}
