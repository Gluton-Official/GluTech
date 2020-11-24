package com.gluton.glutech.container;

import java.util.Objects;

import com.gluton.glutech.container.slot.ResultSlot;
import com.gluton.glutech.tileentity.SintererTileEntity;
import com.gluton.glutech.util.FunctionalIntReferenceHolder;
import com.gluton.glutech.util.RegistryHandler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.SlotItemHandler;

/**
 * @author Gluton
 */
public class SintererContainer extends MachineContainer {
	
	private SintererTileEntity tileEntity;
	private IWorldPosCallable canInteractWithCallable;
	public FunctionalIntReferenceHolder currentProcessTime;

	// Server
	public SintererContainer(final int windowId, final PlayerInventory playerInv, final SintererTileEntity tile) {
		super(RegistryHandler.SINTERER_CONTAINER.get(), windowId, 3, 30);
		
		this.tileEntity = tile;
		this.canInteractWithCallable = IWorldPosCallable.of(tile.getWorld(), tile.getPos());
		
		final int slotSizePlus2 = 18;
		final int startX = 8;
		
		// Crusher slots
		this.addSlot(new SlotItemHandler(tile.getInventory(), 0, 56, 25));
		this.addSlot(new SlotItemHandler(tile.getInventory(), 1, 56, 25 + slotSizePlus2));
		this.addSlot(new ResultSlot(tile.getInventory(), 2, 116, 35));
		
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
		
		this.trackInt(currentProcessTime = new FunctionalIntReferenceHolder(() -> this.tileEntity.currentProcessTime,
				value -> this.tileEntity.currentProcessTime = value));
	}
	
	// Client
	public SintererContainer(final int windowId, final PlayerInventory playerInv, final PacketBuffer data) {
		this(windowId, playerInv, getTileEntity(playerInv, data));
	}
	
	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return isWithinUsableDistance(canInteractWithCallable, playerIn, RegistryHandler.SINTERER_BLOCK.get());
	}

	private static SintererTileEntity getTileEntity(final PlayerInventory playerInv, final PacketBuffer data) {
		Objects.requireNonNull(playerInv, "playerInv cannot be null");
		Objects.requireNonNull(data, "data cannot be null");
		final TileEntity tileAtPos = playerInv.player.world.getTileEntity(data.readBlockPos());
		if (!(tileAtPos instanceof SintererTileEntity)) {
			throw new IllegalStateException("TileEntity is not correct " + tileAtPos);
		}
		return (SintererTileEntity) tileAtPos;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public int getSmeltProgressionScaled() {
		return this.currentProcessTime.get() != 0 && this.tileEntity.maxProcessTime != 0
				? this.currentProcessTime.get() * 24 / this.tileEntity.maxProcessTime : 0;
	}
}
