package com.gluton.glutech.container;

import java.util.Objects;

import com.gluton.glutech.container.slot.ResultSlot;
import com.gluton.glutech.registry.Registry;
import com.gluton.glutech.tileentity.MachineTileEntity;
import com.gluton.glutech.tileentity.SintererTileEntity;
import com.gluton.glutech.util.FunctionalIntReferenceHolder;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
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
	
	public static final int SLOTS = 3;

	// Server
	public SintererContainer(final int windowId, final PlayerInventory playerInv, final SintererTileEntity tile) {
		super(Registry.SINTERER.getContainerType(), tile, windowId, SLOTS);
		
		this.tileEntity = tile;
		this.canInteractWithCallable = IWorldPosCallable.of(tile.getWorld(), tile.getPos());
		
		this.addSlot(new SlotItemHandler(tile.getInventory(), 0, 56, 25));
		this.addSlot(new SlotItemHandler(tile.getInventory(), 1, 56, 25 + SLOT_SIZE_PLUS_2));
		this.addSlot(new ResultSlot(tile.getInventory(), 2, 116, 35));
		
		this.addPlayerInventory(playerInv);
		
		this.trackInt(this.currentProcessTime = new FunctionalIntReferenceHolder(
				() -> this.tileEntity.getCurrentProcessTime(),
				value -> this.tileEntity.setCurrentProcessTime(value)));
	}
	
	// Client
	public SintererContainer(final int windowId, final PlayerInventory playerInv, final PacketBuffer data) {
		this(windowId, playerInv, getTileEntity(playerInv, data));
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
	
	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return isWithinUsableDistance(canInteractWithCallable, playerIn, Registry.SINTERER.getBlock());
	}

	@OnlyIn(Dist.CLIENT)
	public int getProgessBarScaled() {
		return this.currentProcessTime.get() != 0 && this.tileEntity.getMaxProcessTime() != 0
				? this.currentProcessTime.get() * 24 / this.tileEntity.getMaxProcessTime() : 0;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends MachineTileEntity> T getTileEntity() {
		return (T) tileEntity;
	}
}
