package com.gluton.glutech.tileentity;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.gluton.glutech.blocks.properties.EnergyIOMode;
import com.gluton.glutech.capabilities.CapabilityCallable;
import com.gluton.glutech.util.NBTUtils;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.Property;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * @author Gluton
 */
public abstract class MachineTileEntity extends TileEntity implements ITickableTileEntity, IEnergyContainer {

	protected String name;

	public int energy;
	public int capacity;
	public int maxReceive;
	public int maxExtract;
	
	protected Map<Capability<?>, CapabilityCallable<?>> capabilities = new HashMap<>();
	
	public MachineTileEntity(TileEntityType<?> tileEntityTypeIn, String name,
			int defaultEnergy, int capacity, int maxReceive, int maxExtract) {
		super(tileEntityTypeIn);
		
		this.name = name;
		
		this.energy = defaultEnergy;
		this.capacity = capacity;
		this.maxReceive = maxReceive;
		this.maxExtract = maxExtract;
		
		capabilities.put(CapabilityEnergy.ENERGY, new CapabilityCallable<IEnergyStorage>(this) {
			@Override
			public LazyOptional<IEnergyStorage> side(Direction side) {
				if (getEnergyIOModeForSide(side) == EnergyIOMode.INPUT) {
					return LazyOptional.of(() -> this.storage); 
				}
				return LazyOptional.empty();
			}
		});
	}
	
	@Override
	public final void tick() {
		if (this.world != null && !this.world.isRemote) {
			if (machineTick()) {
				this.markDirty();
				// TODO: notify neighbors should maybe be called within machineTick()
				notifyBlockUpdate(Constants.BlockFlags.NOTIFY_NEIGHBORS);
			}
		}
		// TODO: halt() method here?
	}
	
	/**
	 * @return true if dirty
	 */
	public abstract boolean machineTick();
	
	public void notifyBlockUpdate(int flags) {
		this.world.notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), flags);
	}
	
	public <T extends Comparable<T>, V extends T> void setPropertyState(Property<T> property, V value) {
		this.world.setBlockState(this.getPos(), this.getBlockState().with(property, value));
	}
	
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		if (!canReceive()) {
			return 0;
		}
		
		int energyReceived = Math.min(this.capacity - this.energy, Math.min(this.maxReceive, maxReceive));
		if (!simulate && energyReceived > 0) {
			this.energy += energyReceived;
			this.markDirty();
			notifyBlockUpdate(Constants.BlockFlags.BLOCK_UPDATE);
		}
		return energyReceived;
	}
	
	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		if (!canExtract()) {
			return 0;
		}
		
		int energyExtracted = Math.min(this.energy, Math.min(this.maxExtract, maxExtract));
		if (!simulate && energyExtracted > 0) {
			this.energy -= energyExtracted;
			this.markDirty();
			notifyBlockUpdate(Constants.BlockFlags.BLOCK_UPDATE);
		}
		return energyExtracted;
	}
	
	/**
	 * @return true if dirty
	 */
	public boolean transferEnergy() {
		boolean dirty = false;
		for (Direction side : Direction.values()) {
			if (canExtractFromFace(side)) {
				TileEntity tile = this.world.getTileEntity(this.pos.offset(side));
				if (tile != null) {
					IEnergyStorage energyTile = tile.getCapability(CapabilityEnergy.ENERGY, side.getOpposite()).orElse(null);
					if (energyTile != null) {
						int energyToTransfer = this.extractEnergy(this.maxExtract, true);
						// TODO: should receiving energy force block update?
						int energyTransfered = energyTile.receiveEnergy(energyToTransfer, false);
						if (energyTransfered > 0) {
							this.energy -= energyTransfered;
							dirty = true;
							notifyBlockUpdate(Constants.BlockFlags.BLOCK_UPDATE);
						}
					}
				}
			}
		}
		return dirty;
	}
	
	@Override
	public void setEnergyStored(int energy) {
		this.energy = energy;
	}
	
	@Override
	public int getEnergyStored() {
		return this.energy;
	}
	
	@Override
	public void setMaxEnergyStored(int capacity) {
		this.capacity = capacity;
	}
	
	@Override
	public int getMaxEnergyStored() {
		return this.capacity;
	}
	
	public boolean isRedstonePowered() {
		return this.world.isBlockPowered(this.getPos());
	}
	
	@Override
	public final <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (capabilities.containsKey(cap)) {
			return capabilities.get(cap).side(side).cast();
		} else {
			return super.getCapability(cap, side);
		}
	}
	
	@Override
	public final void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		loadFromNBT(nbt);
	}
	
	public void loadFromNBT(CompoundNBT nbt) {
		this.energy = nbt.getInt("Energy");
	}
	
	@Override
	public final CompoundNBT write(CompoundNBT nbt) {
		nbt = super.write(nbt);
		return saveToNBT(nbt);
	}
	
	public CompoundNBT saveToNBT(final CompoundNBT nbt) {
		NBTUtils.putOptionalInt(nbt, "Energy", this.energy, 0);
		return nbt;
	}
	
	@Nullable
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		this.write(nbt);
		return new SUpdateTileEntityPacket(this.pos, 0, nbt);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.read(this.getBlockState(), pkt.getNbtCompound());
	}
	
	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = new CompoundNBT();
		this.write(nbt);
		return nbt;
	}
	
	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT nbt) {
		this.read(state, nbt);
	}
}
