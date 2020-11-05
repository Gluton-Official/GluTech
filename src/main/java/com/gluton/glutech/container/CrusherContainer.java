package com.gluton.glutech.container;

import java.util.Objects;

import com.gluton.glutech.GluTech;
import com.gluton.glutech.tileentity.CrusherTileEntity;
import com.gluton.glutech.util.FunctionalIntReferenceHolder;
import com.gluton.glutech.util.RegistryHandler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.SlotItemHandler;

/**
 * @author Gluton
 */
public class CrusherContainer extends Container {
	
	private CrusherTileEntity tileEntity;
	private IWorldPosCallable canInteractWithCallable;
	public FunctionalIntReferenceHolder currentSmeltTime;
	
	private final int inventoryIndex = 2;
	private final int hotbarIndex = 29;

	// Server
	public CrusherContainer(final int windowId, final PlayerInventory playerInv, final CrusherTileEntity tile) {
		super(RegistryHandler.CRUSHER_CONTAINER.get(), windowId);
		
		this.tileEntity = tile;
		this.canInteractWithCallable = IWorldPosCallable.of(tile.getWorld(), tile.getPos());
		
		final int slotSizePlus2 = 18;
		final int startX = 8;
		
		// Crusher slots
		this.addSlot(new SlotItemHandler(tile.getInventory(), 0, 56, 35));
		this.addSlot(new ResultSlot(tile.getInventory(), 1, 116, 35));
		
		// Main player inventory
		final int startY = 84;
		
		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 9; column++) {
				this.addSlot(new Slot(playerInv, 9 + row * 9 + column, startX + (column * slotSizePlus2), startY + (row * slotSizePlus2)));
			}
		}
		
		// Hotbar
		int hotbarY = 142;
		for (int column = 0; column < 9; column++) {
			this.addSlot(new Slot(playerInv, column, startX + (column * slotSizePlus2), hotbarY));
		}
		
		this.trackInt(currentSmeltTime = new FunctionalIntReferenceHolder(() -> this.tileEntity.currentSmeltTime,
				value -> this.tileEntity.currentSmeltTime = value));
	}

	// Client
	public CrusherContainer(final int windowId, final PlayerInventory playerInv, final PacketBuffer data) {
		this(windowId, playerInv, getTileEntity(playerInv, data));
	}
	
	private static CrusherTileEntity getTileEntity(final PlayerInventory playerInv, final PacketBuffer data) {
		Objects.requireNonNull(playerInv, "playerInv cannot be null");
		Objects.requireNonNull(data, "data cannot be null");
		final TileEntity tileAtPos = playerInv.player.world.getTileEntity(data.readBlockPos());
		if (tileAtPos instanceof CrusherTileEntity) {
			return (CrusherTileEntity) tileAtPos;
		}
		throw new IllegalStateException("TileEntity is not correct " + tileAtPos);
	}
	
	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return isWithinUsableDistance(canInteractWithCallable, playerIn, RegistryHandler.CRUSHER_BLOCK.get());
	}
	
	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int index) {
		ItemStack returnStack = ItemStack.EMPTY;
		final Slot slot = this.inventorySlots.get(index);
		if (slot != null && slot.getHasStack()) {			
			final ItemStack slotStack = slot.getStack();
			returnStack = slotStack.copy();
			
//			final int containerSlots = this.inventorySlots.size() - player.inventory.mainInventory.size();
			if (index < inventoryIndex) {
				// from Crushser into inventory
				if (!mergeItemStack(slotStack, inventoryIndex, this.inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			// from inventory into Crusher
			} else if (!mergeItemStack(slotStack, 0, 1, false)) {	
				if (index < hotbarIndex) {
					// from player inventory into hotbar
					if (!mergeItemStack(slotStack, hotbarIndex, this.inventorySlots.size(), false)) {
						return ItemStack.EMPTY;
					}
				// from hotbar into player inventory
				} else if (index < this.inventorySlots.size() && !mergeItemStack(slotStack, inventoryIndex, hotbarIndex, false)) {
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
	public int getSmeltProgressionScaled() {
		return this.currentSmeltTime.get() != 0 && this.tileEntity.maxSmeltTime != 0
				? this.currentSmeltTime.get() * 24 / this.tileEntity.maxSmeltTime : 0;
	}
	
	private boolean mergeToInput(ItemStack stack) {
		Slot slot = this.inventorySlots.get(0);
        ItemStack itemstack = slot.getStack();
        if (itemstack.isEmpty()) {
        	slot.putStack(stack.split(Math.min(stack.getCount(), slot.getSlotStackLimit())));
        } else if (itemstack.getCount() != itemstack.getMaxStackSize() && areItemsAndTagsEqual(stack, itemstack)) {
           int j = itemstack.getCount() + stack.getCount();
           int maxSize = Math.min(slot.getSlotStackLimit(), stack.getMaxStackSize());
           if (j <= maxSize) {
              stack.setCount(0);
              itemstack.setCount(j);
           } else if (itemstack.getCount() < maxSize) {
              stack.shrink(maxSize - itemstack.getCount());
              itemstack.setCount(maxSize);
           }
        } else {
        	return false;
        }
        slot.onSlotChanged();
        return true;
	}
}
