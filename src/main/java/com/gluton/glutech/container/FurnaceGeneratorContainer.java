package com.gluton.glutech.container;

import java.util.Objects;

import com.gluton.glutech.container.slot.FuelSlot;
import com.gluton.glutech.tileentity.FurnaceGeneratorTileEntity;
import com.gluton.glutech.util.FunctionalIntReferenceHolder;
import com.gluton.glutech.util.RegistryHandler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Gluton
 */
public class FurnaceGeneratorContainer extends MachineContainer {

	private FurnaceGeneratorTileEntity tileEntity;
	private IWorldPosCallable canInteractWithCallable;
	public FunctionalIntReferenceHolder currentProcessTime;
	public FunctionalIntReferenceHolder fuelBurnTime;
	
	public static final int SLOTS = 1;

	// Server
	public FurnaceGeneratorContainer(final int windowId, final PlayerInventory playerInv, final FurnaceGeneratorTileEntity tile) {
		super(RegistryHandler.FURNACE_GENERATOR_CONTAINER.get(), windowId, SLOTS);
		
		this.tileEntity = tile;
		this.canInteractWithCallable = IWorldPosCallable.of(tile.getWorld(), tile.getPos());
		
		this.addSlot(new FuelSlot(tile.getInventory(), 0, 80, 45));
		
		this.addPlayerInventory(playerInv);
		
		this.trackInt(currentProcessTime = new FunctionalIntReferenceHolder(() -> this.tileEntity.currentProcessTime,
				value -> this.tileEntity.currentProcessTime = value));
		this.trackInt(fuelBurnTime = new FunctionalIntReferenceHolder(() -> this.tileEntity.fuelBurnTime,
				value -> this.tileEntity.fuelBurnTime = value));
	}
	
	// Client
	public FurnaceGeneratorContainer(final int windowId, final PlayerInventory playerInv, final PacketBuffer data) {
		this(windowId, playerInv, getTileEntity(playerInv, data));
	}
	
	private static FurnaceGeneratorTileEntity getTileEntity(final PlayerInventory playerInv, final PacketBuffer data) {
		Objects.requireNonNull(playerInv, "playerInv cannot be null");
		Objects.requireNonNull(data, "data cannot be null");
		final TileEntity tileAtPos = playerInv.player.world.getTileEntity(data.readBlockPos());
		if (!(tileAtPos instanceof FurnaceGeneratorTileEntity)) {
			throw new IllegalStateException("TileEntity is not correct " + tileAtPos);
		}
		return (FurnaceGeneratorTileEntity) tileAtPos;
	}
	
	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return isWithinUsableDistance(canInteractWithCallable, playerIn, RegistryHandler.FURNACE_GENERATOR_BLOCK.get());
	}
	
	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int index) {
		ItemStack returnStack = ItemStack.EMPTY;
		final Slot slot = this.inventorySlots.get(index);
		if (slot != null && slot.getHasStack()) {			
			final ItemStack slotStack = slot.getStack();
			returnStack = slotStack.copy();
			
			if (index < this.inventoryIndex) {
				if (!mergeItemStack(slotStack, this.inventoryIndex, this.inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (FuelSlot.isFuel(slotStack) || FuelSlot.isBucket(slotStack)) {
				if (!mergeItemStack(slotStack, 0, this.inventoryIndex, false)) {	
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
	@Override
	public int getSmeltProgressionScaled() {
		return fuelBurnTime.get() == 0 ? 0 : currentProcessTime.get() * 13 / fuelBurnTime.get();
	}
	
	public FurnaceGeneratorTileEntity getTileEntity() {
		return tileEntity;
	}
}