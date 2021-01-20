package com.gluton.glutech.container.slot;

import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

/**
 * @author Gluton
 *
 */
public class ChargeSlot extends SlotItemHandler {

	public ChargeSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return super.isItemValid(stack) && stack.getCapability(CapabilityEnergy.ENERGY).isPresent();
	}
	
	@Override
	public int getSlotStackLimit() {
		return 1;
	}
}
