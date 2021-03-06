package com.gluton.glutech.container.slot;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

/**
 * @author Gluton
 */
public class FuelSlot extends SlotItemHandler {

	public FuelSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
	   return super.isItemValid(stack) && isFuel(stack);
	}
	
	public int getItemStackLimit(ItemStack stack) {
	   return super.getItemStackLimit(stack);
	}
	
	public static boolean isFuel(ItemStack stack) {
		return net.minecraftforge.common.ForgeHooks.getBurnTime(stack) > 0;
	}
}
