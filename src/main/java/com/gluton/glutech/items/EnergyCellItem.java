package com.gluton.glutech.items;

import com.gluton.glutech.GluTech;
import com.gluton.glutech.registry.Registry;
import com.gluton.glutech.tileentity.EnergyCellTileEntity;

import net.minecraft.block.Block;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;

/**
 * @author Gluton
 */
public class EnergyCellItem extends EnergyBlockItem {

	// TODO: add ISTER to the properties with setISTER()
	public EnergyCellItem(Block block) {
		super(block, EnergyCellTileEntity.CAPACITY);
	}
	
	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		super.fillItemGroup(group, items);
		if (group == GluTech.TAB) {
			CompoundNBT nbt = new CompoundNBT();
			nbt.putInt("Energy", EnergyCellTileEntity.CAPACITY);
			ItemStack itemStack = new ItemStack(Registry.ENERGY_CELL.getItem());
			itemStack.setTagInfo("BlockEntityTag", nbt);
			items.add(itemStack);
		}
	}
}
