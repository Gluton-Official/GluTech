package com.gluton.glutech.container;

import com.gluton.glutech.tileentity.MachineTileEntity;
import com.gluton.glutech.util.FunctionalIntReferenceHolder;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * TODO: rename to "Container" or smthn (or not now since this now tracks energy)
 * 
 * @author Gluton
 */
public abstract class MachineContainer extends Container {
	
	public FunctionalIntReferenceHolder energy;
	public FunctionalIntReferenceHolder capacity;
	
	protected final int inventoryIndex;
	protected final int hotbarIndex;
	
	protected static final int SLOT_SIZE_PLUS_2 = 18;
	private static final int START_X = 8;
	private static final int START_Y = 84;
	private static final int HOTBAR_Y = 142;

	public MachineContainer(ContainerType<?> type, MachineTileEntity tile, int id, final int inventoryIndex) {
		super(type, id);
		this.inventoryIndex = inventoryIndex;
		this.hotbarIndex = inventoryIndex + 27; // offsets hotbar index by number of inventory slots
		
		this.trackInt(this.energy = new FunctionalIntReferenceHolder(
				() -> tile.getEnergyStored(),
				value -> tile.setEnergyStored(value)));
		this.trackInt(this.capacity = new FunctionalIntReferenceHolder(
				() -> tile.getMaxEnergyStored(),
				value -> tile.setMaxEnergyStored(value)));
	}
	
	protected void addPlayerInventory(final PlayerInventory playerInv) { 
		// Player inventory
		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 9; column++) {
				this.addSlot(new Slot(playerInv, 9 + row * 9 + column, START_X + (column * SLOT_SIZE_PLUS_2), START_Y + (row * SLOT_SIZE_PLUS_2)));
			}
		}
		
		// Hotbar
		for (int column = 0; column < 9; column++) {
			this.addSlot(new Slot(playerInv, column, START_X + (column * SLOT_SIZE_PLUS_2), HOTBAR_Y));
		}
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
				// from machine into inventory
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
	public int getEnergyBarScaled() {
		return  this.capacity.get() == 0 ? 0 : this.energy.get() * 48 / this.capacity.get();
	}
	
	public abstract <T extends MachineTileEntity> T getTileEntity();
}
