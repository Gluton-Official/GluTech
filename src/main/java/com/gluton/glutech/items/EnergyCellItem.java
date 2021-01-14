package com.gluton.glutech.items;

import com.gluton.glutech.GluTech;
import com.gluton.glutech.registry.Registry;
import com.gluton.glutech.tileentity.EnergyCellTileEntity;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;

/**
 * @author Gluton
 */
public class EnergyCellItem extends BlockItemBase {

	public EnergyCellItem(Block block) {
		super(block, new Item.Properties().group(GluTech.TAB));
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return stack.getChildTag("BlockEntityTag") != null;
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		CompoundNBT tag = stack.getChildTag("BlockEntityTag");
		if (tag != null && tag.contains("Energy")) {
			return 1 - (tag.getInt("Energy") / (double) EnergyCellTileEntity.CAPACITY);
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
		return 0x0000ff00;
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
