package com.gluton.glutech.tileentity;

import java.util.EnumMap;

import com.gluton.glutech.blocks.MachineBlock;
import com.gluton.glutech.blocks.properties.EnergyIOMode;
import com.gluton.glutech.registry.Registry;
import com.gluton.glutech.util.NBTUtils;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;

/**
 * @author Gluton
 */
public class EnergyCellTileEntity extends MachineTileEntity {
	
	public EnumMap<Direction, EnergyIOMode> energyIOConfig = new EnumMap<>(Direction.class);
	
	// TODO: add constants for all energy blocks --- or actually add it to IEnergyContainer/ nahhhh
	public static final int BASE_ENERGY = 0;
	public static final int CAPACITY = 100000;
	public static final int TRANSFER_IN = 1000;
	public static final int TRANSFER_OUT = 1000;
	
	public EnergyCellTileEntity() {
		super(Registry.ENERGY_CELL.getTileEntityType(), "energy_cell", BASE_ENERGY, CAPACITY, TRANSFER_IN, TRANSFER_OUT);
		
		for (Direction d : Direction.values()) {
			this.energyIOConfig.put(d, EnergyIOMode.NONE);
		}
	}

	@Override
	public boolean machineTick() {
		setPropertyState(MachineBlock.ON, this.energy > 0);
		if (this.energy > 0) {
			return transferEnergy();
		}
		return false;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		int energyReceived = super.receiveEnergy(maxReceive, simulate);
		if (!simulate && energyReceived > 0) {
			setPropertyState(MachineBlock.ON, this.energy > 0);
		}
		return energyReceived;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		int energyExtracted = super.extractEnergy(maxExtract, simulate);
		if (!simulate && energyExtracted > 0) {
			setPropertyState(MachineBlock.ON, this.energy > 0);
		}
		return energyExtracted;
	}

	@Override
	public boolean canExtract() {
		return true;
	}
	
	public void setEnergyIOConfig(EnumMap<Direction, EnergyIOMode> energyIOConfig) {
		this.energyIOConfig = energyIOConfig;
	}
	
	/**
	 * @return cloned energyIOConfig
	 */
	public EnumMap<Direction, EnergyIOMode> getEnergyIOConfg() {
		return this.energyIOConfig.clone();
	}
	
	@Override
	public EnergyIOMode getEnergyIOModeForSide(Direction side) {
		return energyIOConfig.get(side);
	}
	
	public void nextIOMode(Direction side) {
		this.energyIOConfig.put(side, this.energyIOConfig.get(side).nextMode());
	}
	
	@Override
	public void loadFromNBT(CompoundNBT nbt) {
		this.energy = nbt.getInt("Energy");
		for (Direction direction : Direction.values()) {
			this.energyIOConfig.put(direction, EnergyIOMode.values()[(int) nbt.getByte("EnergyIOConfig_" + direction.name())]);
		}
	}
	
	@Override
	public CompoundNBT saveToNBT(final CompoundNBT nbt) {
		NBTUtils.putOptionalInt(nbt, "Energy", this.energy, 0);
		
		this.energyIOConfig.forEach((direction, ioMode) -> {
			NBTUtils.putOptionalByte(nbt, "EnergyIOConfig_" + direction.name(), (byte) ioMode.ordinal(), (byte) 0);
		});
		
		return nbt;
	}
}
