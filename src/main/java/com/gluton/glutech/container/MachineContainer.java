package com.gluton.glutech.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Gluton
 *
 */
public abstract class MachineContainer extends Container {
	
	private final int inventoryIndex;
	private final int hotbarIndex;

	public MachineContainer(ContainerType<?> type, int id, final int inventoryIndex, final int hotbarIndex) {
		super(type, id);
		this.inventoryIndex = inventoryIndex;
		this.hotbarIndex = hotbarIndex;
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return false;
	}
	
	/**
	 * Runs repeatedly (every tick?) while shift + left click is held down until slot is empty or unable to move stack
	 */
	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int index) {
		ItemStack returnStack = ItemStack.EMPTY;
		final Slot slot = this.inventorySlots.get(index);
		if (slot != null && slot.getHasStack()) {			
			final ItemStack slotStack = slot.getStack();
			returnStack = slotStack.copy();
			
			if (index < this.inventoryIndex) {
				// from machien into inventory
				if (!mergeItemStack(slotStack, this.inventoryIndex, this.inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			// from inventory into machine
			} else if (!mergeItemStack(slotStack, 0, this.inventoryIndex, false)) {	
				if (index < hotbarIndex) {
					// from player inventory into hotbar
					if (!mergeItemStack(slotStack, this.hotbarIndex, this.inventorySlots.size(), false)) {
						return ItemStack.EMPTY;
					}
				// from hotbar into player inventory
				} else if (index < this.inventorySlots.size()
						&& !mergeItemStack(slotStack, this.inventoryIndex, this.hotbarIndex, false)) {
					return ItemStack.EMPTY;
				} else {
					return ItemStack.EMPTY;
				}
			}
			if (slotStack.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
			if (slotStack.getCount() == returnStack.getCount()) {
				return ItemStack.EMPTY;
			}
			slot.onTake(player, slotStack);
		}
		return returnStack;
	}
	
	@OnlyIn(Dist.CLIENT)
	public abstract int getSmeltProgressionScaled();
}
