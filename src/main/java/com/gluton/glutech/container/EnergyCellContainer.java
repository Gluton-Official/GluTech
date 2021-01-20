package com.gluton.glutech.container;

import java.util.Objects;

import com.gluton.glutech.container.slot.ChargeSlot;
import com.gluton.glutech.registry.Registry;
import com.gluton.glutech.tileentity.EnergyCellTileEntity;
import com.gluton.glutech.tileentity.MachineTileEntity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;

/**
 * @author Gluton
 */
public class EnergyCellContainer extends MachineContainer {

	private EnergyCellTileEntity tileEntity;
	private IWorldPosCallable canInteractWithCallable;
	
	public static final int SLOTS = 1;
	
	public EnergyCellContainer(final int windowId, final PlayerInventory playerInv, final EnergyCellTileEntity tile) {
		super(Registry.ENERGY_CELL.getContainerType(), tile, windowId, SLOTS);
		
		this.tileEntity = tile;
		this.canInteractWithCallable = IWorldPosCallable.of(tile.getWorld(), tile.getPos());
		
		this.addSlot(new ChargeSlot(this.tileEntity.getInventory(), 0, 80, 35));
		
		this.addPlayerInventory(playerInv);
	}
	
	// Client
	public EnergyCellContainer(final int windowId, final PlayerInventory playerInv, final PacketBuffer data) {
		this(windowId, playerInv, readTileEntity(playerInv, data));
	}
	
	private static EnergyCellTileEntity readTileEntity(final PlayerInventory playerInv, final PacketBuffer data) {
		Objects.requireNonNull(playerInv, "playerInv cannot be null");
		Objects.requireNonNull(data, "data cannot be null");
		final TileEntity tileAtPos = playerInv.player.world.getTileEntity(data.readBlockPos());
		if (!(tileAtPos instanceof EnergyCellTileEntity)) {
			throw new IllegalStateException("TileEntity is not correct " + tileAtPos);
		}
		return (EnergyCellTileEntity) tileAtPos;
	}
	
	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return isWithinUsableDistance(canInteractWithCallable, playerIn, Registry.ENERGY_CELL.getBlock());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends MachineTileEntity> T getTileEntity() {
		return (T) tileEntity;
	}

}
