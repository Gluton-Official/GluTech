package com.gluton.glutech.items;

import javax.annotation.Nullable;

import com.gluton.glutech.GluTech;
import com.gluton.glutech.capabilities.EnergyItemProvider;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

/**
 * @author Gluton
 */
public class EnergyBlockItem extends BlockItemBase implements IEnergyItem  {

	private final int capacity;
	
	public EnergyBlockItem(Block block, Properties properties, int capacity) {
		super(block, properties);
		
		this.capacity = capacity;
	}
	
	public EnergyBlockItem(Block block, int capacity) {
		this(block, new Item.Properties().group(GluTech.TAB), capacity);
	}
	
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return new EnergyItemProvider(stack, null, 
				itemStack -> itemStack.getOrCreateChildTag("BlockEntityTag"), this.capacity, false, true);
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
		return 1;
	}
	
	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack) {
		return GREEN;
	}
}
